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
package com.planet57.gshell.commands.plugin.internal;

import com.google.inject.Injector;
import org.eclipse.aether.artifact.Artifact;

import java.net.URLClassLoader;

import static com.google.common.base.Preconditions.checkNotNull;

// FIXME: find a better name

/**
 * ???
 *
 * @since 3.0
 */
public class PluginRegistration
{
  private final Artifact artifact;

  private final String id;

  private final URLClassLoader classLoader;

  private final Injector injector;

  public PluginRegistration(final Artifact artifact, final URLClassLoader classLoader, final Injector injector) {
    this.artifact = checkNotNull(artifact);
    // TODO: id may be better off w/o version
    this.id = String.format("%s:%s:%s", artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion());
    this.classLoader = checkNotNull(classLoader);
    this.injector = checkNotNull(injector);
  }

  public String getId() {
    return id;
  }

  public Artifact getArtifact() {
    return artifact;
  }

  public URLClassLoader getClassLoader() {
    return classLoader;
  }

  public Injector getInjector() {
    return injector;
  }

  public String toString() {
    return id;
  }
}
