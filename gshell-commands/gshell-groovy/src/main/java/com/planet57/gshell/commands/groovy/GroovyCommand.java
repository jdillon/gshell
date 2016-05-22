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
package com.planet57.gshell.commands.groovy;

import java.io.File;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import com.planet57.gshell.branding.Branding;
import com.planet57.gshell.command.Command;
import com.planet57.gshell.command.CommandContext;
import com.planet57.gshell.command.support.CommandActionSupport;
import com.planet57.gshell.util.cli2.Argument;
import com.planet57.gshell.util.pref.Preferences;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import jline.console.completer.Completer;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Execute a <a href="http://groovy.codehaus.org">Groovy</a> script.
 *
 * @since 2.6.5
 */
@Command(name = "groovy")
@Preferences(path = "commands/groovy")
public class GroovyCommand
    extends CommandActionSupport
{
    @Argument(required=true)
    private File file;

    @Argument(index=1)
    private List<String> args;

    @Inject
    public void installCompleters(final @Named("file-name") Completer c1) {
        checkNotNull(c1);
        setCompleters(c1, null);
    }

    public Object execute(final CommandContext context) throws Exception {
        checkNotNull(context);

        Branding branding = context.getShell().getBranding();
        Binding binding = new Binding();
        binding.setVariable("context", context);
        binding.setVariable("variables", context.getVariables());
        binding.setVariable("io", context.getIo());
        binding.setVariable("branding", branding);
        binding.setVariable("args", args);

        CompilerConfiguration cc = new CompilerConfiguration();
        cc.setOutput(context.getIo().out);
        cc.setTargetDirectory(new File(branding.getUserContextDir(), "var/classes"));

        ClassLoader cl = getClass().getClassLoader();

        GroovyShell shell = new GroovyShell(cl, binding, cc);

        log.info("Parsing: {}", file);
        Script script = shell.parse(file);
        binding.setVariable("log", LoggerFactory.getLogger(script.getClass()));
        script.setBinding(binding);

        log.debug("Running");
        Object result = script.run();
        log.info("Result: {}", result);

        return result;
    }
}