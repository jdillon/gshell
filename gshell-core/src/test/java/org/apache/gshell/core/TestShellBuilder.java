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

package org.apache.gshell.core;

import org.apache.gshell.Shell;
import org.apache.gshell.core.guice.GuiceShellBuilder;
import org.apache.gshell.core.guice.CoreModule;
import org.apache.gshell.registry.CommandRegistrar;
import com.google.inject.Injector;
import com.google.inject.Guice;
import com.google.inject.Stage;

/**
 * Builds {@link org.apache.gshell.Shell} instances.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class TestShellBuilder
    extends GuiceShellBuilder
{
    protected Injector createInjector() {
        return Guice.createInjector(Stage.DEVELOPMENT, new CoreModule());
    }

    protected void registerCommand(final String name, final String type) throws Exception {
        CommandRegistrar registrar = getInjector().getInstance(CommandRegistrar.class);
        registrar.registerCommand(name, type);
    }

    @Override
    public Shell create() throws Exception {
        setRegisterCommands(false);
        return super.create();
    }
}