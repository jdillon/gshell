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

import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.impl.ArtifactDescriptorReader;
import org.eclipse.aether.resolution.ArtifactDescriptorException;
import org.eclipse.aether.resolution.ArtifactDescriptorRequest;
import org.eclipse.aether.resolution.ArtifactDescriptorResult;
import org.sonatype.goodies.common.ComponentSupport;

import javax.inject.Named;
import javax.inject.Singleton;

/**
 * ???
 *
 * @since 3.0
 */
@Named
@Singleton
public class ArtifactDescriptorReaderImpl
  extends ComponentSupport
  implements ArtifactDescriptorReader
{
  @Override
  public ArtifactDescriptorResult readArtifactDescriptor(final RepositorySystemSession session, final ArtifactDescriptorRequest request)
    throws ArtifactDescriptorException
  {
    log.debug("Reading: {}", request);

    ArtifactDescriptorResult result = new ArtifactDescriptorResult(request);

    // HACK: testing
    Artifact artifact = new DefaultArtifact("javax.inject:javax.inject:1");

    result.addDependency(new Dependency(artifact, null));

    return result;
  }
}
