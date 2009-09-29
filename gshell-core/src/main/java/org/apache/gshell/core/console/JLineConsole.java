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

package org.apache.gshell.core.console;

import jline.CandidateListCompletionHandler;
import jline.Completor;
import jline.ConsoleReader;
import jline.History;
import org.apache.gshell.console.Console;
import org.apache.gshell.io.IO;
import org.apache.gshell.terminal.Constants;

import java.io.IOException;
import java.io.InputStream;

/**
 * Support for running console using the <a href="http://jline.sf.net">JLine</a> library.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 *
 * @since 2.0
 */
public class JLineConsole
    extends Console
    implements Constants
{
    private final ConsoleReader reader;

    public JLineConsole(final Executor executor, final IO io, final History history, final InputStream bindings) throws IOException {
        super(executor);
        assert io != null;

        reader = io.createConsoleReader(bindings);
        reader.setUsePagination(true);
        if (Boolean.getBoolean(JLINE_NOBELL)) {
            reader.setBellEnabled(false);
        }
        reader.setCompletionHandler(new CandidateListCompletionHandler());
        reader.setHistory(history != null ? history : new History());
    }

    public JLineConsole(final Executor executor, final IO io) throws IOException {
        this(executor, io, null, null);
    }

    public ConsoleReader getReader() {
        return reader;
    }

    public void addCompleter(final Completor completer) {
        assert completer != null;
        reader.addCompletor(completer);
    }

    protected String readLine(final String prompt) throws IOException {
        // prompt may be null
        return reader.readLine(prompt);
    }
}