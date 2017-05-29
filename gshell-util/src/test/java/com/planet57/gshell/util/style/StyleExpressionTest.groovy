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

import org.jline.utils.AttributedString
import org.jline.utils.AttributedStringBuilder
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

import static org.jline.utils.AttributedStyle.BOLD
import static org.jline.utils.AttributedStyle.CYAN
import static org.jline.utils.AttributedStyle.DEFAULT
import static org.jline.utils.AttributedStyle.RED

/**
 * Tests for {@link StyleExpression}.
 */
class StyleExpressionTest
  extends StyleTestSupport
{
  private StyleExpression underTest

  @Before
  void setUp() {
    super.setUp()
    this.underTest = new StyleExpression(new StyleResolver(source, 'test'))
  }

  @Test
  void 'evaluate expression with prefix and suffix'() {
    def result = underTest.evaluate('foo @{bold bar} baz')
    println result.toAnsi()
    assert result == new AttributedStringBuilder()
        .append('foo ')
        .append('bar', BOLD)
        .append(' baz')
        .toAttributedString()
  }

  @Test
  void 'evaluate expression with prefix'() {
    def result = underTest.evaluate('foo @{bold bar}')
    println result.toAnsi()
    assert result == new AttributedStringBuilder()
        .append('foo ')
        .append('bar', BOLD)
        .toAttributedString()
  }

  @Test
  void 'evaluate expression with suffix'() {
    def result = underTest.evaluate('@{bold foo} bar')
    println result.toAnsi()
    assert result == new AttributedStringBuilder()
        .append('foo', BOLD)
        .append(' bar')
        .toAttributedString()
  }

  @Test
  void 'evaluate expression'() {
    def result = underTest.evaluate('@{bold foo}')
    println result.toAnsi()
    assert result == new AttributedString('foo', BOLD)
  }

  @Test
  void 'evaluate expression with default'() {
    def result = underTest.evaluate('@{.foo:-bold foo}')
    println result.toAnsi()
    assert result == new AttributedString('foo', BOLD)
  }

  @Test
  void 'evaluate expression with multiple replacements'() {
    def result = underTest.evaluate('@{bold foo} @{fg:red bar} @{underline baz}')
    println result.toAnsi()
    assert result == new AttributedStringBuilder()
        .append('foo', BOLD)
        .append(' ')
        .append('bar', DEFAULT.foreground(RED))
        .append(' ')
        .append('baz', DEFAULT.underline())
        .toAttributedString()
  }

  @Test
  void 'evaluate expression missing value'() {
    def result = underTest.evaluate('@{bold}')
    println result.toAnsi()
    assert result == new AttributedString('@{bold}', DEFAULT)
  }

  @Test
  void 'evaluate expression missing tokens'() {
    def result = underTest.evaluate('foo')
    println result.toAnsi()
    assert result == new AttributedString('foo', DEFAULT)
  }

  @Test
  @Ignore("FIXME: need to adjust parser to cope with } in value")
  void 'evaluate expression with ${} value'() {
    def result = underTest.evaluate('@{bold,fg:cyan ${foo}}')
    println result.toAnsi()
    // FIXME: this is not presently valid; will match value '${foo'
    assert result == new AttributedString('${foo}', DEFAULT.foreground(CYAN))
  }
}
