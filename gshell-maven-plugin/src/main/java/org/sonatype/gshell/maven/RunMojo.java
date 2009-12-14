/*
 * Copyright (C) 2009 the original author(s).
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

package org.sonatype.gshell.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.sonatype.gshell.vars.VariableNames;

/**
 * ???
 *
 * @goal run
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class RunMojo
    extends AbstractMojo
    implements VariableNames
{
    public void execute() throws MojoExecutionException, MojoFailureException {
        // HACK: Need to setup some bootstrap muck
        System.setProperty(SHELL_HOME, System.getProperty("user.home") + "/.m2/gshell");
        System.setProperty(SHELL_PROGRAM, "gshell-maven-plugin");
        System.setProperty(SHELL_VERSION, "???");

        try {
            ShellRunner runner = new ShellRunner();
            runner.boot();
        }
        catch (Exception e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }
}