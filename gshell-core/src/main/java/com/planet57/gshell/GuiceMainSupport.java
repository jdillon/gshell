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
package com.planet57.gshell;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.planet57.gshell.branding.Branding;
import com.planet57.gshell.branding.BrandingSupport;
import com.planet57.gshell.command.IO;
import com.planet57.gshell.command.registry.CommandRegistrar;
import com.planet57.gshell.event.EventManager;
import com.planet57.gshell.shell.Shell;
import com.planet57.gshell.shell.ShellImpl;
import com.planet57.gshell.variables.Variables;

import org.eclipse.sisu.inject.DefaultBeanLocator;
import org.eclipse.sisu.inject.MutableBeanLocator;
import org.eclipse.sisu.space.BeanScanning;
import org.eclipse.sisu.space.SpaceModule;
import org.eclipse.sisu.space.URLClassSpace;
import org.eclipse.sisu.wire.WireModule;

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
  protected Branding createBranding() {
    // HACK: back to non-injected branding, this is needed too early presently and mess up logging
    return new BrandingSupport();
  }

  @Override
  protected Shell createShell() throws Exception {
    List<Module> modules = new ArrayList<>();
    configure(modules);

    Injector injector = Guice.createInjector(new WireModule(modules));
    container.add(injector, 0);

    ShellImpl shell = injector.getInstance(ShellImpl.class);

    // HACK: really need some component lifecycle
    injector.getInstance(EventManager.class).start();
    injector.getInstance(CommandRegistrar.class).discoverCommands();

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
      bind(IO.class).annotatedWith(named("main")).toInstance(io);
      bind(Variables.class).annotatedWith(named("main")).toInstance(vars);
    }
  }
}
