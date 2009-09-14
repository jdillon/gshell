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

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Prefixes printed lines as instructed to by a given prefix rendering closure.
 *
 * @version $Rev$ $Date$
 */
public class PrefixingStream
    extends PrintStream
{
    private final String prefix;

    /**
     * Flag to indicate if we are on a new line or not... start with a new line of course.
     */
    private boolean newline = true;

    public PrefixingStream(final String prefix, final OutputStream out, final boolean autoFlush) {
        super(out, autoFlush);

        this.prefix = prefix;
    }

    public PrefixingStream(final String prefix, final OutputStream out) {
        this(prefix, out, false);
    }

    public void write(final byte[] b, final int off, final int len) {
        assert b != null;
        assert off > -1;
        assert len > -1;

        synchronized (this) {
            // if we are on a new line, then print the prefix
            if (newline) {
                Object result = prefix;

                // Null result means don't write the prefix
                if (result != null) {
                    byte[] prefix = String.valueOf(result).getBytes();
                    super.write(prefix, 0, prefix.length);
                }

                newline = false;
            }

            super.write(b, off, len);
        }

        // Check if there is a new line in the given string to reset the newline flag
        for (int i=off; i<len; i++) {
            if (b[i] == '\n') {
                synchronized (this) {
                    newline = true;
                }
                break;
            }
        }
    }
}