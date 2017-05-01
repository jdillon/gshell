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
package com.planet57.gshell.internal

import org.sonatype.goodies.testsupport.TestSupport

import com.planet57.gshell.command.CommandAction
import org.junit.Test

/**
 * Tests for {@link ExitCodeDecoder}.
 */
class ExitCodeDecoderTest
  extends TestSupport
{
  @Test
  void 'decode number'() {
    assert ExitCodeDecoder.decode(1) == 1
  }

  @Test
  void 'decode null'() {
    assert ExitCodeDecoder.decode(null) == 0
  }

  @Test
  void 'decode other'() {
    assert ExitCodeDecoder.decode(new Object()) == 1
  }
}
