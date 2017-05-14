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
package com.planet57.gshell.commands.artifact.internal;

import com.google.inject.Binder;
import com.google.inject.Module;
import org.eclipse.aether.impl.guice.AetherModule;

import javax.inject.Named;

/**
 * Artifact module.
 *
 * @since 3.0
 */
@Named
public class ArtifactModule
  implements Module
{
  @Override
  public void configure(final Binder binder) {
    binder.install(new AetherModule());
  }
}
