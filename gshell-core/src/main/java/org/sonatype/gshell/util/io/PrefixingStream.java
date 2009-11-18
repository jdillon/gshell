/*
 * Copyright (C) 2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sonatype.gshell.util.io;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Prefixes printed lines as instructed to by a given {@link Prefixer}.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 1.0
 */
public class PrefixingStream
    extends PrintStream
{
    private final Prefixer prefixer;

    /**
     * Flag to indicate if we are on a new line or not... start with a new line of course.
     */
    private boolean newline = true;

    public PrefixingStream(final Prefixer prefixer, final OutputStream out, final boolean autoFlush) {
        super(out, autoFlush);

        assert prefixer != null;
        this.prefixer = prefixer;
    }

    public PrefixingStream(final Prefixer prefixer, final OutputStream out) {
        this(prefixer, out, false);
    }

    public PrefixingStream(final String prefix, final OutputStream out, final boolean autoFlush) {
        super(out, autoFlush);

        this.prefixer = new Prefixer() {
            public String prefix(final String context) {
                return prefix;
            }
        };
    }

    public PrefixingStream(final String prefix, final OutputStream out) {
        this(prefix, out, false);
    }

    @Override
    public void write(final byte[] b, final int off, final int len) {
        assert b != null;
        assert off > -1;
        assert len > -1;

        //
        // FIXME: This is super-ughly embedded '\n' handling crap, just kinda hacked my want into this...
        //        so it probably sucks a lot :-P
        //

        /*
        if (len > 1) {
            for (int i=off; i<len; i++) {
                if (b[i] == '\n') {
                    synchronized (this) {
                        // Write out the first bits before the new line
                        write(b, off, i - off);

                        // The write out the new line
                        write('\n');
                        newline = true;

                        // The write out everything else (recurses to find more embedded \n muck)
                        write(b, i + 1, len - i - off - 1);

                        // and then stop
                        return;
                    }
                }
            }
        }
        */

        synchronized (this) {
            // if we are on a new line, then print the prefix
            if (newline) {
                String result = prefixer.prefix(new String(b, off, len));

                // Null result means don't write the prefix
                if (result != null) {
                    byte[] prefix = result.getBytes();
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

    public static interface Prefixer
    {
        String prefix(String context);
    }
}