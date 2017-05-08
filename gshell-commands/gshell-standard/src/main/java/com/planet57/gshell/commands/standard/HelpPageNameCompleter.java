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
package com.planet57.gshell.commands.standard;

import com.planet57.gshell.help.HelpPage;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;
import org.jline.reader.impl.completer.AggregateCompleter;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;

/**
 * Completer for {@link HelpPage} names.
 *
 * @since 3.0
 */
@Named("help-page-name")
@Singleton
public class HelpPageNameCompleter
  implements Completer
{
  private final Completer delegate;

  @Inject
  public HelpPageNameCompleter(@Named("alias-name") final Completer aliasName,
                               @Named("node-path") final Completer nodePath,
                               @Named("meta-help-page-name") final Completer metaHelpPageName)
  {
    this.delegate = new AggregateCompleter(aliasName, nodePath, metaHelpPageName);
  }

  @Override
  public void complete(final LineReader reader, final ParsedLine line, final List<Candidate> candidates) {
    delegate.complete(reader, line, candidates);
  }
}
