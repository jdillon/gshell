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
package com.planet57.gshell.help;

import java.io.PrintWriter;

import com.planet57.gshell.util.i18n.MessageSource;
import com.planet57.gshell.util.i18n.ResourceBundleMessageSource;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link HelpPage} for an alias.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
public class AliasHelpPage
    implements HelpPage
{
  private final String name;

  private final String alias;

  private MessageSource messages;

  public AliasHelpPage(final String name, final String alias) {
    this.name = checkNotNull(name);
    this.alias = checkNotNull(alias);
  }

  private MessageSource getMessages() {
    if (messages == null) {
      messages = new ResourceBundleMessageSource(getClass());
    }
    return messages;
  }


  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getDescription() {
    return getMessages().format("alias-description", alias);
  }

  @Override
  public void render(final PrintWriter out) {
    checkNotNull(out);
    out.println(getMessages().format("alias-content", name, alias));
  }

  @Override
  public int hashCode() {
    return getName().hashCode();
  }
}
