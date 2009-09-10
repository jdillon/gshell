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

package org.apache.maven.shell.console;

import jline.CandidateListCompletionHandler;
import jline.Completor;
import jline.ConsoleReader;
import jline.History;

import java.io.IOException;
import java.io.PrintWriter;

import org.apache.maven.shell.io.IO;

/**
 * Support for running console using the <a href="http://jline.sf.net">JLine</a> library.
 *
 * @version $Rev$ $Date$
 */
public class JLineConsole
    extends Console
{
    private final ConsoleReader reader;

    public JLineConsole(final Executor executor, final IO io) throws IOException {
        super(executor);

        assert io != null;

        // TODO: Expose bindings, and/or setup the default to load from our configuration
        /*
        This is what ConsoleReader is doing related to bindings...

        if (bindings == null) {
            try {
                String bindingFile = System.getProperty("jline.keybindings",
                    new File(System.getProperty("user.home",
                        ".jlinebindings.properties")).getAbsolutePath());

                if (new File(bindingFile).isFile()) {
                    bindings = new FileInputStream(new File(bindingFile));
                }
            } catch (Exception e) {
                // swallow exceptions with option debugging
                if (debugger != null) {
                    e.printStackTrace(debugger);
                }
            }
        }

        if (bindings == null) {
            bindings = terminal.getDefaultBindings();
        }
        */

        reader = new ConsoleReader(io.inputStream, new PrintWriter(io.outputStream, true), /*bindings*/null, io.getTerminal());
        reader.setUsePagination(true);
        if (Boolean.getBoolean("jline.nobell")) {
            reader.setBellEnabled(false);
        }
        reader.setCompletionHandler(new CandidateListCompletionHandler());
    }

    public void addCompleter(final Completor completer) {
        assert completer != null;

        reader.addCompletor(completer);
    }

    public void setHistory(final History history) {
        assert history != null;

        reader.setHistory(history);
    }

    protected String readLine(final String prompt) throws IOException {
        // prompt may be null

        return reader.readLine(prompt);
    }
}