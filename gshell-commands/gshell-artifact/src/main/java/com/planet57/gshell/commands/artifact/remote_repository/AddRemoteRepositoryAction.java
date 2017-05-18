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
package com.planet57.gshell.commands.artifact.remote_repository;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import com.planet57.gshell.util.cli2.Option;
import org.eclipse.aether.repository.RemoteRepository;
import com.planet57.gshell.command.Command;
import com.planet57.gshell.command.CommandActionSupport;
import com.planet57.gshell.command.CommandContext;
import com.planet57.gshell.repository.RepositoryAccess;
import com.planet57.gshell.util.cli2.Argument;

/**
 * Add a remote-repository.
 *
 * @since 3.0
 */
@Command(name="artifact/remote-repository/add", description = "Add a remote-repository")
public class AddRemoteRepositoryAction
  extends CommandActionSupport
{
  @Inject
  private RepositoryAccess repositoryAccess;

  @Option(name="t", longName = "type", description = "Repository type", token = "TYPE")
  private String type = "default";

  @Argument(index = 0, required = true, description = "Repository identifier", token = "ID")
  private String id;

  @Argument(index = 1, required = true, description = "Repository URL", token = "URL")
  private String url;

  /*
//      // default add central
//      RemoteRepository central = new RemoteRepository.Builder("central", "default", "https://repo1.maven.org/maven2")
//        // disable snapshots
//        .setSnapshotPolicy(new RepositoryPolicy(false, RepositoryPolicy.UPDATE_POLICY_NEVER, RepositoryPolicy.CHECKSUM_POLICY_IGNORE))
//        .build();
//      repositories.add(central);
//
//      // default add local ~/.m2/repository
//      File homeDir = new File(System.getProperty("user.home"));
//      String location = new File(homeDir, ".m2/repository").toURI().toASCIIString();
//      RemoteRepository mavenLocal = new RemoteRepository.Builder("maven-local", "default", location)
//        .setReleasePolicy(new RepositoryPolicy(true, RepositoryPolicy.UPDATE_POLICY_NEVER, RepositoryPolicy.CHECKSUM_POLICY_IGNORE))
//        .setSnapshotPolicy(new RepositoryPolicy(true, RepositoryPolicy.UPDATE_POLICY_NEVER, RepositoryPolicy.CHECKSUM_POLICY_IGNORE))
//        .build();
//      repositories.add(mavenLocal);
   */

  @Override
  public Object execute(@Nonnull final CommandContext context) throws Exception {
    RemoteRepository.Builder builder = new RemoteRepository.Builder(id, type, url);
    repositoryAccess.addRemoteRepository(builder.build());
    return null;
  }
}
