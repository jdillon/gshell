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
package org.sonatype.gshell.vfs;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.sonatype.gshell.util.i18n.MessageSource;
import org.sonatype.gshell.util.i18n.ResourceBundleMessageSource;

/**
 * Helper to assert various file requirements.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class FileObjectAssert
{
    private static enum Messages
    {
        NOT_FOUND,
        EXISTS,
        NOT_FILE,
        IS_FILE,
        NOT_DIRECTORY,
        IS_DIRECTORY,
        NOT_READABLE,
        IS_READABLE,
        NOT_WRITABLE,
        IS_WRITABLE,
        NOT_HIDDEN,
        IS_HIDDEN;

        private static final MessageSource MESSAGES = new ResourceBundleMessageSource(FileObjectAssert.class);

        String format(final Object... args) {
            return MESSAGES.format(name(), args);
        }
    }

    private final FileObject file;

    public FileObjectAssert(final FileObject file) {
        assert file != null;
        this.file = file;
    }

    public FileObject getFile() {
        return file;
    }

    //
    // FIXME: Need to close the file when we toss an exception
    //

    @Override
    protected void finalize() throws Throwable {
        FileObjects.close(getFile());
    }

    public FileObjectAssert exists(final boolean flag) throws FileSystemException {
        if (getFile().exists() != flag) {
            if (flag) {
                throw new AssertionException(Messages.NOT_FOUND.format(getFile()));
            }
            else {
                throw new AssertionException(Messages.EXISTS.format(getFile()));
            }
        }
        return this;
    }

    public FileObjectAssert exists() throws FileSystemException {
        return exists(true);
    }

    public FileObjectAssert isFile(final boolean flag) throws FileSystemException {
        if (getFile().getType().hasChildren() == flag) {
            if (flag) {
                throw new AssertionException(Messages.NOT_FILE.format(getFile()));
            }
            else {
                throw new AssertionException(Messages.IS_FILE.format(getFile()));
            }
        }
        return this;
    }

    public FileObjectAssert isFile() throws FileSystemException {
        return isFile(true);
    }

    public FileObjectAssert isDirectory(final boolean flag) throws FileSystemException {
        if (getFile().getType().hasChildren() != flag) {
            if (flag) {
                throw new AssertionException(Messages.NOT_DIRECTORY.format(getFile()));
            }
            else {
                throw new AssertionException(Messages.IS_DIRECTORY.format(getFile()));
            }
        }
        return this;
    }

    public FileObjectAssert isDirectory() throws FileSystemException {
        return isDirectory(true);
    }

    public FileObjectAssert isReadable(final boolean flag) throws FileSystemException {
        if (getFile().isReadable() != flag) {
            if (flag) {
                throw new AssertionException(Messages.NOT_READABLE.format(getFile()));
            }
            else {
                throw new AssertionException(Messages.IS_READABLE.format(getFile()));
            }
        }
        return this;
    }

    public FileObjectAssert isReadable() throws FileSystemException {
        return isReadable(true);
    }

    public FileObjectAssert isWritable(final boolean flag) throws FileSystemException {
        if (getFile().isWriteable() != flag) {
            if (flag) {
                throw new AssertionException(Messages.NOT_WRITABLE.format(getFile()));
            }
            else {
                throw new AssertionException(Messages.IS_WRITABLE.format(getFile()));
            }
        }
        return this;
    }

    public FileObjectAssert isWritable() throws FileSystemException {
        return isWritable(true);
    }

    public FileObjectAssert isHidden(final boolean flag) throws FileSystemException {
        if (getFile().isHidden() != flag) {
            if (flag) {
                throw new AssertionException(Messages.NOT_HIDDEN.format(getFile()));
            }
            else {
                throw new AssertionException(Messages.IS_HIDDEN.format(getFile()));
            }
        }
        return this;
    }

    public FileObjectAssert isHidden() throws FileSystemException {
        return isHidden(true);
    }

    public class AssertionException
        extends RuntimeException
    {
        private static final long serialVersionUID = 1;

        public AssertionException(final String message) {
            super(message);
        }

        public FileObject getFile() {
            return FileObjectAssert.this.getFile();
        }
    }
}