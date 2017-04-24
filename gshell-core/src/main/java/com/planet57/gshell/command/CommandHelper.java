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
package com.planet57.gshell.command;

import com.planet57.gshell.branding.Branding;
import com.planet57.gshell.util.cli2.CliProcessor;
import com.planet57.gshell.util.cli2.Option;
import com.planet57.gshell.util.i18n.AggregateMessageSource;
import com.planet57.gshell.util.i18n.MessageSource;
import com.planet57.gshell.util.i18n.PrefixingMessageSource;
import com.planet57.gshell.util.i18n.ResourceBundleMessageSource;
import com.planet57.gshell.util.pref.PreferenceProcessor;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Command helper.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class CommandHelper
{
  public static final String COMMAND_DOT = "command.";

  public static final String COMMAND_NAME = "command.name";

  public static final String COMMAND_DESCRIPTION = "command.description";

  @Option(name = "h", longName = "help", override = true)
  public boolean displayHelp;

  private MessageSource messages;

  private MessageSource getMessages() {
    if (messages == null) {
      messages = new ResourceBundleMessageSource(getClass());
    }
    return messages;
  }

  /**
   * Construct a {@link CliProcessor} for given action.
   */
  public CliProcessor createCliProcessor(final CommandAction command) {
    checkNotNull(command);

    CliProcessor clp = new CliProcessor();
    clp.addBean(command);
    clp.addBean(this);

    AggregateMessageSource messages = new AggregateMessageSource(command.getMessages(), this.getMessages());
    clp.setMessages(new PrefixingMessageSource(messages, COMMAND_DOT));

    return clp;
  }

  /**
   * Get the description for a given action.
   */
  public static String getDescription(final CommandAction command) {
    checkNotNull(command);
    return command.getMessages().getMessage(COMMAND_DESCRIPTION);
  }

  /**
   * Create a {@link PreferenceProcessor} for given action.
   */
  public static PreferenceProcessor createPreferenceProcessor(final CommandAction command, final Branding branding) {
    checkNotNull(command);
    checkNotNull(branding);

    PreferenceProcessor pp = new PreferenceProcessor();
    pp.setBasePath(branding.getPreferencesBasePath());
    pp.addBean(command);

    return pp;
  }
}
