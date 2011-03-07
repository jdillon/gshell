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
package org.sonatype.gshell;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;
import com.google.inject.name.Names;
import org.sonatype.gshell.branding.Branding;
import org.sonatype.gshell.command.IO;
import org.sonatype.gshell.command.registry.CommandRegistrar;
import org.sonatype.gshell.console.ConsoleErrorHandler;
import org.sonatype.gshell.console.ConsolePrompt;
import org.sonatype.gshell.guice.CoreModule;
import org.sonatype.gshell.logging.LoggingSystem;
import org.sonatype.gshell.logging.logback.LogbackLoggingSystem;
import org.sonatype.gshell.shell.Shell;
import org.sonatype.gshell.shell.ShellErrorHandler;
import org.sonatype.gshell.shell.ShellImpl;
import org.sonatype.gshell.shell.ShellPrompt;
import org.sonatype.gshell.variables.Variables;
import org.sonatype.guice.bean.binders.SpaceModule;
import org.sonatype.guice.bean.binders.WireModule;
import org.sonatype.guice.bean.locators.DefaultBeanLocator;
import org.sonatype.guice.bean.locators.MutableBeanLocator;
import org.sonatype.guice.bean.reflect.URLClassSpace;

/**
 * Command-line bootstrap for GShell (<tt>gsh</tt>).
 * 
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class Main
    extends MainSupport
{
    private static final DefaultBeanLocator container = new DefaultBeanLocator();

    @Override
    protected Branding createBranding() {
        return new BrandingImpl();
    }

    @Override
    protected Shell createShell() throws Exception {
        Module boot = new AbstractModule()
        {
            @Override
            protected void configure() {
                bind(MutableBeanLocator.class).toInstance(container);
                bind(LoggingSystem.class).to(LogbackLoggingSystem.class);
                bind(ConsolePrompt.class).to(ShellPrompt.class);
                bind(ConsoleErrorHandler.class).to(ShellErrorHandler.class);
                bind(Branding.class).toInstance(getBranding());
                bind(IO.class).annotatedWith(Names.named("main")).toInstance(io);
                bind(Variables.class).annotatedWith(Names.named("main")).toInstance(vars);
            }
        };

        URLClassSpace space = new URLClassSpace(getClass().getClassLoader());
        Injector injector = Guice.createInjector(Stage.PRODUCTION, new WireModule(new SpaceModule(space), boot, new CoreModule()));
        container.add(injector, 0);

        ShellImpl shell = injector.getInstance(ShellImpl.class);
        injector.getInstance(CommandRegistrar.class).registerCommands();

        return shell;
    }

    public static void main(final String[] args) throws Exception {
        new Main().boot(args);
    }
}
