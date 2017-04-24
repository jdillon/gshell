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

import com.planet57.gshell.command.resolver.Node;
import com.planet57.gshell.util.i18n.MessageSource;
import com.planet57.gshell.util.i18n.ResourceBundleMessageSource;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link HelpPage} for a group.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
public class GroupHelpPage
    implements HelpPage
{
  private final Node node;

  private final HelpContentLoader loader;

  private MessageSource messages;

  public GroupHelpPage(final Node node, final HelpContentLoader loader) {
    this.node = checkNotNull(node);
    checkArgument(node.isGroup());
    this.loader = checkNotNull(loader);
  }

  private MessageSource getMessages() {
    if (messages == null) {
      messages = new ResourceBundleMessageSource(getClass());
    }
    return messages;
  }

  @Override
  public String getName() {
    return node.getAction().getSimpleName();
  }

  @Override
  public String getDescription() {
    return getMessages().format("group-description", getName());
  }

  @Override
  public void render(final PrintWriter out) {
    checkNotNull(out);

    out.println(getMessages().format("group-content-header", getName()));
    HelpPageUtil.render(out, HelpPageUtil.pagesFor(node, loader));
  }

  @Override
  public int hashCode() {
    return getName().hashCode();
  }
}
