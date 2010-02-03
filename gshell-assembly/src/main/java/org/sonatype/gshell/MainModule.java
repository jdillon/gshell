/*
 * Copyright (C) 2010 the original author or authors.
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

package org.sonatype.gshell;

import com.google.inject.AbstractModule;
import org.sonatype.gshell.branding.Branding;
import org.sonatype.gshell.console.ConsoleErrorHandler;
import org.sonatype.gshell.console.ConsolePrompt;
import org.sonatype.gshell.shell.Shell;
import org.sonatype.gshell.shell.ShellErrorHandler;
import org.sonatype.gshell.shell.ShellImpl;
import org.sonatype.gshell.shell.ShellPrompt;

/**
 * Main module.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
public class MainModule
    extends AbstractModule
{
    @Override
    protected void configure() {
        bind(Branding.class).to(BrandingImpl.class);
        bind(ConsolePrompt.class).to(ShellPrompt.class);
        bind(ConsoleErrorHandler.class).to(ShellErrorHandler.class);
        bind(Shell.class).to(ShellImpl.class);
    }
}