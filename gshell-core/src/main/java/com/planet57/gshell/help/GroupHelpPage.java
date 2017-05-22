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
import com.planet57.gshell.shell.Shell;
import com.planet57.gshell.util.i18n.I18N;
import com.planet57.gshell.util.i18n.MessageBundle;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link HelpPage} for a group.
 *
 * @since 2.5
 */
public class GroupHelpPage
    implements HelpPage
{
  private interface Messages
    extends MessageBundle
  {
    @DefaultMessage("Command group: @|bold %s|@")
    String description(String name);

    @DefaultMessage("Help pages in group @|bold %s|@:")
    String header(String name);
  }

  private static final Messages messages = I18N.create(Messages.class);

  private final Node node;

  private final HelpContentLoader loader;

  public GroupHelpPage(final Node node, final HelpContentLoader loader) {
    this.node = checkNotNull(node);
    checkArgument(node.isGroup());
    this.loader = checkNotNull(loader);
  }

  @Override
  public String getName() {
    return node.getAction().getSimpleName();
  }

  @Override
  public String getDescription() {
    return messages.description(getName());
  }

  @Override
  public void render(final Shell shell, final PrintWriter out) throws Exception {
    checkNotNull(shell);
    checkNotNull(out);

    out.println(messages.header(getName()));
    HelpPageUtil.renderIndex(out, HelpPageUtil.pagesFor(node, loader));
  }

  @Override
  public int hashCode() {
    return getName().hashCode();
  }
}
