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
package org.sonatype.gshell.commands.vfs;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSelectInfo;
import org.apache.commons.vfs.FileSelector;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileType;
import org.apache.oro.text.GlobCompiler;
import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternCompiler;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.Perl5Matcher;
import org.sonatype.gshell.command.Command;
import org.sonatype.gshell.command.CommandContext;
import org.sonatype.gshell.command.IO;
import org.sonatype.gshell.util.cli2.Argument;
import org.sonatype.gshell.util.cli2.Option;
import org.sonatype.gshell.vfs.FileObjectAssert;
import org.sonatype.gshell.vfs.FileObjects;
import org.sonatype.gshell.vfs.selector.AggregateFileSelector;

import java.net.URI;

/**
 * Find files in a hierarchy.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
@Command(name="vfs/find")
public class FindCommand
    extends VfsCommandSupport
{
    private final AggregateFileSelector selector = new AggregateFileSelector();

    @Option(name="name")
    private void addNameFilter(final String name) throws MalformedPatternException {
        log.debug("Adding -name selector for: {}", name);
        selector.getSelectors().add(new NameSelector(name));
    }

    @Option(name="iname")
    private void addiNameFilter(final String name) throws MalformedPatternException {
        log.debug("Adding -iname selector for: {}", name);
        selector.getSelectors().add(new NameSelector(name, true));
    }

    @Option(name="type")
    private void addTypeFilter(final Type type) {
        log.debug("Adding -type selector for: {}", type);
        selector.getSelectors().add(new TypeSelector(type));
    }

    @Argument(required=true)
    private String path;

    public Object execute(final CommandContext context) throws Exception {
        assert context != null;
        IO io = context.getIo();

        FileObject root = resolveFile(context, path);

        new FileObjectAssert(root).exists();

        find(context, root, selector);

        FileObjects.close(root);

        return Result.SUCCESS;
    }

    private void find(final CommandContext context, final FileObject file, final FileSelector selector) throws FileSystemException {
        assert context != null;
        assert file != null;
        assert selector != null;

        FileObject[] files = file.findFiles(selector);

        if (files != null && files.length != 0) {
            for (FileObject child : files) {
                display(context, child, file);
            }
        }
    }

    private void display(final CommandContext context, final FileObject file, final FileObject root) throws FileSystemException {
        assert context != null;
        assert file != null;

        String path;
        try {
            path = new URI(this.path).resolve(root.getURL().toURI().relativize(file.getURL().toURI())).toString();
        } catch (Exception e) {
            path = file.getName().getPath();
        }
        IO io = context.getIo();
        io.println(path);
    }

    //
    // Type & TypeSelector
    //

    private enum Type
    {
        F, // normal file
        D, // directory
    }

    private class TypeSelector
        implements FileSelector
    {
        private final Type type;

        public TypeSelector(final Type type) {
            assert type != null;

            this.type = type;

            log.trace("Type: {}", type);
        }

        public boolean includeFile(final FileSelectInfo selection) throws Exception {
            assert selection != null;

            FileType ftype = selection.getFile().getType();

            switch (type) {
                case D:
                    return ftype == FileType.FOLDER;

                case F:
                    return ftype == FileType.FILE;

                // TODO: Handle FileType.FILE_OR_FOLDER

                default:
                    return false;
            }
        }

        public boolean traverseDescendents(final FileSelectInfo selection) throws Exception {
            return true;
        }
    }

    //
    // NameSelector
    //

    private class NameSelector
        implements FileSelector
    {
        private final Pattern pattern;

        private final PatternMatcher matcher;

        public NameSelector(final String name, final boolean ignoreCase) throws MalformedPatternException {
            assert name != null;

            PatternCompiler compiler = new GlobCompiler();
            int options;
            if (ignoreCase) {
                options = GlobCompiler.CASE_INSENSITIVE_MASK;
            }
            else {
                options = GlobCompiler.DEFAULT_MASK;
            }
            this.pattern = compiler.compile(name, options);
            this.matcher = new Perl5Matcher();

            log.trace("Pattern: {}", pattern.getPattern());
        }

        public NameSelector(final String name) throws MalformedPatternException {
            this(name, false);
        }

        public boolean includeFile(final FileSelectInfo selection) throws Exception {
            assert selection != null;
            return matcher.matches(selection.getFile().getName().getBaseName(), pattern);
        }

        public boolean traverseDescendents(final FileSelectInfo selection) throws Exception {
            return true;
        }
    }
}