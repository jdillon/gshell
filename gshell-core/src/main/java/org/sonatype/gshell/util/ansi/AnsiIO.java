/*
 * Copyright (C) 2009 the original author(s).
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

package org.sonatype.gshell.util.ansi;

import org.fusesource.jansi.AnsiConsole;
import org.fusesource.jansi.AnsiRenderWriter;
import org.sonatype.gshell.command.IO;
import org.sonatype.gshell.io.StreamSet;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * ANSI-aware {@link org.sonatype.gshell.command.IO}.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class AnsiIO
    extends IO
{
    public AnsiIO(final StreamSet streams, final boolean autoFlush) {
        super(ansiStreams(streams), autoFlush);
    }

    public AnsiIO() {
        this(StreamSet.system(), true);
    }

    private static StreamSet ansiStreams(final StreamSet streams) {
        assert streams != null;
        return new StreamSet(streams.in, wrap(streams.out), wrap(streams.err));
    }

    private static PrintStream wrap(final PrintStream stream) {
        assert stream != null;
        return new PrintStream(AnsiConsole.wrapOutputStream(stream));
    }

    @Override
    protected PrintWriter createWriter(final PrintStream out, final boolean autoFlush) {
        assert out != null;
        return new AnsiRenderWriter(out, autoFlush);
    }
}