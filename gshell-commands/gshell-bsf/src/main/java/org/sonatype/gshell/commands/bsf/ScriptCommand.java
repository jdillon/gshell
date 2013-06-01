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
package org.sonatype.gshell.commands.bsf;

import javax.inject.Inject;
import org.apache.bsf.BSFEngine;
import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileUtil;
import org.fusesource.jansi.Ansi;
import org.sonatype.gshell.command.Command;
import org.sonatype.gshell.command.support.CommandActionSupport;
import org.sonatype.gshell.command.CommandContext;
import org.sonatype.gshell.command.IO;
import org.sonatype.gshell.console.Console;
import org.sonatype.gshell.console.ConsoleErrorHandler;
import org.sonatype.gshell.console.ConsolePrompt;
import org.sonatype.gshell.console.ConsoleTask;
import org.sonatype.gshell.util.cli2.Argument;
import org.sonatype.gshell.util.cli2.Option;
import org.sonatype.gshell.vfs.FileSystemAccess;

import java.util.concurrent.Callable;

/**
 * Provides generic scripting language integration via <a href="http://http://jakarta.apache.org/bsf">BSF</a>.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
@Command(name="script")
public class ScriptCommand
    extends CommandActionSupport
{
    private final BSFManager manager;

    private final FileSystemAccess fileSystemAccess;

    private String language;

    @Option(name="l", longName="language")
    private void setLanguage(final String language) {
        assert language != null;

        if (!BSFManager.isLanguageRegistered(language)) {
            throw new RuntimeException("Language is not registered: " + language); // TODO: i18n
        }

        this.language = language;
    }

    @Option(name="e", longName="expression")
    private String expression;

    @Argument
    private String path;

    @Inject
    public ScriptCommand(final BSFManager manager, final FileSystemAccess fileSystemAccess) {
        assert manager != null;
        this.manager = manager;
        assert fileSystemAccess != null;
        this.fileSystemAccess = fileSystemAccess;
    }

    public Object execute(final CommandContext context) throws Exception {
        assert context != null;
        IO io = context.getIo();

        if (expression != null && path != null) {
            io.error("Can only specify an expression or a script file"); // TODO: i18n
            return Result.FAILURE;
        }
        else if (expression != null) {
            return eval(context);
        }
        else if (path != null){
        	return exec(context);
        }

        return console(context);
    }

    private String detectLanguage(final FileObject file) throws Exception {
        assert file != null;

        return BSFManager.getLangFromFilename(file.getName().getBaseName());
    }

    private BSFEngine createEngine(final CommandContext context) throws BSFException {
        assert context != null;

        // Bind some stuff into the scripting engine's namespace
        manager.declareBean("context", context, CommandContext.class);

        BSFEngine engine = manager.loadScriptingEngine(language);

        log.debug("Created engine: {}", engine);

        return engine;
    }

    private Object eval(final CommandContext context) throws Exception {
        assert context != null;
        IO io = context.getIo();

        if (language == null) {
            io.error("The scripting language must be configured via --language to evaluate an expression"); // TODO: i18n
            return Result.FAILURE;
        }

        log.debug("Evaluating script ({}): {}", language, expression);

        BSFEngine engine = createEngine(context);

        try {
            return engine.eval("<script.expression>", 1, 1, expression);
        }
        finally {
            engine.terminate();
        }
    }

    private Object exec(final CommandContext context) throws Exception {
        assert context != null;
        IO io = context.getIo();

        FileObject cwd = fileSystemAccess.getCurrentDirectory(context.getVariables());
        FileObject file = fileSystemAccess.resolveFile(cwd, path);

        if (!file.exists()) {
            io.error("File not found: {}", file.getName()); // TODO: i18n
            return Result.FAILURE;
        }
        else if (!file.getType().hasContent()) {
            io.error("File has not content: {}", file.getName()); // TODO: i18n
            return Result.FAILURE;
        }
        else if (!file.isReadable()) {
            io.error("File is not readable: {}", file.getName()); // TODO: i18n
            return Result.FAILURE;
        }

        if (language == null) {
            language = detectLanguage(file);
        }

        BSFEngine engine = createEngine(context);

        byte[] bytes = FileUtil.getContent(file);
        String script = new String(bytes);

        log.info("Evaluating file ({}): {}", language, path); // TODO: i18n

        try {
            return engine.eval(file.getName().getBaseName(), 1, 1, script);
        }
        finally {
            engine.terminate();
            file.close();
        }
    }

    private Object console(final CommandContext context) throws Exception {
        assert context != null;
        IO io = context.getIo();

        if (language == null) {
            io.error("The scripting language must be configured via --language to run an interactive console"); // TODO: i18n
            return Result.FAILURE;
        }

        log.debug("Starting console ({})...", language);

        final BSFEngine engine = createEngine(context);
        final ResultHolder holder = new ResultHolder();

        Callable<ConsoleTask> taskFactory = new Callable<ConsoleTask>() {
            public ConsoleTask call() throws Exception {
                return new ConsoleTask() {
                    @Override
                    public boolean doExecute(String input) throws Exception {
                        //
                        // TODO: Update the allow the console to handle CTRL-D and have that cause the loop to exit
                        //

                        if (input == null || input.trim().equals("exit") || input.trim().equals("quit")) {
                            return false;
                        }
                        else if (!input.trim().equals("")) {
                            holder.result = engine.eval("<script.console>", 1, 1, input);
                        }

                        return true;
                    }
                };
            }
        };

        Console console = new Console(context.getIo(), taskFactory, null, null);

        console.setErrorHandler(new ConsoleErrorHandler() {
            public boolean handleError(final Throwable error) {
                log.error("Script evaluation failed: " + error, error); // TODO: i18n
                return true;
            }
        });

        console.setPrompt(new ConsolePrompt() {
            public String prompt() {
                return Ansi.ansi().a(Ansi.Attribute.INTENSITY_BOLD).a(language).reset().a("> ").toString();
            }
        });

        console.run();

        engine.terminate();

        return holder.result;
    }

    private class ResultHolder
    {
        public Object result;
    }
}