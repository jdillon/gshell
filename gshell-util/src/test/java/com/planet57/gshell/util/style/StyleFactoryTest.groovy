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
import org.junit.Before
import org.junit.Test

import static org.jline.utils.AttributedStyle.BOLD
import static org.jline.utils.AttributedStyle.RED
import static org.jline.utils.AttributedStyle.YELLOW

/**
 * Tests for {@link StyleFactory}.
 */
class StyleFactoryTest
  extends StyleTestSupport
{
  private StyleFactory underTest

  @Before
  void setUp() {
    super.setUp()
    this.underTest = new StyleFactory(new StyleResolver(source, 'test'))
  }

  @Test
  void 'style direct'() {
    def string = underTest.style('bold,fg:red', 'foo bar')
    println string.toAnsi()
    assert string == new AttributedString('foo bar', BOLD.foreground(RED))
  }

  @Test
  void 'style referenced'() {
    source.group('test').put('very-red', 'bold,fg:red')
    def string = underTest.style('.very-red', 'foo bar')
    println string.toAnsi()
    assert string == new AttributedString('foo bar', BOLD.foreground(RED))
  }

  @Test
  void 'missing referenced style with default'() {
    def string = underTest.style('.very-red:-bold,fg:red', 'foo bar')
    println string.toAnsi()
    assert string == new AttributedString('foo bar', BOLD.foreground(RED))
  }

  @Test
  void 'missing referenced style with customized'() {
    source.group('test').put('very-red', 'bold,fg:yellow')
    def string = underTest.style('.very-red:-bold,fg:red', 'foo bar')
    println string.toAnsi()
    assert string == new AttributedString('foo bar', BOLD.foreground(YELLOW))
  }

  @Test
  void 'style format'() {
    def string = underTest.style('bold', '%s', 'foo')
    println string.toAnsi()
    assert string == new AttributedString('foo', BOLD)
  }

  @Test
  void 'evaluate expression'() {
    def string = underTest.evaluate('@{bold foo}')
    println string.toAnsi()
    assert string == new AttributedString('foo', BOLD)
  }

  @Test
  void 'evaluate expression with format'() {
    def string = underTest.evaluate('@{bold %s}', 'foo')
    println string.toAnsi()
    assert string == new AttributedString('foo', BOLD)
  }
}
