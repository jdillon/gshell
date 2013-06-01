/*
 * Copyright (c) 2009-2013 the original author or authors.
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

import jline.console.completer.Completer;
import org.apache.sshd.common.Factory;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;
import org.sonatype.gshell.util.io.Closer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * SSHD {@link org.apache.sshd.server.Command} factory which provides access to Shell.
 *
 * @since 2.3
 */
public class ShellFactoryImpl
    implements Factory<Command>
{
    private CommandProcessor commandProcessor;
    private List<Completer> completers;

    public void setCommandProcessor(CommandProcessor commandProcessor) {
        this.commandProcessor = commandProcessor;
    }

    public void setCompleters(List<Completer> completers) {
        this.completers = completers;
    }

    public Command create() {
        return new ShellImpl();
    }

    public class ShellImpl
        implements Command
    {
        private InputStream in;

        private OutputStream out;

        private OutputStream err;

        private ExitCallback callback;

        private boolean closed;

        public void setInputStream(final InputStream in) {
            this.in = in;
        }

        public void setOutputStream(final OutputStream out) {
            this.out = out;
        }

        public void setErrorStream(final OutputStream err) {
            this.err = err;
        }

        public void setExitCallback(ExitCallback callback) {
            this.callback = callback;
        }

        public void start(final Environment env) throws IOException {
            try {
                final Callable<Boolean> printStackTraces = new Callable<Boolean>() {
                    public Boolean call() {
                        return Boolean.valueOf(System.getProperty(Console.PRINT_STACK_TRACES));
                    }
                };

                Console console = new Console(commandProcessor,
                                              in,
                                              new PrintStream(out, true),
                                              new PrintStream(err, true),
                                              new SshTerminal(env),
                                              new AggregateCompleter(completers),
                                              new Runnable() {
                                                  public void run() {
                                                      destroy();
                                                  }
                                              },
                                              printStackTraces);

                CommandSession session = console.getSession();
                session.put("APPLICATION", System.getProperty("karaf.name", "root"));
                for (Map.Entry<String,String> e : env.getEnv().entrySet()) {
                    session.put(e.getKey(), e.getValue());
                }

                new Thread(console).start();
            }
            catch (Exception e) {
                throw (IOException) new IOException("Unable to start shell").initCause(e);
            }
        }

        public void destroy() {
            if (!closed) {
                closed = true;
                Closer.close(in, out, err);
                callback.onExit(0);
            }
        }
    }
}
