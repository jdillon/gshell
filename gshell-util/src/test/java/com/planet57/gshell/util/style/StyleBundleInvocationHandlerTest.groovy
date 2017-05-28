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
package com.planet57.gshell.util.style

import org.sonatype.goodies.testsupport.TestSupport

import com.planet57.gossip.Log
import com.planet57.gshell.util.style.StyleBundle.DefaultStyle
import com.planet57.gshell.util.style.StyleBundle.StyleGroup
import com.planet57.gshell.util.style.StyleBundle.StyleName
import com.planet57.gshell.util.style.StyleBundleInvocationHandler.InvalidStyleBundleMethodException
import org.jline.utils.AttributedString
import org.jline.utils.AttributedStyle
import org.junit.Before
import org.junit.Test
import org.slf4j.LoggerFactory

/**
 * Tests for {@link StyleBundleInvocationHandler}.
 */
class StyleBundleInvocationHandlerTest
  extends TestSupport
{
  private MemoryStyleSource source

  @Before
  void setUp() {
    // force bootstrap gossip logger to adapt to runtime logger-factory
    Log.configure(LoggerFactory.getILoggerFactory())

    this.source = new MemoryStyleSource()
  }

  @StyleGroup('test')
  static interface Styles
    extends StyleBundle
  {
    @DefaultStyle('bold,fg:red')
    AttributedString boldRed(String value)

    @StyleName('boldRed')
    @DefaultStyle('bold,fg:red')
    AttributedString boldRedObjectWithStyleName(Object value)

    void invalidReturn(String value)

    AttributedString notEnoughArguments()

    AttributedString tooManyArguments(int a, int b)
  }

  @Test
  void 'bundle default-style'() {
    def styles = StyleBundleInvocationHandler.create(source, Styles.class)
    println styles
    def string = styles.boldRed('foo bar')
    assert string == new AttributedString('foo bar', AttributedStyle.BOLD.foreground(AttributedStyle.RED))
  }

  @Test
  void 'bundle style-name with default-style'() {
    def styles = StyleBundleInvocationHandler.create(source, Styles.class)
    def string = styles.boldRedObjectWithStyleName('foo bar')
    assert string == new AttributedString('foo bar', AttributedStyle.BOLD.foreground(AttributedStyle.RED))
  }

  @Test
  void 'bundle sourced-style'() {
    source.group('test').put('boldRed', 'bold,fg:yellow')
    def styles = StyleBundleInvocationHandler.create(source, Styles.class)
    def string = styles.boldRed('foo bar')
    assert string == new AttributedString('foo bar', AttributedStyle.BOLD.foreground(AttributedStyle.YELLOW))
  }

  @Test
  void 'bundle method validation'() {
    def styles = StyleBundleInvocationHandler.create(source, Styles.class)

    try {
      styles.invalidReturn('foo')
      assert false
    }
    catch (InvalidStyleBundleMethodException e) {
      // expected
    }

    try {
      styles.notEnoughArguments()
      assert false
    }
    catch (InvalidStyleBundleMethodException e) {
      // expected
    }

    try {
      styles.tooManyArguments(1, 2)
      assert false
    }
    catch (InvalidStyleBundleMethodException e) {
      // expected
    }
  }
}
