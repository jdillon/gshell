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

import org.apache.maven.exception.ExceptionSummary;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionResult;
import org.apache.maven.shell.Command;
import org.apache.maven.shell.CommandContext;
import org.apache.maven.shell.CommandSupport;
import org.apache.maven.shell.OpaqueArguments;
import org.apache.maven.shell.io.IO;
import org.apache.maven.cli.MavenCli;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.classworlds.ClassWorld;

/**
 * The <tt>mvn</tt> command.
 *
 * @version $Rev$ $Date$
 */
@Component(role=Command.class, hint="mvn", instantiationStrategy="per-lookup")
public class MavenCommand
    extends CommandSupport
    implements OpaqueArguments
{
    // @Requirement
    // private MavenRuntime runtime;
    
    public String getName() {
        return "mvn";
    }

    @Override
    public Object execute(final CommandContext context) throws Exception {
        assert context != null;

        IO io = context.getIo();

        /*
        MavenExecutionRequest request = new DefaultMavenExecutionRequest();

        MavenExecutionResult result = runtime.getEmbedder().execute(request);

        if (result.hasExceptions()) {
            ExceptionSummary es = result.getExceptionSummary();

            if (es == null) {
                io.error("", result.getExceptions().get(0));
            }
            else {
                //noinspection ThrowableResultOfMethodCallIgnored
                io.error(es.getMessage(), es.getException());
            }

            return Result.FAILURE;
        }
        */

        ClassWorld classWorld = new ClassWorld("plexus.core", Thread.currentThread().getContextClassLoader());
        return MavenCli.main(context.getArguments(), classWorld);
    }
}