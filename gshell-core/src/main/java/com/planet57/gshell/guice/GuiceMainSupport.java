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
package com.planet57.gshell.guice;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.planet57.gshell.MainSupport;
import com.planet57.gshell.branding.Branding;
import com.planet57.gshell.branding.BrandingSupport;
import com.planet57.gshell.command.IO;
import com.planet57.gshell.shell.Shell;
import com.planet57.gshell.shell.ShellImpl;
import com.planet57.gshell.variables.Variables;

import org.eclipse.sisu.space.BeanScanning;
import org.eclipse.sisu.space.SpaceModule;
import org.eclipse.sisu.space.URLClassSpace;
import org.eclipse.sisu.wire.WireModule;

import javax.annotation.Nonnull;

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
  private final BeanContainer container = new BeanContainer();

  @Override
  protected Branding createBranding() {
    return new BrandingSupport();
  }

  @Override
  protected Shell createShell() throws Exception {
    List<Module> modules = new ArrayList<>();
    configure(modules);

    Injector injector = Guice.createInjector(new WireModule(modules));
    container.add(injector, 0);

    ShellImpl shell = injector.getInstance(ShellImpl.class);
    shell.start();

    return shell;
  }

  protected void configure(@Nonnull final List<Module> modules) {
    modules.add(createSpaceModule());
    modules.add(binder -> {
      binder.bind(BeanContainer.class).toInstance(container);

      // FIXME: due to ShellImpl being a Guice component, but there are not we have to bind these so they can be injected
      binder.bind(IO.class).annotatedWith(named("main")).toInstance(getIo());
      binder.bind(Variables.class).annotatedWith(named("main")).toInstance(getVariables());
      binder.bind(Branding.class).toInstance(getBranding());
    });
  }

  protected SpaceModule createSpaceModule() {
    URLClassSpace space = new URLClassSpace(getClass().getClassLoader());
    return new SpaceModule(space, BeanScanning.INDEX);
  }
}
