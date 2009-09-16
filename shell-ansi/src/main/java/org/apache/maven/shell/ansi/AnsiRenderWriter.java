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

package org.apache.maven.shell.ansi;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;

/**
 * Print writer implementation which supports automatic ANSI color rendering
 *
 * @version $Rev$ $Date$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class AnsiRenderWriter
    extends PrintWriter
{
    private final AnsiRenderer renderer = new AnsiRenderer();

    public AnsiRenderWriter(final OutputStream out) {
        super(out);
    }

    public AnsiRenderWriter(final OutputStream out, final boolean autoFlush) {
        super(out, autoFlush);
    }

    public AnsiRenderWriter(final Writer out) {
        super(out);
    }

    public AnsiRenderWriter(final Writer out, final boolean autoFlush) {
        super(out, autoFlush);
    }

    public void write(final String s) {
        if (AnsiRenderer.test(s)) {
            super.write(renderer.render(s));
        }
        else {
            super.write(s);
        }
    }
}
