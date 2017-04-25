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
package com.planet57.gshell.maven;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.planet57.gshell.branding.Asl2License;
import com.planet57.gshell.branding.Branding;
import com.planet57.gshell.branding.BrandingSupport;
import com.planet57.gshell.branding.License;
import com.planet57.gshell.command.IO;
import com.planet57.gshell.execute.ExitNotification;
import com.planet57.gshell.internal.BeanContainer;
import com.planet57.gshell.logging.LoggingSystem;
import com.planet57.gshell.shell.ShellImpl;
import com.planet57.gshell.util.io.PrintBuffer;
import com.planet57.gshell.util.io.StreamSet;
import com.planet57.gshell.variables.VariableNames;
import com.planet57.gshell.variables.Variables;
import com.planet57.gshell.variables.VariablesSupport;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.eclipse.sisu.space.BeanScanning;
import org.eclipse.sisu.space.SpaceModule;
import org.eclipse.sisu.space.URLClassSpace;
import org.eclipse.sisu.wire.WireModule;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.google.inject.name.Names.named;

/**
 * Run shell.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
@Mojo(name="run", requiresProject=false)
public class RunMojo
  extends AbstractMojo
{
  @Parameter(defaultValue = "${project}", readonly = true)
  MavenProject project;

  @Parameter(defaultValue = "${project.basedir}")
  File shellHome;

  @Parameter(defaultValue = "gshell")
  String shellProgram;

  @Parameter(defaultValue = "${project.version}")
  String shellVersion;

  @Parameter
  boolean shellErrors = false;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    try {
      doExecute();
    }
    catch (Exception e) {
      throw new MojoExecutionException(e.getMessage(), e);
    }
  }

  private class BrandingImpl
    extends BrandingSupport
  {
    @Override
    public String getProgramName() {
      return shellProgram;
    }

    @Override
    public String getVersion() {
      return shellVersion;
    }

    @Override
    public String getDisplayName() {
      // TODO: expose for configuration
      return "@|bold GShell|@";
    }

    @Override
    public File getShellHomeDir() {
      // FIXME: this could be null; if not in a directory with a project
      return shellHome;
    }

    @Override
    public File getShellContextDir() {
      // FIXME: this could be null; if not in a directory with a project
      // TODO: expose for configuration
      return project.getBasedir();
    }

    @Override
    public File getUserContextDir() {
      // TODO: expose for configuration
      return resolveFile(new File(getUserHomeDir(), ".m2/gshell/" + getProgramName()));
    }

    @Override
    public License getLicense() {
      // TODO: expose for configuration?
      return new Asl2License();
    }

    @Override
    public String getWelcomeMessage() {
      // TODO: expose for configuration
      PrintBuffer buff = new PrintBuffer();
      buff.format("%nType '@|bold help|@' for more information.%n");
      buff.print(LINE_TOKEN);
      return buff.toString();
    }

    @Override
    public String getGoodbyeMessage() {
      // TODO: expose for configuration
      return "@|green Goodbye!|@\n";
    }
  }

  private void doExecute() throws Exception {
    // TODO: check if we can get a reference to the maven containers BeanLocator?
    final BeanContainer container = new BeanContainer();
    final Terminal terminal = TerminalBuilder.builder().build();
    final IO io = new IO(StreamSet.SYSTEM_FD, terminal);
    final Variables variables = new VariablesSupport();
    // TODO: adapt variables to maven context
    variables.set(VariableNames.SHELL_ERRORS, shellErrors);
    final Branding branding = new BrandingImpl();

    List<Module> modules = new ArrayList<>();
    URLClassSpace space = new URLClassSpace(getClass().getClassLoader());
    modules.add(new SpaceModule(space, BeanScanning.INDEX));

    modules.add(binder -> {
      binder.bind(BeanContainer.class).toInstance(container);
      binder.bind(Branding.class).toInstance(branding);
      binder.bind(IO.class).annotatedWith(named("main")).toInstance(io);
      binder.bind(Variables.class).annotatedWith(named("main")).toInstance(variables);
      binder.bind(LoggingSystem.class).to(LoggingSystemImpl.class);
    });

    Injector injector = Guice.createInjector(new WireModule(modules));
    container.add(injector, 0);

    ShellImpl shell = injector.getInstance(ShellImpl.class);
    shell.start();

    // FIXME: allow more options
    try {
      shell.run();
    }
    catch (ExitNotification e) {
      // ignore
    }
    finally {
      shell.stop();
      io.flush();
      terminal.close();
    }
  }
}
