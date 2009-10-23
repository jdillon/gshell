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

package org.sonatype.gshell;

import org.sonatype.gshell.core.MainSupport;
import org.sonatype.gshell.core.console.ConsoleErrorHandlerImpl;
import org.sonatype.gshell.core.console.ConsolePrompterImpl;
import org.sonatype.gshell.core.simple.SimpleShellBuilder;

/**
 * Command-line bootstrap for GShell (<tt>gsh</tt>).
 * 
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class Main
    extends MainSupport
{
    @Override
    protected Branding createBranding() {
        return new BrandingImpl();
    }

    @Override
    protected Shell createShell() throws Exception {
        SimpleShellBuilder builder = new SimpleShellBuilder();
        
        return builder
                .setBranding(getBranding())
                .setIo(io)
                .setVariables(vars)
                .setPrompter(new ConsolePrompterImpl(vars, getBranding()))
                .setErrorHandler(new ConsoleErrorHandlerImpl(io))
                .create();
    }

    public static void main(final String[] args) throws Exception {
        new Main().boot(args);
    }
}
