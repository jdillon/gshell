/*
 * Copyright (C) 2009 the original author(s).
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

package org.sonatype.gshell.shell;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import org.sonatype.gshell.builder.guice.CoreModule;
import org.sonatype.gshell.builder.guice.GuiceShellBuilder;
import org.sonatype.gshell.registry.CommandRegistrar;
import org.sonatype.gshell.shell.Shell;

/**
 * Builds {@link org.sonatype.gshell.shell.Shell} instances for testing.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class TestShellBuilder
    extends GuiceShellBuilder
{
    @Override
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