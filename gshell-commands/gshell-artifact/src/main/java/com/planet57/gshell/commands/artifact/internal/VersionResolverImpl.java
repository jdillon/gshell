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

import javax.inject.Named;
import javax.inject.Singleton;

import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.impl.VersionResolver;
import org.eclipse.aether.resolution.VersionRequest;
import org.eclipse.aether.resolution.VersionResolutionException;
import org.eclipse.aether.resolution.VersionResult;
import org.sonatype.goodies.common.ComponentSupport;

/**
 * ???
 * 
 * @since 3.0
 */
@Named
@Singleton
public class VersionResolverImpl
  extends ComponentSupport
  implements VersionResolver
{
  @Override
  public VersionResult resolveVersion(final RepositorySystemSession session, final VersionRequest request)
    throws VersionResolutionException
  {
    log.debug("Resolving: {}", request);
    VersionResult result = new VersionResult(request).setVersion(request.getArtifact().getVersion());
    if (request.getRepositories().size() > 0) {
      result = result.setRepository(request.getRepositories().get(0));
    }
    return result;
  }
}
