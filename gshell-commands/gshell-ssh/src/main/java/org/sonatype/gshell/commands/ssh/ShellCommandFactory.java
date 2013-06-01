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
package org.sonatype.gshell.commands.ssh;

import org.apache.sshd.server.Command;
import org.apache.sshd.server.CommandFactory;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;
import org.sonatype.gshell.util.io.Closer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * ???
 *
 * @since 2.3
 */
public class ShellCommandFactory
    implements CommandFactory
{
    private CommandProcessor commandProcessor;

    public void setCommandProcessor(CommandProcessor commandProcessor) {
        this.commandProcessor = commandProcessor;
    }

    public Command createCommand(String command) {
        return new ShellCommand(command);
    }

    public class ShellCommand
        implements Command
    {
        private String command;
        private InputStream in;
        private OutputStream out;
        private OutputStream err;
        private ExitCallback callback;

        public ShellCommand(String command) {
            this.command = command;
        }

        public void setInputStream(InputStream in) {
            this.in = in;
        }

        public void setOutputStream(OutputStream out) {
            this.out = out;
        }

        public void setErrorStream(OutputStream err) {
            this.err = err;
        }

        public void setExitCallback(ExitCallback callback) {
            this.callback = callback;
        }

        public void start(final Environment env) throws IOException {
            try {
                CommandSession session = commandProcessor.createSession(in, new PrintStream(out), new PrintStream(err));
                session.execute(command);
            }
            catch (Exception e) {
                throw (IOException) new IOException("Unable to start shell").initCause(e);
            }
            finally {
                Closer.close(in, out, err);
                callback.onExit(0);
            }
        }

        public void destroy() {
		}
    }
}
