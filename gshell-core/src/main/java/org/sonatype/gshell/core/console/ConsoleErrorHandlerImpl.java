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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.gshell.ShellHolder;
import org.sonatype.gshell.VariableNames;
import org.sonatype.gshell.Variables;
import org.sonatype.gshell.command.IO;
import org.sonatype.gshell.console.Console;
import org.sonatype.gshell.i18n.MessageSource;
import org.sonatype.gshell.i18n.ResourceBundleMessageSource;
import org.sonatype.gshell.notification.ErrorNotification;

/**
 * {@link org.sonatype.gshell.console.Console.ErrorHandler} component.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 *
 * @since 2.0
 */
public class ConsoleErrorHandlerImpl
    implements Console.ErrorHandler, VariableNames
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private static final String ERROR_EXCEPTION_NAME = "error.exception.name";

    private static final String ERROR_EXCEPTION_AT = "error.exception.at";

    private static final String ERROR_EXCEPTION_CAUSEBY = "error.exception.causeby";

    private static final String ERROR_LOCATION_NATIVE = "error.location.native";

    private static final String ERROR_LOCATION_UNKNOWN = "error.location.unknown";

    private final IO io;

    private final MessageSource messages = new ResourceBundleMessageSource(getClass());

    public ConsoleErrorHandlerImpl(final IO io) {
        assert io != null;
        this.io = io;
    }

    public Console.Result handleError(final Throwable error) {
        assert error != null;

        displayError(error);

        return Console.Result.CONTINUE;
    }

    private void displayError(final Throwable error) {
        assert error != null;

        // Decode any error notifications
        Throwable cause = error;
        if (error instanceof ErrorNotification) {
            cause = error.getCause();
        }

        if (io.isDebug()) {
            // If we have debug enabled then skip the fancy bits below, and log the full error, don't decode shit
            log.debug(error.toString(), error);
        }

        Variables vars = ShellHolder.get().getVariables();

        // Determine if the stack trace flag is set
        boolean showTrace = false;
        if (vars.contains(SHELL_ERRORS)) {
            String tmp = vars.get(SHELL_ERRORS, String.class);
            showTrace = Boolean.parseBoolean(tmp.trim());
        }

        // Spit out the terse reason why we've failed
        io.err.println(messages.format(ERROR_EXCEPTION_NAME,  cause.getClass().getName(), cause.getMessage()));

        if (showTrace || io.isVerbose()) {
            while (cause != null) {
                for (StackTraceElement e : cause.getStackTrace()) {
                    io.err.print("    ");
                    io.err.println(messages.format(ERROR_EXCEPTION_AT, e.getClassName(), e.getMethodName(), getLocation(e)));
                }

                cause = cause.getCause();
                if (cause != null) {
                    io.err.println(messages.format(ERROR_EXCEPTION_CAUSEBY, cause.getClass().getName(), cause.getMessage()));
                }
            }
        }

        io.err.flush();
    }

    private String getLocation(final StackTraceElement e) {
        assert e != null;

        if (e.isNativeMethod()) {
            return messages.format(ERROR_LOCATION_NATIVE);
        }
        else if (e.getFileName() == null) {
            return messages.format(ERROR_LOCATION_UNKNOWN);
        }
        else if (e.getLineNumber() >= 0) {
            return String.format("%s:%s", e.getFileName(), e.getLineNumber());
        }
        else {
            return e.getFileName();
        }
    }
}