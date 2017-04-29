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

import java.util.List;

import com.google.inject.Module;
import com.planet57.gshell.branding.Branding;
import com.planet57.gshell.logging.LoggingSystem;
import com.planet57.gshell.logging.logback.LogbackLoggingSystem;

import javax.annotation.Nonnull;

/**
 * Command-line bootstrap for GShell (<tt>gsh</tt>).
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class Main
    extends MainSupport
{
  @Override
  protected Branding createBranding() {
    return new BrandingImpl();
  }

  @Override
  protected void configure(@Nonnull final List<Module> modules) {
    super.configure(modules);
    modules.add(binder -> {
      binder.bind(LoggingSystem.class).to(LogbackLoggingSystem.class);
    });
  }

  public static void main(final String[] args) throws Exception {
    new Main().boot(args);
  }
}