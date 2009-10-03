/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.gshell.core.guice;

import com.google.inject.AbstractModule;
import org.apache.gshell.command.CommandDocumenter;
import org.apache.gshell.core.command.CommandDocumenterImpl;
import org.apache.gshell.core.event.EventManagerImpl;
import org.apache.gshell.core.execute.CommandExecutorImpl;
import org.apache.gshell.core.parser.CommandLineParserImpl;
import org.apache.gshell.core.registry.AliasRegistryImpl;
import org.apache.gshell.core.registry.CommandRegistryImpl;
import org.apache.gshell.core.registry.CommandResolverImpl;
import org.apache.gshell.event.EventManager;
import org.apache.gshell.execute.CommandExecutor;
import org.apache.gshell.execute.CommandLineParser;
import org.apache.gshell.registry.AliasRegistry;
import org.apache.gshell.registry.CommandRegistrar;
import org.apache.gshell.registry.CommandRegistry;
import org.apache.gshell.registry.CommandResolver;

/**
 * Mvnsh module.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 *
 * @since 2.0
 */
public class CoreModule
    extends AbstractModule
{
    @Override
    protected void configure() {
        // Core components
        bind(EventManager.class).to(EventManagerImpl.class);
        bind(AliasRegistry.class).to(AliasRegistryImpl.class);
        bind(CommandRegistry.class).to(CommandRegistryImpl.class);
        bind(CommandDocumenter.class).to(CommandDocumenterImpl.class);
        bind(CommandLineParser.class).to(CommandLineParserImpl.class);
        bind(CommandExecutor.class).to(CommandExecutorImpl.class);
        bind(CommandResolver.class).to(CommandResolverImpl.class);
        bind(CommandRegistrar.class).to(GuiceCommandRegistrar.class);
    }
}