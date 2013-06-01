/*
 * Copyright (c) 2009-2011 the original author or authors.
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
package org.sonatype.gshell.util.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

/**
 * Stream/Reader/Writer copy helpers.
 *
 * Based on plexus-utils IOUtil.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.6.4
 */
public class Copier
{
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    /**
     * Copy bytes from an <code>InputStream</code> to an <code>OutputStream</code>.
     */
    public static void copy(final InputStream input, final OutputStream output) throws IOException {
        copy(input, output, DEFAULT_BUFFER_SIZE);
    }

    /**
     * Copy bytes from an <code>InputStream</code> to an <code>OutputStream</code>.
     *
     * @param bufferSize Size of internal buffer to use.
     */
    public static void copy(final InputStream input, final OutputStream output, final int bufferSize) throws IOException {
        final byte[] buffer = new byte[bufferSize];
        int n;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
    }

    /**
     * Copy chars from a <code>Reader</code> to a <code>Writer</code>.
     */
    public static void copy(final Reader input, final Writer output) throws IOException {
        copy(input, output, DEFAULT_BUFFER_SIZE);
    }

    /**
     * Copy chars from a <code>Reader</code> to a <code>Writer</code>.
     *
     * @param bufferSize Size of internal buffer to use.
     */
    public static void copy(final Reader input, final Writer output, final int bufferSize) throws IOException {
        final char[] buffer = new char[bufferSize];
        int n;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        output.flush();
    }

    /**
     * Copy and convert bytes from an <code>InputStream</code> to chars on a <code>Writer</code>.
     * The platform's default encoding is used for the byte-to-char conversion.
     */
    public static void copy(final InputStream input, final Writer output) throws IOException {
        copy(input, output, DEFAULT_BUFFER_SIZE);
    }

    /**
     * Copy and convert bytes from an <code>InputStream</code> to chars on a <code>Writer</code>.
     * The platform's default encoding is used for the byte-to-char conversion.
     *
     * @param bufferSize Size of internal buffer to use.
     */
    public static void copy(final InputStream input, final Writer output, final int bufferSize) throws IOException {
        final InputStreamReader in = new InputStreamReader(input);
        copy(in, output, bufferSize);
    }

    /**
     * Copy and convert bytes from an <code>InputStream</code> to chars on a <code>Writer</code>, using the specified encoding.
     *
     * @param encoding The name of a supported character encoding. See the <a href="http://www.iana.org/assignments/character-sets">IANA Charset
     *                 Registry</a> for a list of valid encoding types.
     */
    public static void copy(final InputStream input, final Writer output, final String encoding) throws IOException {
        final InputStreamReader in = new InputStreamReader(input, encoding);
        copy(in, output);
    }

    /**
     * Copy and convert bytes from an <code>InputStream</code> to chars on a <code>Writer</code>, using the specified encoding.
     *
     * @param encoding   The name of a supported character encoding. See the <a href="http://www.iana.org/assignments/character-sets">IANA Charset
     *                   Registry</a> for a list of valid encoding types.
     * @param bufferSize Size of internal buffer to use.
     */
    public static void copy(final InputStream input, final Writer output, final String encoding, final int bufferSize) throws IOException {
        final InputStreamReader in = new InputStreamReader(input, encoding);
        copy(in, output, bufferSize);
    }
}