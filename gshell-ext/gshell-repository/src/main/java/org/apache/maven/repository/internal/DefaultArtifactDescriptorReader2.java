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
package org.apache.maven.repository.internal;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.planet57.gshell.repository.internal.RepositoryModule;
import org.apache.maven.model.building.ModelBuilder;
import org.eclipse.aether.impl.ArtifactResolver;
import org.eclipse.aether.impl.RemoteRepositoryManager;
import org.eclipse.aether.impl.RepositoryEventDispatcher;
import org.eclipse.aether.impl.VersionRangeResolver;
import org.eclipse.aether.impl.VersionResolver;
import org.eclipse.aether.spi.log.LoggerFactory;

// SEE: https://github.com/apache/maven/pull/116
// SEE: https://issues.apache.org/jira/browse/MNG-6233

/**
 * Fixes JSR-330 injection of {@link DefaultArtifactDescriptorReader} until Maven folks can sort out the simple fix.
 *
 * @since 3.0
 * @see RepositoryModule
 */
@Named("default")
@Singleton
public class DefaultArtifactDescriptorReader2
    extends DefaultArtifactDescriptorReader
{
  @Inject
  public DefaultArtifactDescriptorReader2(final RemoteRepositoryManager remoteRepositoryManager,
                                          final VersionResolver versionResolver,
                                          final VersionRangeResolver versionRangeResolver,
                                          final ArtifactResolver artifactResolver,
                                          final ModelBuilder modelBuilder,
                                          final RepositoryEventDispatcher repositoryEventDispatcher,
                                          final LoggerFactory loggerFactory)
  {
    super(remoteRepositoryManager, versionResolver, versionRangeResolver, artifactResolver, modelBuilder, repositoryEventDispatcher, loggerFactory);

    // HACK: super ctor ignores the versionRangeResolver parameter
    setVersionRangeResolver(versionRangeResolver);
  }
}
