/*
 * Copyright (c) 2009-2011 the original author or authors.
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
package org.sonatype.gshell.commands.artifact;

import javax.inject.Inject;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.ArtifactResolutionRequest;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.artifact.resolver.filter.ScopeArtifactFilter;
import org.apache.maven.repository.RepositorySystem;
import org.sonatype.gshell.artifact.monitor.ProgressSpinnerMonitor;
import org.sonatype.gshell.command.Command;
import org.sonatype.gshell.command.support.CommandActionSupport;
import org.sonatype.gshell.command.CommandContext;
import org.sonatype.gshell.command.IO;
import org.sonatype.gshell.plexus.PlexusRuntime;
import org.sonatype.gshell.util.cli2.Argument;
import org.sonatype.gshell.util.cli2.Option;

import java.util.Collections;
import java.util.Set;

/**
 * Resolve repository artifacts.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
@Command(name = "artifact/resolve")
public class ResolveCommand
    extends CommandActionSupport
{
    private final PlexusRuntime plexus;

    @Option(name = "t", longName="type")
    private String type = "jar";

    @Option(name = "c", longName="classifier")
    private String classifier;

    @Option(name = "s", longName="scope")
    private String scope;

    @Option(name = "T", longName="transitive", optionalArg=true)
    private boolean transitive;

    @Option(name = "o", longName="offline", optionalArg=true)
    private boolean offline;

    @Argument(required = true)
    private String resolveId;

    @Inject
    public ResolveCommand(final PlexusRuntime plexus) {
        assert plexus != null;
        this.plexus = plexus;
    }

    public Object execute(final CommandContext context) throws Exception {
        assert context != null;
        IO io = context.getIo();

        RepositorySystem rsys = plexus.lookup(RepositorySystem.class);

        ArtifactResolutionRequest request = new ArtifactResolutionRequest();

        String[] items = resolveId.split(":", 3);
        if (items.length != 3) {
            io.error("Invalid artifact resolution id: {}", resolveId); // TODO: i18n
            return Result.FAILURE;
        }

        String groupId = items[0];
        String artifactId = items[1];
        String version = items[2];

        Artifact artifact;
        if (classifier != null) {
            artifact = rsys.createArtifactWithClassifier(groupId, artifactId, version, type, classifier);
        }
        else {
            artifact = rsys.createArtifact(groupId, artifactId, version, type);
        }
        io.println("Resolving artifact: {}", artifact); // TODO: i18n

        //
        // TODO: Bring the ArtifactManager/ArtifactRepsitoryManager back to manage these components

        request.setLocalRepository(rsys.createDefaultLocalRepository());
        request.setRemoteRepositories(Collections.singletonList(rsys.createDefaultRemoteRepository()));

        request.setResolveRoot(true);
        request.setResolveTransitively(transitive);
        request.setArtifact(artifact);

        if (scope != null) {
            io.debug("Using scope: {}", scope);
            request.setCollectionFilter(new ScopeArtifactFilter(scope));
        }

        request.setOffline(offline);
        request.setTransferListener(new ProgressSpinnerMonitor(io));
        ArtifactResolutionResult result = rsys.resolve(request);

        Set<Artifact> artifacts = result.getArtifacts();
        io.println("Resolved artifacts:"); // TODO: i18n
        for (Artifact a : artifacts) {
            io.println("    {}", a);
        }

        return Result.SUCCESS;
    }
}