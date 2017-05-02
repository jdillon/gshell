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

import static com.planet57.gshell.util.i18n.MessageBundle.DefaultMessage
import org.junit.Before
import org.junit.Test
import org.sonatype.goodies.testsupport.TestSupport

/**
 * Tests for {@link MessageBundle}.
 */
class MessageBundleTest
    extends TestSupport
{
  interface Messages
      extends MessageBundle
  {
    @DefaultMessage('This is a test')
    String test1()

    @DefaultMessage('s:%s,i:%s')
    String testWithFormat(String a, int b)

    String testMissing()

    Object testInvalid()
  }

  private Messages messages

  @Before
  void setUp() {
    messages = I18N.create(Messages.class)
    assert messages != null
  }

  @Test
  void 'default message'() {
    assert messages.test1() == 'This is a test'
  }

  @Test
  void 'default message with format'() {
    assert messages.testWithFormat('foo', 1) == 's:foo,i:1'
  }

  @Test
  void 'missing message'() {
    assert messages.testMissing() == String.format(I18N.MISSING_MESSAGE_FORMAT, 'testMissing')
  }

  @Test(expected = Error.class)
  void 'invalid message'() {
    // message-bundle methods must return a String
    messages.testInvalid()
  }
}
