/*
 * Copyright (c) 2009-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.planet57.gshell.shell;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;
import org.jline.reader.impl.DefaultParser;
import org.sonatype.goodies.common.ComponentSupport;

import com.google.common.collect.Lists;
import com.planet57.gshell.command.CommandAction;
import com.planet57.gshell.command.resolver.CommandResolver;
import com.planet57.gshell.command.resolver.Node;

/**
 * Shell {@link Completer}.
 *
 * @since 3.0
 */
@Named("main")
public class ShellCompleter
  extends ComponentSupport
  implements Completer
{
  private final CommandResolver commandResolver;

  private final Completer aliasNameCompleter;

  private final Completer commandNameCompleter;

  @Inject
  public ShellCompleter(final CommandResolver commandResolver,
                        @Named("alias-name") final Completer aliasName,
                        @Named("command-name") final Completer commandName)
  {
    this.commandResolver = checkNotNull(commandResolver);
    this.aliasNameCompleter = checkNotNull(aliasName);
    this.commandNameCompleter = checkNotNull(commandName);
  }

  @Override
  public void complete(final LineReader reader, final ParsedLine line, final List<Candidate> candidates) {
    explain("Parsed-line", line);

    if (line.wordIndex() == 0) {
      aliasNameCompleter.complete(reader, line, candidates);
      commandNameCompleter.complete(reader, line, candidates);
    }
    else {
      String command = line.words().get(0);
      log.debug("Command: {}", command);

      // resolve node for command, this should be non-null?
      Node node = commandResolver.resolve(command);
      log.debug("Node: {}", node);

      if (node != null) {
        CommandAction action = node.getAction();

        Completer completer = action.getCompleter();
        if (completer == null) {
          log.debug("Action has no specific completer; skipping");
          return;
        }
        log.debug("Completer: {}", completer);

        // HACK: complexity here to re-use ArgumentCompleter; not terribly efficient
        ParsedLine arguments = extractCommandArguments(line);
        explain("Command-arguments", arguments);

        completer.complete(reader, arguments, candidates);
      }
    }
  }

  /**
   * Helper to log {@link ParsedLine} details.
   */
  private void explain(final String message, final ParsedLine line) {
    // HACK: ParsedLine has no sane toString(); render all its details to logging
    log.debug("{}: line={}, words={}, wordIndex: {}, wordCursor: {}, cursor: {}",
      message,
      line.line(),
      line.words(),
      line.wordIndex(),
      line.wordCursor(),
      line.cursor()
    );
  }

  /**
   * Extract the command specific portions of the given line.
   *
   * This is everything past the first word.
   */
  private static ParsedLine extractCommandArguments(final ParsedLine line) {
    // copy the list, so we can mutate and pop the first item off
    LinkedList<String> words = Lists.newLinkedList(line.words());
    String remove = words.pop();

    String rawLine = line.line();
    return new DefaultParser.ArgumentList(
      rawLine.substring(remove.length() + 1, rawLine.length()),
      words,
      line.wordIndex() - 1,
      line.wordCursor(),
      line.cursor() - remove.length() + 1
    );
  }
}
