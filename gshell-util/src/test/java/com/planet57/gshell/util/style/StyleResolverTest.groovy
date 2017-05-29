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

import org.junit.Before
import org.junit.Test

import static org.jline.utils.AttributedStyle.BOLD
import static org.jline.utils.AttributedStyle.DEFAULT
import static org.jline.utils.AttributedStyle.RED

/**
 * Tests for {@link StyleResolver}.
 */
class StyleResolverTest
  extends StyleTestSupport
{
  private StyleResolver underTest

  @Before
  void setUp() {
    super.setUp()
    this.underTest = new StyleResolver(source, 'test')
  }

  @Test
  void 'resolve bold'() {
    def style = underTest.resolve('bold')
    assert style == BOLD
  }

  @Test
  void 'resolve fg:red'() {
    def style = underTest.resolve('fg:red')
    assert style == DEFAULT.foreground(RED)
  }

  @Test
  void 'resolve fg:red with whitespace'() {
    def style = underTest.resolve(' fg:  red ')
    assert style == DEFAULT.foreground(RED)
  }

  @Test
  void 'resolve bg:red'() {
    def style = underTest.resolve('bg:red')
    assert style == DEFAULT.background(RED)
  }

  @Test
  void 'resolve invalid color-mode'() {
    def style = underTest.resolve('invalid:red')
    assert style == DEFAULT
  }

  @Test
  void 'resolve invalid color-name'() {
    def style = underTest.resolve('fg:invalid')
    assert style == DEFAULT
  }

  @Test
  void 'resolve bold,fg:red'() {
    def style = underTest.resolve('bold,fg:red')
    assert style == BOLD.foreground(RED)
  }

  @Test
  void 'resolve with whitespace'() {
    def style = underTest.resolve('  bold ,   fg:red   ')
    assert style == BOLD.foreground(RED)
  }

  @Test
  void 'resolve with missing values'() {
    def style = underTest.resolve('bold,,,,,fg:red')
    assert style == BOLD.foreground(RED)
  }

  @Test
  void 'resolve referenced style'() {
    source.set('test', 'very-red', 'bold,fg:red')
    def style = underTest.resolve('.very-red')
    assert style == BOLD.foreground(RED)
  }

  @Test
  void 'resolve referenced style-missing with default direct'() {
    def style = underTest.resolve('.very-red:-bold,fg:red')
    assert style == BOLD.foreground(RED)
  }

  @Test
  void 'resolve referenced style-missing with default direct and whitespace'() {
    def style = underTest.resolve('.very-red   :-   bold,fg:red')
    assert style == BOLD.foreground(RED)
  }

  @Test
  void 'resolve referenced style-missing with default referenced'() {
    source.set('test', 'more-red', 'bold,fg:red')
    def style = underTest.resolve('.very-red:-.more-red')
    assert style == BOLD.foreground(RED)
  }
}
