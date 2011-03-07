/**
 * Copyright (c) 2009-2011 the original author or authors.
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
package org.sonatype.gshell.maven;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import org.sonatype.gshell.branding.Branding;
import org.sonatype.gshell.console.ConsoleErrorHandler;
import org.sonatype.gshell.console.ConsolePrompt;
import org.sonatype.gshell.guice.GuiceMainSupport;
import org.sonatype.gshell.logging.LoggingSystem;
import org.sonatype.gshell.logging.gossip.GossipLoggingSystem;
import org.sonatype.gshell.shell.ShellErrorHandler;
import org.sonatype.gshell.shell.ShellPrompt;

import java.util.List;

/**
 * Runs GShell for use in a maven-plugin.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class ShellRunner
    extends GuiceMainSupport
{
    @Override
    protected Branding createBranding() {
        return new BrandingImpl();
    }

    @Override
    protected void configure(final List<Module> modules) {
        super.configure(modules);

        Module custom = new AbstractModule()
        {
            @Override
            protected void configure() {
                bind(LoggingSystem.class).to(GossipLoggingSystem.class);
                bind(ConsolePrompt.class).to(ShellPrompt.class);
                bind(ConsoleErrorHandler.class).to(ShellErrorHandler.class);
            }
        };

        modules.add(custom);
    }
}