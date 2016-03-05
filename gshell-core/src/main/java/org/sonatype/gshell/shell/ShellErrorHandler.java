/*
 * Copyright (c) 2009-present the original author or authors.
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
package org.sonatype.gshell.shell;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Named;
import org.sonatype.gshell.command.IO;
import org.sonatype.gshell.console.ConsoleErrorHandler;
import org.sonatype.gshell.notification.ErrorNotification;
import org.sonatype.gshell.util.i18n.MessageSource;
import org.sonatype.gshell.util.i18n.ResourceBundleMessageSource;
import org.sonatype.gshell.variables.Variables;

import static org.fusesource.jansi.Ansi.Attribute.*;
import static org.fusesource.jansi.Ansi.Color.*;
import static org.fusesource.jansi.Ansi.*;
import static org.sonatype.gshell.variables.VariableNames.*;

/**
 * Shell {@link ConsoleErrorHandler} which renders errors with ANSI codes.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class ShellErrorHandler
    implements ConsoleErrorHandler
{
    private static enum Messages
    {
        ERROR_AT,
        ERROR_CAUSED_BY,
        ERROR_LOCATION_NATIVE,
        ERROR_LOCATION_UNKNOWN;

        private static final MessageSource messages = new ResourceBundleMessageSource(ShellErrorHandler.class);

        String format(final Object... args) {
            return messages.format(name(), args);
        }
    }

    private final IO io;

    private final Provider<Variables> variables;

    @Inject
    public ShellErrorHandler(final @Named("main") IO io, final Provider<Variables> variables) {
        assert io != null;
        this.io = io;
        assert variables != null;
        this.variables = variables;
    }

    public boolean handleError(final Throwable error) {
        assert error != null;
        displayError(error);
        return true;
    }

    private void displayError(final Throwable error) {
        assert error != null;

        Throwable cause = error;
        if (error instanceof ErrorNotification) {
            cause = error.getCause();
        }

        Variables vars = variables.get();

        // Determine if the stack trace flag is set
        boolean showTrace = false;
        if (vars.contains(SHELL_ERRORS)) {
            showTrace = vars.get(SHELL_ERRORS, Boolean.class);
        }

        if (showTrace || !io.isSilent()) {
            io.err.print(ansi().a(INTENSITY_BOLD).fg(RED).a(cause.getClass().getName()).reset());
            if (cause.getMessage() != null) {
                io.err.print(": ");
                io.err.print(ansi().a(INTENSITY_BOLD).fg(RED).a(cause.getMessage()).reset());
            }
            io.err.println();
        }

        if (showTrace) {
            while (cause != null) {
                for (StackTraceElement e : cause.getStackTrace()) {
                    io.err.print("    ");
                    io.err.print(ansi().a(INTENSITY_BOLD).a(Messages.ERROR_AT.format()).reset().a(" ").a(e.getClassName()).a(".").a(e.getMethodName()));
                    io.err.print(ansi().a(" (").a(INTENSITY_BOLD).a(getLocation(e)).reset().a(")"));
                    io.err.println();
                }

                cause = cause.getCause();
                if (cause != null) {
                    io.err.print(ansi().a(INTENSITY_BOLD).a(Messages.ERROR_CAUSED_BY.format()).reset().a(" ").a(cause.getClass().getName()));
                    if (cause.getMessage() != null) {
                        io.err.print(": ");
                        io.err.print(ansi().a(INTENSITY_BOLD).fg(RED).a(cause.getMessage()).reset());
                    }
                    io.err.println();
                }
            }
        }

        io.err.flush();
    }

    private String getLocation(final StackTraceElement e) {
        assert e != null;

        if (e.isNativeMethod()) {
            return Messages.ERROR_LOCATION_NATIVE.format();
        }
        else if (e.getFileName() == null) {
            return Messages.ERROR_LOCATION_UNKNOWN.format();
        }
        else if (e.getLineNumber() >= 0) {
            return String.format("%s:%s", e.getFileName(), e.getLineNumber());
        }
        else {
            return e.getFileName();
        }
    }
}