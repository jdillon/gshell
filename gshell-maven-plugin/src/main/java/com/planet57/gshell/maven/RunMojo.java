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

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.planet57.gshell.variables.VariableNames;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.List;

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

  @Parameter(defaultValue = "${gshell.arguments}")
  String[] shellArgs;

  public void execute() throws MojoExecutionException, MojoFailureException {
    // HACK: Need to setup some bootstrap muck
    System.setProperty(VariableNames.SHELL_HOME, shellHome.getAbsolutePath());
    System.setProperty(VariableNames.SHELL_PROGRAM, shellProgram);
    System.setProperty(VariableNames.SHELL_VERSION, shellVersion);

    try {
      ShellRunner runner = new ShellRunner()
      {
        @Override
        protected void configure(List<Module> modules) {
          super.configure(modules);

          // FIXME: see if there is a more dynamic way to bridge components to nested Guice container
          modules.add(new AbstractModule()
          {
            @Override
            protected void configure() {
              bind(MavenProject.class).toInstance(project);
            }
          });
        }
      };

      runner.boot(shellArgs);
    }
    catch (Exception e) {
      throw new MojoExecutionException(e.getMessage(), e);
    }
  }
}
