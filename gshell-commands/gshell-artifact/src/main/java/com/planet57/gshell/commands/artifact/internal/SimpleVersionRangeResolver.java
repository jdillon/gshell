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
import org.eclipse.aether.impl.VersionRangeResolver;
import org.eclipse.aether.resolution.VersionRangeRequest;
import org.eclipse.aether.resolution.VersionRangeResolutionException;
import org.eclipse.aether.resolution.VersionRangeResult;
import org.eclipse.aether.util.version.GenericVersionScheme;
import org.eclipse.aether.version.InvalidVersionSpecificationException;
import org.eclipse.aether.version.VersionConstraint;
import org.eclipse.aether.version.VersionScheme;
import org.sonatype.goodies.common.ComponentSupport;

/**
 * {@code simple} {@link VersionRangeResolver}.
 * 
 * @since 3.0
 */
//@Named("simple")
//@Singleton
public class SimpleVersionRangeResolver
  extends ComponentSupport
  implements VersionRangeResolver
{
  private final VersionScheme versionScheme = new GenericVersionScheme();

  @Override
  public VersionRangeResult resolveVersionRange(final RepositorySystemSession session, final VersionRangeRequest request)
    throws VersionRangeResolutionException
  {
    log.debug("Resolving: {}", request);

    VersionRangeResult result = new VersionRangeResult(request);
    VersionConstraint constraint;
    try {
      constraint = versionScheme.parseVersionConstraint(request.getArtifact().getVersion());
    }
    catch (InvalidVersionSpecificationException e) {
      result.addException(e);
      throw new VersionRangeResolutionException(result);
    }

    result.setVersionConstraint(constraint);
    if (constraint.getRange() == null) {
      result.addVersion(constraint.getVersion());
    }

    return result;
  }
}
