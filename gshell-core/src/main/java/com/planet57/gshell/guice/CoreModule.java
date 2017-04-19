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

import java.io.IOException;

import org.fusesource.jansi.AnsiRenderer;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.planet57.gshell.command.IO;
import com.planet57.gshell.command.registry.CommandRegistrar;
import com.planet57.gshell.command.registry.CommandRegistrarImpl;
import com.planet57.gshell.shell.Shell;
import com.planet57.gshell.shell.ShellHolder;
import com.planet57.gshell.util.io.PromptReader;
import com.planet57.gshell.variables.Variables;

import jline.Terminal;

/**
 * GShell core module.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
public class CoreModule
    extends AbstractModule
{
  @Override
  protected void configure() {
    // FIXME: for some reason, this is required, @Named is not working for this one component
    bind(CommandRegistrar.class).to(CommandRegistrarImpl.class);
  }

  @Provides
  private Shell provideShell() {
    return ShellHolder.get();
  }

  @Provides
  private IO provideIo() {
    return provideShell().getIo();
  }

  @Provides
  private Variables provideVariables() {
    return provideShell().getVariables();
  }

  @Provides
  private Terminal provideTerminal() {
    return provideIo().getTerminal();
  }

  @Provides
  private PromptReader providePromptReader() throws IOException {
    IO io = provideIo();

    return new PromptReader(io.streams, io.getTerminal())
    {
      @Override
      public String readLine(String prompt, Validator validator) throws IOException {
        return super.readLine(AnsiRenderer.render(prompt), validator);
      }

      @Override
      public String readLine(String prompt, char mask, Validator validator) throws IOException {
        return super.readLine(AnsiRenderer.render(prompt), mask, validator);
      }

      @Override
      public String readPassword(String prompt, Validator validator) throws IOException {
        return super.readPassword(AnsiRenderer.render(prompt), validator);
      }
    };
  }
}
