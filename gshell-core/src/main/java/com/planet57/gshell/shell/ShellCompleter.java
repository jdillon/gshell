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

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;
import org.jline.reader.impl.DefaultParser;
import org.jline.reader.impl.completer.ArgumentCompleter;
import org.jline.reader.impl.completer.NullCompleter;
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
    // HACK: object has no reasonable toString
    log.info("Parsed-line: line={}, words={}, wordIndex: {}, wordCursor: {}, cursor: {}",
      line.line(),
      line.words(),
      line.wordIndex(),
      line.wordCursor(),
      line.cursor()
    );

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

        List<Completer> completers = Arrays.asList(action.getCompleters());

        // skip further processing if command has no completers
        if (completers.isEmpty()) {
          log.debug("Action has no specific completers; skipping");
          return;
        }

        // HACK: adjust completers 'null' is expected to be translated to NullCompleter.INSTNACE
        for (int i=0; i<completers.size(); i++) {
          if (completers.get(i) == null) {
            // null means stop completing, else w/o implies to use the last completer; this is due to strict?
            completers.set(i, NullCompleter.INSTANCE);
          }
        }
        log.debug("Completers: {}", completers);

        // rebuild parsed-line stripping out the first word (the command-name)
        List<String> words = Lists.newLinkedList(line.words());
        words.remove(0); // shift

        // HACK: complexity here to re-use ArgumentCompleter; not terribly efficient
        String rawLine = line.line();
        ParsedLine argumentList = new DefaultParser.ArgumentList(
          rawLine.substring(command.length() + 1, rawLine.length()),
          words,
          line.wordIndex() - 1,
          line.wordCursor() - 1,
          line.cursor() - command.length() + 1
        );

        // HACK: object has no reasonable toString
        log.debug("Command argument-list: line={}, words={}, wordIndex: {}, wordCursor: {}, cursor: {}",
          argumentList.line(),
          argumentList.words(),
          argumentList.wordIndex(),
          argumentList.wordCursor(),
          argumentList.cursor()
        );

        ArgumentCompleter delegate = new ArgumentCompleter(completers);
        delegate.complete(reader, argumentList, candidates);
      }
    }
  }
}
