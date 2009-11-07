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

import java.io.File;

/**
 * Helper to validate files.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class FileValidator
{
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
                throw new InvalidFileException("File does not exist", getFile()); // TODO: i18n
            }
            else {
                throw new InvalidFileException("File exists", getFile()); // TODO: i18n
            }
        }
        return this;
    }

    public FileValidator isFile(final boolean flag) {
        if (getFile().isFile() != flag) {
            if (flag) {
                throw new InvalidFileException("File is not a file", getFile()); // TODO: i18n
            }
            else {
                throw new InvalidFileException("File is a file", getFile()); // TODO: i18n
            }
        }
        return this;
    }

    public FileValidator isDirectory(final boolean flag) {
        if (getFile().isDirectory() != flag) {
            if (flag) {
                throw new InvalidFileException("File is not a directory", getFile()); // TODO: i18n
            }
            else {
                throw new InvalidFileException("File is a directory", getFile()); // TODO: i18n
            }
        }
        return this;
    }

    public FileValidator isReadable(final boolean flag) {
        if (getFile().canRead() != flag) {
            if (flag) {
                throw new InvalidFileException("File is not readable", getFile()); // TODO: i18n
            }
            else {
                throw new InvalidFileException("File is readable", getFile()); // TODO: i18n
            }
        }
        return this;
    }

    public FileValidator isWritable(final boolean flag) {
        if (getFile().canWrite() != flag) {
            if (flag) {
                throw new InvalidFileException("File is not writable", getFile()); // TODO: i18n
            }
            else {
                throw new InvalidFileException("File is a writable", getFile()); // TODO: i18n
            }
        }
        return this;
    }

    public FileValidator isHidden(final boolean flag) {
        if (getFile().isHidden() != flag) {
            if (flag) {
                throw new InvalidFileException("File is not hidden", getFile()); // TODO: i18n
            }
            else {
                throw new InvalidFileException("File is a hidden", getFile()); // TODO: i18n
            }
        }
        return this;
    }

    public FileValidator isExecutable(final boolean flag) {
        if (getFile().canExecute() != flag) {
            if (flag) {
                throw new InvalidFileException("File is not executable", getFile()); // TODO: i18n
            }
            else {
                throw new InvalidFileException("File is executable", getFile()); // TODO: i18n
            }
        }
        return this;
    }

    public FileValidator isAbsolute(final boolean flag) {
        if (getFile().isAbsolute() != flag) {
            if (flag) {
                throw new InvalidFileException("File is not absolute", getFile()); // TODO: i18n
            }
            else {
                throw new InvalidFileException("File is absolute", getFile()); // TODO: i18n
            }
        }
        return this;
    }

    public static class InvalidFileException
        extends RuntimeException
    {
        private final File file;

        public InvalidFileException(final String message, final File file) {
            super(message + ": " + file);
            this.file = file;
        }

        public File getFile() {
            return file;
        }
    }
}