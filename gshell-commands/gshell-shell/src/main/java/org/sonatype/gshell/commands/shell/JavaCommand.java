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
package org.sonatype.gshell.commands.shell;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.gshell.command.Command;
import org.sonatype.gshell.command.support.CommandActionSupport;
import org.sonatype.gshell.command.CommandContext;
import org.sonatype.gshell.util.cli2.Argument;
import org.sonatype.gshell.util.cli2.Option;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Execute a Java standard application.
 *
 * <p>By default looks for static main(String[]) to execute, but
 * you can specify a different static method that takes a String[]
 * to execute instead.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
@Command(name="java")
public class JavaCommand
    extends CommandActionSupport
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Option(name="m", longName="method")
    private String methodName = "main";

    @Argument(index=0, required=true)
    private String className;

    @Argument(index=1)
    private List<String> args;

    public Object execute(final CommandContext context) throws Exception {
        assert context != null;

        log.debug("Loading class: {}", className);
        Class type = Thread.currentThread().getContextClassLoader().loadClass(className);
        log.info("Using type: {}", type);

        log.debug("Locating method: {}", methodName);
        Method method = type.getMethod(methodName, String[].class);
        log.info("Using method: {}", method);

        log.info("Invoking w/arguments: {}", args);
        Object result = method.invoke(null, args);
        log.info("Result: {}", result);

        return result;
    }
}