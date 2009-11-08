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

package org.sonatype.gshell.command;

import jline.Terminal;
import jline.TerminalFactory;
import org.slf4j.helpers.MessageFormatter;
import org.sonatype.gshell.io.Closer;
import org.sonatype.gshell.io.Flusher;
import org.sonatype.gshell.io.StreamSet;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;

/**
 * Provides access to input/output handles.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class IO
{
    public final StreamSet streams;

    /**
     * Input reader.
     */
    public final Reader in;

    /**
     * Output writer.
     */
    public final PrintWriter out;

    /**
     * Error output writer.
     */
    public final PrintWriter err;

    /**
     * The verbosity setting, which commands (and framework) should inspect and respect when
     * spitting up output to the user.
     */
    private Verbosity verbosity = Verbosity.INFO;

    public IO(final StreamSet streams, final boolean autoFlush) {
        assert streams != null;

        this.streams = streams;
        this.in = createReader(streams.in);
        this.out = createWriter(streams.out, autoFlush);

        /// Don't rewrite the error stream if we have the same stream for out and error
        if (streams.isOutputCombined()) {
            this.err = this.out;
        }
        else {
            this.err = createWriter(streams.err, autoFlush);
        }
    }

    /**
     * Helper which uses current values from {@link StreamSet#system}.
     */
    public IO() {
        this(StreamSet.system(), true);
    }

    protected Reader createReader(final InputStream in) {
        assert in != null;

        return new InputStreamReader(in);
    }

    protected PrintWriter createWriter(final PrintStream out, final boolean autoFlush) {
        assert out != null;

        return new PrintWriter(out, autoFlush);
    }

    public Terminal getTerminal() {
        return TerminalFactory.get();
    }

    /**
     * Set the verbosity level.
     */
    public void setVerbosity(final Verbosity verbosity) {
        assert verbosity != null;

        this.verbosity = verbosity;
    }

    /**
     * Returns the verbosity level.
     */
    public Verbosity getVerbosity() {
        return verbosity;
    }

    /**
     * Check if the verbosity level is set to {@link Verbosity#SILENT}.
     */
    public boolean isSilent() {
        return verbosity == Verbosity.SILENT;
    }

    /**
     * Check if the verbosity level is set to {@link Verbosity#QUIET}.
     */
    public boolean isQuiet() {
        return verbosity == Verbosity.QUIET;
    }

    /**
     * Check if the verbosity level is set to {@link Verbosity#INFO}.
     */
    public boolean isInfo() {
        return verbosity == Verbosity.INFO;
    }

    /**
     * Check if the verbosity level is set to {@link Verbosity#VERBOSE}.
     */
    public boolean isVerbose() {
        return verbosity == Verbosity.VERBOSE;
    }

    /**
     * Check if the verbosity level is set to {@link Verbosity#DEBUG}.
     * <p/>
     * <p>For generaly usage, when debug output is required, it is better
     * to use the logging facility instead.
     */
    public boolean isDebug() {
        return verbosity == Verbosity.DEBUG;
    }

    /**
     * Flush both output streams.
     */
    public void flush() {
        Flusher.flush(out);

        // Only attempt to flush the err stream if we aren't sharing it with out
        if (!streams.isOutputCombined()) {
            Flusher.flush(err);
        }
    }

    /**
     * Close all streams.
     */
    public void close() throws IOException {
        Closer.close(in, out);

        // Only attempt to close the err stream if we aren't sharing it with out
        if (!streams.isOutputCombined()) {
            Closer.close(err);
        }
    }

    //
    // Verbosity
    //

    /**
     * Defines the valid values of the {@link IO} containers verbosity settings.
     */
    public static enum Verbosity
    {
        SILENT,
        QUIET,
        INFO,
        VERBOSE,
        DEBUG
    }

    //
    // Output Helpers
    //

    public void debug(final Object msg) {
        if (isDebug()) {
            out.println(msg);
        }
    }

    public void debug(final String format, final Object... args) {
        if (isDebug()) {
            out.println(MessageFormatter.arrayFormat(format, args));
        }
    }

    public void verbose(final Object msg) {
        if (isVerbose()) {
            out.println(msg);
        }
    }

    public void verbose(final String format, final Object... args) {
        if (isVerbose()) {
            out.println(MessageFormatter.arrayFormat(format, args));
        }
    }

    public void info(final Object msg) {
        if (!isQuiet()) {
            out.println(msg);
        }
    }

    public void info(final String format, final Object... args) {
        if (!isQuiet()) {
            out.println(MessageFormatter.arrayFormat(format, args));
        }
    }

    public void warn(final Object msg) {
        if (!isQuiet()) {
            err.println(msg);
        }
    }

    public void warn(final String format, final Object... args) {
        if (!isQuiet()) {
            err.println(MessageFormatter.arrayFormat(format, args));
        }
    }

    public void error(final Object msg) {
        err.println(msg);
    }

    public void error(final String format, final Object... args) {
        err.println(MessageFormatter.arrayFormat(format, args));
    }
}
