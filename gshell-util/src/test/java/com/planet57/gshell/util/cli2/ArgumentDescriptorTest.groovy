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
package com.planet57.gshell.util.cli2

import java.lang.annotation.Annotation

import javax.annotation.Nullable

import org.sonatype.goodies.testsupport.TestSupport

import com.planet57.gossip.Log
import com.planet57.gshell.util.cli2.handler.Handler
import org.junit.Before
import org.junit.Test
import org.slf4j.LoggerFactory

/**
 * Tests for {@link ArgumentDescriptor}.
 */
class ArgumentDescriptorTest
  extends TestSupport
{
  @Before
  void setUp() {
    Log.configure(LoggerFactory.getILoggerFactory())
  }

  private static Argument createArgumentWithToken(@Nullable final String token) {
    return new Argument() {
      @Override
      int index() {
        return 0
      }

      @Override
      String token() {
        return token
      }

      @Override
      boolean required() {
        return false
      }

      @Override
      String description() {
        return null
      }

      @Override
      Class<? extends Handler> handler() {
        return null
      }

      @Override
      Class<? extends Annotation> annotationType() {
        return null
      }
    }
  }

  @Test
  void 'test render-syntax with default token'() {
    def desc = createArgumentWithToken(null)
    def underTest = new ArgumentDescriptor(desc, new NopSetter())
    assert underTest.getToken() == 'ARG'
  }

  @Test
  void 'test render-syntax with specified token'() {
    def desc = createArgumentWithToken('FOO')
    def underTest = new ArgumentDescriptor(desc, new NopSetter())
    assert underTest.getToken() == 'FOO'
  }
}
