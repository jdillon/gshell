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
package com.planet57.gshell.commands.plugin;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import com.planet57.gshell.command.Command;
import com.planet57.gshell.command.CommandActionSupport;
import com.planet57.gshell.command.CommandContext;
import com.planet57.gshell.commands.plugin.internal.PluginManager;
import com.planet57.gshell.commands.plugin.internal.PluginRegistration;
import com.planet57.gshell.guice.BeanContainer;
import com.planet57.gshell.util.cli2.Argument;

import static com.google.common.base.Preconditions.checkState;

/**
 * Unload a plugin.
 *
 * @since 3.0
 */
@Command(name="plugin/unload", description = "Unload a plugin")
public class UnloadPluginAction
  extends CommandActionSupport
{
  @Inject
  private BeanContainer container;

  @Inject
  private PluginManager pluginManager;

  @Argument(required = true, description = "Plugin ID", token = "ID")
  private String id;

  @Override
  public Object execute(@Nonnull final CommandContext context) throws Exception {
    PluginRegistration registration = pluginManager.remove(id);
    checkState(registration != null, "Missing plugin: %s", id);

    container.remove(registration.getInjector());
    registration.getClassLoader().close();

    context.getIo().format("Plugin unloaded: %s%n", id);

    return null;
  }
}
