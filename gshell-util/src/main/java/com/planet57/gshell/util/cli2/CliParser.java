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
import org.apache.commons.cli.MissingOptionException;

/**
 * Command-line parser.
 *
 * @since 3.0
 */
interface CliParser
  extends CommandLineParser
{
  enum Flavor
  {
    POSIX,
    GNU,
    DEFAULT;

    public CliParser create() {
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

    @SuppressWarnings("deprecation")
    private static class GnuParser
      extends org.apache.commons.cli.GnuParser
      implements CliParser
    {
      /**
       * Default required options check delayed for {@link Option#override()} support.
       */
      @Override
      protected void checkRequiredOptions() {
        // empty
      }

      @Override
      public void ensureRequiredOptionsPresent() throws MissingOptionException {
        super.checkRequiredOptions();
      }
    }

    @SuppressWarnings("deprecation")
    private static class PosixParser
      extends org.apache.commons.cli.PosixParser
      implements CliParser
    {
      /**
       * Default required options check delayed for {@link Option#override()} support.
       */
      @Override
      protected void checkRequiredOptions() {
        // empty
      }

      @Override
      public void ensureRequiredOptionsPresent() throws MissingOptionException {
        super.checkRequiredOptions();
      }
    }

    private static class DefaultParser
      extends org.apache.commons.cli.DefaultParser
      implements CliParser
    {
      /**
       * Default required options check delayed for {@link Option#override()} support.
       */
      @Override
      protected void checkRequiredOptions() {
        // empty
      }

      @Override
      public void ensureRequiredOptionsPresent() throws MissingOptionException {
         super.checkRequiredOptions();
      }
    }
  }

  /**
   * Throws {@link MissingOptionException} if any required options are missing.
   */
  void ensureRequiredOptionsPresent() throws MissingOptionException;
}
