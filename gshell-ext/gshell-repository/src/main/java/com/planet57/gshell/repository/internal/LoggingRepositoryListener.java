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
package com.planet57.gshell.repository.internal;

import org.eclipse.aether.RepositoryEvent;
import org.eclipse.aether.RepositoryListener;
import org.sonatype.goodies.common.ComponentSupport;

/**
 * Logging {@link RepositoryListener}.
 * 
 * @since 3.0
 */
public class LoggingRepositoryListener
  extends ComponentSupport
  implements RepositoryListener
{
  private void log(final String format, final Object... args) {
    log.debug(format, args);
  }

  //
  // Artifact-descriptor
  //

  @Override
  public void artifactDescriptorInvalid(final RepositoryEvent event) {
    log("Artifact-descriptor invalid: {}", event);
  }

  @Override
  public void artifactDescriptorMissing(final RepositoryEvent event) {
    log("Artifact-descriptor missing: {}", event);
  }

  //
  // Metadata
  //

  @Override
  public void metadataInvalid(final RepositoryEvent event) {
    log("Metadata invalid: {}", event);
  }

  @Override
  public void metadataResolving(final RepositoryEvent event) {
    log("Metadata resolving: {}", event);
  }

  @Override
  public void metadataResolved(final RepositoryEvent event) {
    log("Metadata resolved: {}", event);
  }

  @Override
  public void metadataDownloading(final RepositoryEvent event) {
    log("Metadata downloading: {}", event);
  }

  @Override
  public void metadataDownloaded(final RepositoryEvent event) {
    log("Metadata downloaded: {}", event);
  }

  @Override
  public void metadataInstalling(final RepositoryEvent event) {
    log("Metadata installing: {}", event);
  }

  @Override
  public void metadataInstalled(final RepositoryEvent event) {
    log("Metadata installed: {}", event);
  }

  @Override
  public void metadataDeploying(final RepositoryEvent event) {
    log("Metadata deploying: {}", event);
  }

  @Override
  public void metadataDeployed(final RepositoryEvent event) {
    log("Metadata deployed: {}", event);
  }

  //
  // Artifact
  //

  @Override
  public void artifactResolving(final RepositoryEvent event) {
    log("Artifact resolving: {}", event);
  }

  @Override
  public void artifactResolved(final RepositoryEvent event) {
    log("Artifact resolved: {}", event);
  }

  @Override
  public void artifactDownloading(final RepositoryEvent event) {
    log("Artifact downloading: {}", event);
  }

  @Override
  public void artifactDownloaded(final RepositoryEvent event) {
    log("Artifact downloaded: {}", event);
  }

  @Override
  public void artifactInstalling(final RepositoryEvent event) {
    log("Artifact installing: {}", event);
  }

  @Override
  public void artifactInstalled(final RepositoryEvent event) {
    log("Artifact installed: {}", event);
  }

  @Override
  public void artifactDeploying(final RepositoryEvent event) {
    log("Artifact deploying: {}", event);
  }

  @Override
  public void artifactDeployed(final RepositoryEvent event) {
    log("Artifact deployed: {}", event);
  }
}
