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

import com.planet57.gshell.command.Command;
import com.planet57.gshell.command.CommandActionSupport;
import com.planet57.gshell.command.CommandContext;
import com.planet57.gshell.repository.RepositoryAccess;
import com.planet57.gshell.util.cli2.Argument;

/**
 * Remove a remote-repository.
 *
 * @since 3.0
 */
@Command(name="artifact/remote-repository/remove", description = "Remove a remote-repository")
public class RemoveRemoteRepositoryAction
  extends CommandActionSupport
{
  @Inject
  private RepositoryAccess repositoryAccess;

  @Argument(index = 0, required = true, description = "Repository identifier", token = "ID")
  private String id;

  @Override
  public Object execute(@Nonnull final CommandContext context) throws Exception {
    repositoryAccess.removeRemoteRepository(id);
    return null;
  }
}
