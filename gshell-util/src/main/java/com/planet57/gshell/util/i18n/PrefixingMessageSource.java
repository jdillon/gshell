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
package com.planet57.gshell.util.i18n;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A message source which prefixes message codes.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class PrefixingMessageSource
    implements MessageSource
{
  private final MessageSource messages;

  private final String prefix;

  public PrefixingMessageSource(final MessageSource messages, final String prefix) {
    this.messages = checkNotNull(messages);
    this.prefix = checkNotNull(prefix);
  }

  protected String createCode(final String code) {
    checkNotNull(code);
    return prefix + code;
  }

  public String getMessage(final String code) {
    return messages.getMessage(createCode(code));
  }

  public String format(final String code, final Object... args) {
    return messages.format(createCode(code), args);
  }
}
