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

package org.apache.maven.shell.terminal;

import jline.ConsoleReader;
import jline.Terminal;
import org.codehaus.plexus.util.Os;

import java.io.IOException;
import java.io.InputStream;

/**
 * Auto-detected terminal.
 *
 * @version $Rev$ $Date$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class AutoDetectedTerminal
    extends jline.Terminal
    implements Constants
{
    private final Terminal delegate;

    public AutoDetectedTerminal() {
        if (Os.isFamily(Os.FAMILY_WINDOWS)) {
            delegate = new WindowsTerminal();
        }
        else {
            delegate = new UnixTerminal();
        }
    }

    public Terminal getDelegate() {
        return delegate;
    }

    public boolean isANSISupported() {
        return delegate.isANSISupported();
    }

    public int readCharacter(final InputStream in) throws IOException {
        return delegate.readCharacter(in);
    }

    public int readVirtualKey(final InputStream in) throws IOException {
        return delegate.readVirtualKey(in);
    }

    public void initializeTerminal() throws Exception {
        delegate.initializeTerminal();
    }

    public int getTerminalWidth() {
        return delegate.getTerminalWidth();
    }

    public int getTerminalHeight() {
        return delegate.getTerminalHeight();
    }

    public boolean isSupported() {
        return delegate.isSupported();
    }

    public boolean getEcho() {
        return delegate.getEcho();
    }

    public void beforeReadLine(final ConsoleReader reader, final String prompt, final Character mask) {
        delegate.beforeReadLine(reader, prompt, mask);
    }

    public void afterReadLine(final ConsoleReader reader, final String prompt, final Character mask) {
        delegate.afterReadLine(reader, prompt, mask);
    }

    public boolean isEchoEnabled() {
        return delegate.isEchoEnabled();
    }

    public void enableEcho() {
        delegate.enableEcho();
    }

    public void disableEcho() {
        delegate.disableEcho();
    }

    public InputStream getDefaultBindings() {
        return delegate.getDefaultBindings();
    }

    public static String configure(String type) {
        if (type == null) {
            type = AUTO;
        }

        type = type.toLowerCase();

        if (AUTO.equals(type)) {
            type = AutoDetectedTerminal.class.getName();
        }
        else if (UNIX.equals(type)) {
            type = UnixTerminal.class.getName();
        }
        else if (WIN.equals(type) || WINDOWS.equals(type)) {
            type = WindowsTerminal.class.getName();
        }
        else if (FALSE.equals(type) || OFF.equals(type) || NONE.equals(type)) {
            type = UnsupportedTerminal.class.getName();
        }

        System.setProperty(JLINE_TERMINAL, type);

        return type;
    }
}