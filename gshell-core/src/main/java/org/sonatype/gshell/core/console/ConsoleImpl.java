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

package org.sonatype.gshell.core.console;

import jline.console.CandidateListCompletionHandler;
import jline.console.Completer;
import jline.console.ConsoleReader;
import jline.console.History;
import jline.console.MemoryHistory;
import org.sonatype.gshell.console.Console;
import org.sonatype.gshell.command.IO;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

/**
 * Support for running console using the <a href="http://jline.sf.net">JLine</a> library.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 *
 * @since 2.0
 */
public class ConsoleImpl
    extends Console
{
    private final ConsoleReader reader;

    public ConsoleImpl(final Executor executor, final IO io, final History history, final InputStream bindings) throws IOException {
        super(executor);
        assert io != null;

        reader = new ConsoleReader(
            io.streams.in,
            new PrintWriter(io.streams.out, true),
            bindings,
            io.getTerminal());

        reader.setPaginationEnabled(true);
        reader.setCompletionHandler(new CandidateListCompletionHandler());
        reader.setHistory(history != null ? history : new MemoryHistory());
    }

    public ConsoleImpl(final Executor executor, final IO io) throws IOException {
        this(executor, io, null, null);
    }

    public ConsoleReader getReader() {
        return reader;
    }
    
    public void addCompleter(final Completer completer) {
        assert completer != null;
        reader.addCompleter(completer);
    }

    @Override
    protected String readLine(final String prompt) throws IOException {
        // prompt may be null
        return reader.readLine(prompt);
    }
}