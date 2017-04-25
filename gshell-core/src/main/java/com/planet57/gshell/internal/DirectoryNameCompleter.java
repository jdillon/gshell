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
package com.planet57.gshell.internal;

import java.nio.file.Path;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.planet57.gshell.util.jline.StringsCompleter2;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;

import com.planet57.gshell.variables.Variables;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

/**
 * {@link Completer} for directory names.
 *
 * @since 3.0
 */
@Named("directory-name")
@Singleton
public class DirectoryNameCompleter
  extends FileNameCompleter
{
  @Inject
  public DirectoryNameCompleter(final Provider<Variables> variables) {
    super(variables);
  }

  @Override
  public void complete(final LineReader reader, final ParsedLine commandLine, final List<Candidate> candidates) {
    super.complete(reader, commandLine, candidates);

    // HACK: make directory completion aware of ./ and ../; this won't resolve to other members however
    candidates.add(StringsCompleter2.candidate("./"));
    candidates.add(StringsCompleter2.candidate("../"));
  }

  @Override
  protected boolean accept(final Path path) {
    return path.toFile().isDirectory();
  }
}
