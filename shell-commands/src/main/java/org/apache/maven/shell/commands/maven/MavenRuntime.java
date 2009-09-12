/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.maven.shell.commands.maven;

import org.apache.maven.artifact.InvalidRepositoryException;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.embedder.Configuration;
import org.apache.maven.embedder.DefaultConfiguration;
import org.apache.maven.embedder.MavenEmbedder;
import org.apache.maven.embedder.MavenEmbedderConsoleLogger;
import org.apache.maven.embedder.MavenEmbedderException;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionResult;
import org.apache.maven.repository.RepositorySystem;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * The default {@link MavenRuntime} component.
 *
 * @version $Rev$ $Date$
 */
@Component(role=MavenRuntime.class)
public class MavenRuntime
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Requirement
    private RepositorySystem repositorySystem;

    private MavenEmbedder embedder;

    public MavenEmbedder getEmbedder() {
        if (embedder == null) {
            try {
                embedder = createEmbedder();
            }
            catch (MavenEmbedderException e) {
                throw new RuntimeException(e);
            }
        }

        return embedder;
    }

    private MavenEmbedder createEmbedder() throws MavenEmbedderException {
        Configuration config = new DefaultConfiguration();
        config.setClassLoader(Thread.currentThread().getContextClassLoader());
        config.setMavenEmbedderLogger(new MavenEmbedderConsoleLogger());
        config.setUserSettingsFile(MavenEmbedder.DEFAULT_USER_SETTINGS_FILE);

        MavenEmbedder embedder = new MavenEmbedder(config);

        log.debug("Created embedder: {}", embedder);

        return embedder;
    }

    public RepositorySystem getRepositorySystem() {
        return repositorySystem;
    }

    public MavenExecutionResult execute(MavenExecutionRequest request) {
        return null;
    }

    protected ArtifactRepository getLocalRepository() throws InvalidRepositoryException
    {
        return repositorySystem.createDefaultLocalRepository();
    }

    protected List<ArtifactRepository> getRemoteRepositories()
        throws InvalidRepositoryException
    {
        return Arrays.asList( repositorySystem.createDefaultRemoteRepository() );
    }

}