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
package com.planet57.gshell.util.cli2;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.PosixParser;

/**
 * {@link CommandLineParser} flavor.
 *
 * @since 3.0
 */
public enum CliParserFlavor
{
  @Deprecated
  POSIX,

  @Deprecated
  GNU,

  /**
   * Preferred flavor; which supports features of POSIX and GNU.
   */
  DEFAULT;

  public CommandLineParser create() {
    switch (this) {
      case POSIX:
        return new PosixParser();

      case GNU:
        return new GnuParser();

      case DEFAULT:
        return new DefaultParser();
    }

    // unreachable
    throw new Error();
  }
}
