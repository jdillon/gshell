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
package com.planet57.gshell.util.cli2.handler;

import com.planet57.gshell.util.cli2.CliDescriptor;
import com.planet57.gshell.util.converter.Converter;
import com.planet57.gshell.util.i18n.I18N;
import com.planet57.gshell.util.i18n.MessageBundle;

/**
 * Handler which uses a {@link Converter} to coerce types.
 *
 * @since 2.3
 */
public class DefaultHandler
    extends Handler
{
  private interface Messages
    extends MessageBundle
  {
    @DefaultMessage("VAL")
    String defaultToken();
  }

  private static Messages messages = I18N.create(Messages.class);

  public DefaultHandler(final CliDescriptor desc) {
    super(desc);
  }

  @Override
  public void handle(final String arg) throws Exception {
    set(arg);
  }

  @Override
  public String getDefaultToken() {
    return messages.defaultToken();
  }
}
