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
package com.planet57.gshell.parser;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;
import com.planet57.gshell.parser.impl.eval.Evaluator;
import com.planet57.gshell.parser.impl.eval.RegexEvaluator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sonatype.goodies.testsupport.TestSupport;

import static org.junit.Assert.fail;

/**
 * Tests for {@link CommandLineParserImpl}.
 */
public class CommandLineParserImplTest
  extends TestSupport
{
  private CommandLineParser underTest;

  @Before
  public void setUp() throws Exception {
    Injector injector = Guice.createInjector(Stage.DEVELOPMENT, (Module) binder -> {
      binder.bind(CommandLineParser.class).to(CommandLineParserImpl.class);
      binder.bind(Evaluator.class).to(RegexEvaluator.class);
    });
    underTest = injector.getInstance(CommandLineParser.class);
  }

  @After
  public void tearDown() {
    underTest = null;
  }

  @Test
  public void testParseNull() throws Exception {
    try {
      underTest.parse(null);
      fail();
    }
    catch (NullPointerException expected) {
      // ignore
    }
  }
}
