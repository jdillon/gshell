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

package org.sonatype.gshell.util;

import org.sonatype.gshell.util.i18n.MessageSource;
import org.sonatype.gshell.util.i18n.ResourceBundleMessageSource;

import java.io.File;

/**
 * Helper to validate files.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class FileValidator
{
    private static enum Messages
    {
        ///CLOVER:OFF

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
        IS_HIDDEN,
        NOT_EXECUTABLE,
        IS_EXECUTABLE,
        NOT_ABSOLUTE,
        IS_ABSOLUTE;

        private static final MessageSource MESSAGES = new ResourceBundleMessageSource(FileValidator.class);

        String format(final Object... args) {
            return MESSAGES.format(name(), args);
        }
    }

    private final File file;

    public FileValidator(final File file) {
        assert file != null;
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public FileValidator exists(final boolean flag) {
        if (getFile().exists() != flag) {
            if (flag) {
                throw new InvalidFileException(Messages.NOT_FOUND.format(getFile()));
            }
            else {
                throw new InvalidFileException(Messages.EXISTS.format(getFile()));
            }
        }
        return this;
    }

    public FileValidator isFile(final boolean flag) {
        if (getFile().isFile() != flag) {
            if (flag) {
                throw new InvalidFileException(Messages.NOT_FILE.format(getFile()));
            }
            else {
                throw new InvalidFileException(Messages.IS_FILE.format(getFile()));
            }
        }
        return this;
    }

    public FileValidator isDirectory(final boolean flag) {
        if (getFile().isDirectory() != flag) {
            if (flag) {
                throw new InvalidFileException(Messages.NOT_DIRECTORY.format(getFile()));
            }
            else {
                throw new InvalidFileException(Messages.IS_DIRECTORY.format(getFile()));
            }
        }
        return this;
    }

    public FileValidator isReadable(final boolean flag) {
        if (getFile().canRead() != flag) {
            if (flag) {
                throw new InvalidFileException(Messages.NOT_READABLE.format(getFile()));
            }
            else {
                throw new InvalidFileException(Messages.IS_READABLE.format(getFile()));
            }
        }
        return this;
    }

    public FileValidator isWritable(final boolean flag) {
        if (getFile().canWrite() != flag) {
            if (flag) {
                throw new InvalidFileException(Messages.NOT_WRITABLE.format(getFile()));
            }
            else {
                throw new InvalidFileException(Messages.IS_WRITABLE.format(getFile()));
            }
        }
        return this;
    }

    public FileValidator isHidden(final boolean flag) {
        if (getFile().isHidden() != flag) {
            if (flag) {
                throw new InvalidFileException(Messages.NOT_HIDDEN.format(getFile()));
            }
            else {
                throw new InvalidFileException(Messages.IS_HIDDEN.format(getFile()));
            }
        }
        return this;
    }

    public FileValidator isExecutable(final boolean flag) {
        if (getFile().canExecute() != flag) {
            if (flag) {
                throw new InvalidFileException(Messages.NOT_EXECUTABLE.format(getFile()));
            }
            else {
                throw new InvalidFileException(Messages.IS_EXECUTABLE.format(getFile()));
            }
        }
        return this;
    }

    public FileValidator isAbsolute(final boolean flag) {
        if (getFile().isAbsolute() != flag) {
            if (flag) {
                throw new InvalidFileException(Messages.NOT_ABSOLUTE.format(getFile()));
            }
            else {
                throw new InvalidFileException(Messages.IS_ABSOLUTE.format(getFile()));
            }
        }
        return this;
    }

    public class InvalidFileException
        extends RuntimeException
    {
        public InvalidFileException(final String message) {
            super(message);
        }

        public File getFile() {
            return FileValidator.this.getFile();
        }
    }
}