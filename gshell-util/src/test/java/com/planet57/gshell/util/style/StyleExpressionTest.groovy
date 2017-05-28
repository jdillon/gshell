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
import org.jline.utils.AttributedString
import org.jline.utils.AttributedStringBuilder
import org.jline.utils.AttributedStyle
import org.junit.Before
import org.junit.Test
import org.slf4j.LoggerFactory

/**
 * Tests for {@link StyleExpression}.
 */
class StyleExpressionTest
  extends TestSupport
{
  private MemoryStyleSource source

  private StyleExpression underTest

  @Before
  void setUp() {
    // force bootstrap gossip logger to adapt to runtime logger-factory
    Log.configure(LoggerFactory.getILoggerFactory())

    this.source = new MemoryStyleSource()
    this.underTest = new StyleExpression(new StyleResolver(source, 'test'))
  }

  @Test
  void 'evaluate expression with prefix and suffix'() {
    def result = underTest.evaluate('foo @{bold bar} baz')
    assert result == new AttributedStringBuilder()
        .append('foo ')
        .append('bar', AttributedStyle.BOLD)
        .append(' baz')
        .toAttributedString()
  }

  @Test
  void 'evaluate expression with prefix'() {
    def result = underTest.evaluate('foo @{bold bar}')
    assert result == new AttributedStringBuilder()
        .append('foo ')
        .append('bar', AttributedStyle.BOLD)
        .toAttributedString()
  }

  @Test
  void 'evaluate expression with suffix'() {
    def result = underTest.evaluate('@{bold foo} bar')
    assert result == new AttributedStringBuilder()
        .append('foo', AttributedStyle.BOLD)
        .append(' bar')
        .toAttributedString()
  }

  @Test
  void 'evaluate expression'() {
    def result = underTest.evaluate('@{bold foo}')
    assert result == new AttributedString('foo', AttributedStyle.BOLD)
  }

  @Test
  void 'evaluate expression missing value'() {
    def result = underTest.evaluate('@{bold}')
    assert result == new AttributedString('@{bold}', AttributedStyle.DEFAULT)
  }

  @Test
  void 'evaluate expression missing tokens'() {
    def result = underTest.evaluate('foo')
    assert result == new AttributedString('foo', AttributedStyle.DEFAULT)
  }
}
