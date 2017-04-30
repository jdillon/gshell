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
package com.planet57.gshell.shell;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.assistedinject.FactoryModuleBuilder;

import javax.inject.Named;

/**
 * ???
 *
 * @since 3.0
 */
@Named
public class ShellModule
  implements Module
{
  @Override
  public void configure(final Binder binder) {
    binder.install(new FactoryModuleBuilder()
      .implement(Shell.class, ShellImpl.class)
      .build(ShellFactory.class)
    );
  }
}
