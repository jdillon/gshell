/*
 * Copyright (C) 2009 the original author(s).
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

package org.sonatype.gshell.core.commands;

import jline.console.completers.EnumCompleter;
import org.sonatype.gshell.ansi.Ansi;
import org.sonatype.gshell.cli.Argument;
import org.sonatype.gshell.cli.CommandLineProcessor;
import org.sonatype.gshell.cli.CommandLineProcessorAware;
import org.sonatype.gshell.cli.Option;
import org.sonatype.gshell.command.Command;
import org.sonatype.gshell.command.CommandContext;
import org.sonatype.gshell.command.IO;
import org.sonatype.gshell.core.command.CommandActionSupport;
import org.sonatype.gshell.io.Closer;
import org.sonatype.gshell.io.Flusher;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.prefs.Preferences;

/**
 * Manage preferences.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
@Command
public class PreferenceCommand
    extends CommandActionSupport
    implements CommandLineProcessorAware
{
    private static enum Mode
    {
        LIST,
        REMOVE,
        SET,
        GET,
        UNSET,
        CLEAR,
        EXPORT,
        IMPORT,

        // TODO: Once we can effectively take objects, add listener support
    }

    @Option(name = "-s", aliases = {"--system"})
    private boolean system;

    @Argument(index = 0, required = true)
    private Mode mode;

    @Argument(index = 1, multiValued = true)
    private List<String> args;

    public PreferenceCommand() {
        this.setCompleters(new EnumCompleter(Mode.class), null);
    }

    public void setProcessor(final CommandLineProcessor processor) {
        processor.setStopAtNonOption(true);
    }

    public Object execute(final CommandContext context) throws Exception {
        assert context != null;

        Operation op = createOperation(context);

        CommandLineProcessor cli = new CommandLineProcessor(op);
        cli.process(args);

        return op.execute();
    }

    private Operation createOperation(CommandContext context) {
        log.debug("Mode: {}", mode);

        switch (mode) {
            case LIST:
                return new ListOperation(context);

            case REMOVE:
                return new RemoveOperation(context);

            case SET:
                return new SetOperation(context);

            case GET:
                return new GetOperation(context);

            case UNSET:
                return new UnsetOperation(context);

            case CLEAR:
                return new ClearOperation(context);

            case EXPORT:
                return new ExportOperation(context);

            case IMPORT:
                return new ImportOperation(context);

            default:
                // Should never happen
                throw new InternalError();
        }
    }

    private interface Operation
    {
        Object execute() throws Exception;
    }

    private abstract class OperationSupport
        implements Operation
    {
        protected final CommandContext context;

        protected final IO io;

        protected OperationSupport(final CommandContext context) {
            assert context != null;
            this.context = context;
            this.io = context.getIo();
        }

        protected Preferences root() {
            Preferences root;
            if (system) {
                root = Preferences.systemRoot();
            }
            else {
                root = Preferences.userRoot();
            }
            return root;
        }
    }

    private abstract class NodeOperationSupport
        extends OperationSupport
    {
        @Argument(index = 0, required = true)
        private String path;

        protected NodeOperationSupport(final CommandContext context) {
            super(context);
        }

        public abstract Object execute(Preferences prefs) throws Exception;

        public Object execute() throws Exception {
            Preferences root = root();
            log.debug("Root: {}", root);

            Preferences node = root.node(path);
            log.debug("Node: {}", node);

            return execute(node);
        }
    }

    private class ListOperation
        extends NodeOperationSupport
    {
        @Option(name = "-r", aliases = {"--recursive"})
        private boolean recursive;

        private ListOperation(final CommandContext context) {
            super(context);
        }

        public Object execute(final Preferences prefs) throws Exception {
            list(prefs);
            return Result.SUCCESS;
        }

        private void list(final Preferences node) throws Exception {
            io.info("{}", Ansi.ansi().fg(Ansi.Color.GREEN).a(node.absolutePath()).reset());

            for (String key : node.keys()) {
                io.info("  {}={}", Ansi.ansi().a(Ansi.Attribute.INTENSITY_BOLD).a(key).reset(), node.get(key, null));
            }
            if (recursive) {
                for (String name : node.childrenNames()) {
                    list(node.node(name));
                }
            }
        }
    }

    private class RemoveOperation
        extends NodeOperationSupport
    {
        private RemoveOperation(final CommandContext context) {
            super(context);
        }

        public Object execute(final Preferences prefs) throws Exception {
            prefs.removeNode();
            return Result.SUCCESS;
        }
    }

    private class SetOperation
        extends NodeOperationSupport
    {
        @Argument(index = 1, required = true)
        private String key;

        @Argument(index = 2, required = true)
        private String value;

        private SetOperation(final CommandContext context) {
            super(context);
        }

        public Object execute(final Preferences prefs) throws Exception {
            prefs.put(key, value);
            return Result.SUCCESS;
        }
    }

    private class GetOperation
        extends NodeOperationSupport
    {
        @Argument(index = 1, required = true)
        private String key;

        private GetOperation(final CommandContext context) {
            super(context);
        }

        public Object execute(final Preferences prefs) throws Exception {
            Object result = prefs.get(key, null);
            io.info("{}", result);
            return result;
        }
    }

    private class UnsetOperation
        extends NodeOperationSupport
    {
        @Argument(index = 1, required = true)
        private String key;

        private UnsetOperation(final CommandContext context) {
            super(context);
        }

        public Object execute(final Preferences prefs) throws Exception {
            prefs.remove(key);
            return Result.SUCCESS;
        }
    }

    private class ClearOperation
        extends NodeOperationSupport
    {
        private ClearOperation(final CommandContext context) {
            super(context);
        }

        public Object execute(final Preferences prefs) throws Exception {
            prefs.clear();
            return Result.SUCCESS;
        }
    }

    private class ExportOperation
        extends NodeOperationSupport
    {
        @Option(name = "-t", aliases = {"--subtree"})
        private boolean subTree;

        @Argument(index = 1)
        private File file;

        private ExportOperation(final CommandContext context) {
            super(context);
        }

        public Object execute(final Preferences prefs) throws Exception {
            OutputStream out;
            if (file == null) {
                out = io.streams.out;
            }
            else {
                io.info("Exporting to: {}", file); // TODO: i18n
                out = new BufferedOutputStream(new FileOutputStream(file));
            }

            try {
                if (subTree) {
                    prefs.exportSubtree(out);
                }
                else {
                    prefs.exportNode(out);
                }

                Flusher.flush(out);
            }
            finally {
                if (file != null) {
                    Closer.close(out);
                }
            }

            return Result.SUCCESS;
        }
    }

    private class ImportOperation
        extends OperationSupport
    {
        @Argument(index = 0, required = true)
        private File source;

        private ImportOperation(final CommandContext context) {
            super(context);
        }

        public Object execute() throws Exception {
            io.info("Importing preferences from: {}", source); // TODO: i18n

            InputStream in = new BufferedInputStream(new FileInputStream(source));

            try {
                Preferences.importPreferences(in);
            }
            finally {
                Closer.close(in);
            }

            return Result.SUCCESS;
        }
    }
}