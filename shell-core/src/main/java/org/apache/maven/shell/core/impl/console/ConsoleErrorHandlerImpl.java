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

package org.apache.maven.shell.core.impl.console;

import org.apache.maven.shell.ShellHolder;
import org.apache.maven.shell.VariableNames;
import org.apache.maven.shell.Variables;
import org.apache.maven.shell.i18n.MessageSource;
import org.apache.maven.shell.i18n.ResourceBundleMessageSource;
import org.apache.maven.shell.console.Console;
import org.apache.maven.shell.io.IO;
import org.apache.maven.shell.notification.ErrorNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link Console.ErrorHandler} component.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class ConsoleErrorHandlerImpl
    implements Console.ErrorHandler, VariableNames
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final IO io;

    private final MessageSource messages = new ResourceBundleMessageSource(getClass());
    
    public ConsoleErrorHandlerImpl(final IO io) {
        assert io != null;
        this.io = io;
    }

    public Result handleError(final Throwable error) {
        assert error != null;

        displayError(error);

        return Result.CONTINUE;
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

        // TODO: i18n

        // Spit out the terse reason why we've failed
        io.err.format("@|bold,red ERROR| %s: @|bold,red %s|", cause.getClass().getSimpleName(), cause.getMessage()).println();

        if (showTrace || io.isVerbose()) {
            while (cause != null) {
                for (StackTraceElement e : cause.getStackTrace()) {
                    io.err.format("        @|bold at| %s.%s (@|bold %s|)", e.getClassName(), e.getMethodName(), getLocation(e)).println();
                }

                cause = cause.getCause();
                if (cause != null) {
                    io.err.format("    @|bold Caused by| %s: @|bold,red %s|", cause.getClass().getSimpleName(), cause.getMessage()).println();    
                }
            }
        }

        io.err.flush();
    }

    private String getLocation(final StackTraceElement e) {
        assert e != null;

        // TODO: i18n
        
        if (e.isNativeMethod()) {
            return "Native Method";
        }
        else if (e.getFileName() == null) {
            return "Unknown Source";
        }
        else if (e.getLineNumber() >= 0) {
            return String.format("%s:%s", e.getFileName(), e.getLineNumber());
        }
        else {
            return e.getFileName();
        }
    }
}