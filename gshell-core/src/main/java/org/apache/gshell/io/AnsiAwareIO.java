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

package org.apache.gshell.io;

import org.apache.gshell.ansi.AnsiRenderWriter;
import org.fusesource.jansi.AnsiConsole;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.OutputStream;
import java.lang.reflect.Method;

/**
 * ANSI-aware {@link IO}.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 *
 * @since 2.0
 */
public class AnsiAwareIO
    extends IO
{
    public AnsiAwareIO(StreamSet streams, boolean autoFlush) {
        super(streams, autoFlush);
    }

    public AnsiAwareIO() {
        super();
    }

    @Override
    protected PrintWriter createWriter(final PrintStream out, final boolean autoFlush) {
        assert out != null;
        return new AnsiRenderWriter(wrap(unwrap(out)), autoFlush);
    }

    //
    // These bits lifted from Karaf's ConsoleFactory
    //

    private static PrintStream wrap(final PrintStream out) {
        OutputStream stream = AnsiConsole.wrapOutputStream(out);
        if (stream instanceof PrintStream) {
            return ((PrintStream) stream);
        }
        else {
            return new PrintStream(stream);
        }
    }

    @SuppressWarnings({ "unchecked" })
    private static <T> T unwrap(final T stream) {
        try {
            Method method = stream.getClass().getMethod("getRoot");
            return (T) method.invoke(stream);
        }
        catch (Throwable t) {
            return stream;
        }
    }
}