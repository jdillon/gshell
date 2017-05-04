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
package com.planet57.gshell.util.i18n

import org.junit.Before
import org.junit.Test
import org.sonatype.goodies.testsupport.TestSupport

/**
 * Tests for {@link ResourceBundleMessageSource}.
 */
class ResourceBundleMessageSourceTest
  extends TestSupport
{
  private ResourceBundleMessageSource underTest

  @Before
  void setUp() {
    underTest = new ResourceBundleMessageSource(getClass())
  }

  @Test
  void 'load and get message'() {
    assert underTest.getMessage("a") == "1"
    assert underTest.getMessage("b") == "2"
    assert underTest.getMessage("c") == "3"
    assert underTest.format("f", 1, 2, 3) == "1 2 3"
  }

  @Test
  void 'load format'() {
    assert underTest.format("f", 1, 2, 3) == "1 2 3"
  }

  @Test(expected = ResourceNotFoundException.class)
  void 'missing resource throws exception'() {
    underTest.getMessage("no-such-code")
  }
}
