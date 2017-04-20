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
package com.planet57.gshell.util.converter.basic;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.sonatype.goodies.testsupport.TestSupport;

/**
 * Tests for {@link EnumConverter}.
 */
public class EnumConverterTest
  extends TestSupport
{
  enum TestEnum
  {
    FOO,
    BAR,
    BAZ
  }

  @Test
  public void testCaseSensitivity() throws Exception {
    TestEnum subject;

    subject = (TestEnum) new EnumConverter(TestEnum.class).convertToObject("foo");
    assertThat(subject).isEqualTo(TestEnum.FOO);

    subject = (TestEnum) new EnumConverter(TestEnum.class).convertToObject("BaR");
    assertThat(subject).isEqualTo(TestEnum.BAR);
  }
}
