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

package org.sonatype.gshell.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;
import org.sonatype.gshell.MainSupport;
import org.sonatype.gshell.branding.Branding;
import org.sonatype.gshell.command.IO;
import org.sonatype.gshell.command.registry.CommandRegistrar;
import org.sonatype.gshell.shell.Shell;
import org.sonatype.gshell.shell.ShellImpl;
import org.sonatype.gshell.variables.Variables;
import org.sonatype.guice.bean.binders.SpaceModule;
import org.sonatype.guice.bean.binders.WireModule;
import org.sonatype.guice.bean.locators.DefaultBeanLocator;
import org.sonatype.guice.bean.locators.MutableBeanLocator;
import org.sonatype.guice.bean.reflect.URLClassSpace;
import org.sonatype.inject.BeanScanning;

import java.util.ArrayList;
import java.util.List;

import static com.google.inject.name.Names.named;

/**
 * Support for booting shell applications with Guice.
 * 
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.6.4
 */
public abstract class GuiceMainSupport
    extends MainSupport
{
    protected final DefaultBeanLocator container = new DefaultBeanLocator();

    @Override
    protected Shell createShell() throws Exception {
        List<Module> modules = new ArrayList<Module>();
        configure(modules);

        Injector injector = Guice.createInjector(Stage.PRODUCTION, new WireModule(modules));
        container.add(injector, 0);

        ShellImpl shell = injector.getInstance(ShellImpl.class);
        injector.getInstance(CommandRegistrar.class).registerCommands();

        return shell;
    }

    protected void configure(final List<Module> modules) {
        assert modules != null;
        modules.add(createSpaceModule());
        modules.add(new BootModule());
        modules.add(new CoreModule());
    }

    protected SpaceModule createSpaceModule() {
        URLClassSpace space = new URLClassSpace(getClass().getClassLoader());
        return new SpaceModule(space, BeanScanning.INDEX);
    }

    protected class BootModule
        extends AbstractModule
    {
        @Override
        protected void configure() {
            bind(MutableBeanLocator.class).toInstance(container);
            bind(Branding.class).toInstance(getBranding());
            bind(IO.class).annotatedWith(named("main")).toInstance(io);
            bind(Variables.class).annotatedWith(named("main")).toInstance(vars);
        }
    }
}
