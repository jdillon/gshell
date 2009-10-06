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

package org.apache.gshell;

import com.google.inject.Injector;
import org.apache.gshell.core.MainSupport;
import org.apache.gshell.core.completer.AliasNameCompleter;
import org.apache.gshell.core.completer.CommandsCompleter;
import org.apache.gshell.core.console.ConsoleErrorHandlerImpl;
import org.apache.gshell.core.console.ConsolePrompterImpl;
import org.apache.gshell.core.guice.GuiceShellBuilder;
import org.fusesource.jansi.AnsiConsole;
import jline.console.completers.AggregateCompleter;

import java.io.PrintStream;

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
        GuiceShellBuilder builder = new GuiceShellBuilder();
        Injector injector = builder.getInjector();
        
        return builder
                .setBranding(getBranding())
                .setIo(io)
                .setVariables(vars)
                .setPrompter(new ConsolePrompterImpl(vars, getBranding()))
                .setErrorHandler(new ConsoleErrorHandlerImpl(io))
                .addCompleter(new AggregateCompleter(
                        injector.getInstance(AliasNameCompleter.class),
                        injector.getInstance(CommandsCompleter.class)
                ))
                .create();
    }

    public static void main(final String[] args) throws Exception {
        // FIXME: Need to set this here, some other stream muck must be getting in the way
        //        think this must have something to do with StreamSet.SYSTEM
        AnsiConsole.systemInstall();

        // AnsiConsole does not install System.err, so do it ourself
        final PrintStream err = System.err;
        System.setErr(new PrintStream(AnsiConsole.wrapOutputStream(err)));

        try {
            new Main().boot(args);
        }
        finally {
            AnsiConsole.systemUninstall();
            System.setErr(err);
        }
    }
}
