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
package com.planet57.gshell.util.converter

import static org.fest.assertions.api.Assertions.assertThat

import com.planet57.gshell.util.converter.basic.EnumConverter
import org.junit.Test
import org.sonatype.goodies.testsupport.TestSupport

import java.beans.PropertyEditor

/**
 * Tests for {@link Converters}.
 */
class ConvertersTest
  extends TestSupport
{
  @Test
  void findEnumConverter() {
    PropertyEditor found = Converters.findConverterOrEditor(Thread.State.class)
    assertThat(found).isOfAnyClassIn(EnumConverter.class)
  }
}
