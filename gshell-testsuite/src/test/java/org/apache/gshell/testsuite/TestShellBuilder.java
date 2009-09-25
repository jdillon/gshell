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

package org.apache.gshell.testsuite;

import jline.Completor;
import org.apache.gshell.Branding;
import org.apache.gshell.Shell;
import org.apache.gshell.Variables;
import org.apache.gshell.console.Console;
import org.apache.gshell.core.impl.ShellImpl;
import org.apache.gshell.execute.CommandExecutor;
import org.apache.gshell.io.IO;
import org.apache.gshell.registry.CommandRegistrar;
import org.apache.maven.shell.ShellBuilder;
import org.codehaus.plexus.DefaultContainerConfiguration;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds {@link org.apache.gshell.Shell} instances.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class TestShellBuilder
    extends ShellBuilder
{
    protected void registerCommand(final String name, final String type) throws Exception {
        CommandRegistrar registrar = getContainer().lookup(CommandRegistrar.class);
        registrar.registerCommand(name, type);
    }
}