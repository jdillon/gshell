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

package org.apache.maven.shell.io;

import org.apache.maven.shell.ansi.AnsiRenderWriter;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * ANSI-aware {@link IO}.
 *
 * @version $Rev$ $Date$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class AnsiAwareIO
    extends IO
{
    public AnsiAwareIO(final InputStream in, final PrintStream out, final PrintStream err, final boolean autoFlush) {
        super(in, out, err, autoFlush);
    }

    public AnsiAwareIO(final InputStream in, final OutputStream out, final OutputStream err, final boolean autoFlush) {
        super(in, out, err, autoFlush);
    }

    public AnsiAwareIO(final InputStream in, final OutputStream out, final boolean autoFlush) {
        super(in, out, autoFlush);
    }

    public AnsiAwareIO(final InputStream in, final PrintStream out, final PrintStream err) {
        super(in, out, err);
    }

    public AnsiAwareIO(final InputStream in, final PrintStream out, final boolean autoFlush) {
        super(in, out, autoFlush);
    }

    public AnsiAwareIO(final InputStream in, final PrintStream out) {
        super(in, out);
    }

    public AnsiAwareIO() {
        super();
    }
    
    @Override
    protected PrintWriter createWriter(final PrintStream out, final boolean autoFlush) {
        assert out != null;
        return new AnsiRenderWriter(out, autoFlush);
    }
}