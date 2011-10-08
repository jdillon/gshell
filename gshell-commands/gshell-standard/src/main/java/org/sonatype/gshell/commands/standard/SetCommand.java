/**
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
package org.sonatype.gshell.commands.standard;

import javax.inject.Inject;
import com.google.inject.name.Named;
import jline.console.completer.Completer;
import org.sonatype.gshell.command.Command;
import org.sonatype.gshell.command.support.CommandActionSupport;
import org.sonatype.gshell.command.CommandContext;
import org.sonatype.gshell.command.IO;
import org.sonatype.gshell.util.Strings;
import org.sonatype.gshell.util.cli2.Argument;
import org.sonatype.gshell.util.cli2.Option;
import org.sonatype.gshell.util.i18n.MessageSource;
import org.sonatype.gshell.variables.Variables;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * Set a variable or property.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
@Command(name="set")
public class SetCommand
    extends CommandActionSupport
{
    enum Mode
    {
        VARIABLE,
        PROPERTY
    }

    @Option(name = "m", longName = "mode")
    private Mode mode = Mode.VARIABLE;

    @Option(name = "v", longName = "verbose")
    private boolean verbose;

    /**
     * @since 2.5.6
     */
    @Option(name = "e", longName = "evaluate")
    private boolean evaluate;

    @Argument(index = 0)
    private String name;

    @Argument(index = 1)
    private List<String> values;

    @Inject
    public SetCommand installCompleters(@Named("variable-name") final Completer c1) {
        assert c1 != null;
        setCompleters(c1, null);
        return this;
    }

    public Object execute(final CommandContext context) throws Exception {
        assert context != null;
        IO io = context.getIo();
        MessageSource messages = getMessages();

        if (name == null) {
            return displayList(context);
        }
        else if (values == null) {
            io.error(getMessages().format("error.missing-arg", messages.getMessage("command.argument.values.token")));
            return Result.FAILURE;
        }

        String value = Strings.join(values.toArray(), " ");

        if (evaluate) {
            Object result = context.getShell().execute(value);
            if (result == null || result instanceof Result) {
                io.error(messages.format("error.expression-did-not-return-a-value", value));
                return Result.FAILURE;
            }
            value = result.toString();
        }

        switch (mode) {
            case PROPERTY:
                log.info("Setting system property: {}={}", name, value);
                System.setProperty(name, value);
                break;

            case VARIABLE:
                Variables vars = context.getVariables();
                log.info("Setting variable: {}={}", name, value);
                vars.set(name, value);
                break;
        }

        return Result.SUCCESS;
    }

    private Object displayList(final CommandContext context) throws Exception {
        assert context != null;
        IO io = context.getIo();

        // NOTE: Using io.outputStream to display values to avoid any ANSI encoding or other translation.

        switch (mode) {
            case PROPERTY: {
                Properties props = System.getProperties();

                for (Object o : props.keySet()) {
                    String name = (String) o;
                    String value = props.getProperty(name);

                    io.streams.out.print(name);
                    io.streams.out.print("='");
                    io.streams.out.print(value);
                    io.streams.out.println("'");
                }
                break;
            }

            case VARIABLE: {
                Variables variables = context.getVariables();
                Iterator<String> iter = variables.names();

                while (iter.hasNext()) {
                    String name = iter.next();
                    Object value = variables.get(name);

                    io.streams.out.print(name);
                    io.streams.out.print("='");
                    io.streams.out.print(value);
                    io.streams.out.flush();
                    io.streams.out.print("'");

                    // When --verbose include the class details of the values
                    if (verbose && value != null) {
                        io.streams.out.print(" (");
                        io.streams.out.print(value.getClass());
                        io.streams.out.print(")");
                    }

                    io.streams.out.println();
                }
                break;
            }
        }

        // Manually flush the stream, normally framework only flushes io.out
        io.streams.out.flush();

        return Result.SUCCESS;
    }
}
