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
package com.planet57.gshell.util.io;

import java.io.File;

import com.planet57.gshell.util.i18n.MessageSource;
import com.planet57.gshell.util.i18n.ResourceBundleMessageSource;

/**
 * Helper to assert various file requirements.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class FileAssert
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
    IS_HIDDEN,
    NOT_EXECUTABLE,
    IS_EXECUTABLE,
    NOT_ABSOLUTE,
    IS_ABSOLUTE;

    private static final MessageSource MESSAGES = new ResourceBundleMessageSource(FileAssert.class);

    String format(final Object... args) {
      return MESSAGES.format(name(), args);
    }
  }

  private final File file;

  public FileAssert(final File file) {
    assert file != null;
    this.file = file;
  }

  public File getFile() {
    return file;
  }

  public FileAssert exists(final boolean flag) {
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

  public FileAssert exists() {
    return exists(true);
  }

  public FileAssert isFile(final boolean flag) {
    if (getFile().isFile() != flag) {
      if (flag) {
        throw new AssertionException(Messages.NOT_FILE.format(getFile()));
      }
      else {
        throw new AssertionException(Messages.IS_FILE.format(getFile()));
      }
    }
    return this;
  }

  public FileAssert isFile() {
    return isFile(true);
  }

  public FileAssert isDirectory(final boolean flag) {
    if (getFile().isDirectory() != flag) {
      if (flag) {
        throw new AssertionException(Messages.NOT_DIRECTORY.format(getFile()));
      }
      else {
        throw new AssertionException(Messages.IS_DIRECTORY.format(getFile()));
      }
    }
    return this;
  }

  public FileAssert isDirectory() {
    return isDirectory(true);
  }

  public FileAssert isReadable(final boolean flag) {
    if (getFile().canRead() != flag) {
      if (flag) {
        throw new AssertionException(Messages.NOT_READABLE.format(getFile()));
      }
      else {
        throw new AssertionException(Messages.IS_READABLE.format(getFile()));
      }
    }
    return this;
  }

  public FileAssert isReadable() {
    return isReadable(true);
  }

  public FileAssert isWritable(final boolean flag) {
    if (getFile().canWrite() != flag) {
      if (flag) {
        throw new AssertionException(Messages.NOT_WRITABLE.format(getFile()));
      }
      else {
        throw new AssertionException(Messages.IS_WRITABLE.format(getFile()));
      }
    }
    return this;
  }

  public FileAssert isWritable() {
    return isWritable(true);
  }

  public FileAssert isHidden(final boolean flag) {
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

  public FileAssert isHidden() {
    return isHidden(true);
  }

  //    public FileAssert isExecutable(final boolean flag) {
  //        if (getFile().canExecute() != flag) {
  //            if (flag) {
  //                throw new AssertionException(Messages.NOT_EXECUTABLE.format(getFile()));
  //            }
  //            else {
  //                throw new AssertionException(Messages.IS_EXECUTABLE.format(getFile()));
  //            }
  //        }
  //        return this;
  //    }
  //
  //    public FileAssert isExecutable() {
  //        return isExecutable(true);
  //    }

  public FileAssert isAbsolute(final boolean flag) {
    if (getFile().isAbsolute() != flag) {
      if (flag) {
        throw new AssertionException(Messages.NOT_ABSOLUTE.format(getFile()));
      }
      else {
        throw new AssertionException(Messages.IS_ABSOLUTE.format(getFile()));
      }
    }
    return this;
  }

  public FileAssert isAbsolute() {
    return isAbsolute(true);
  }

  public class AssertionException
      extends RuntimeException
  {
    private static final long serialVersionUID = 1;

    public AssertionException(final String message) {
      super(message);
    }

    public File getFile() {
      return FileAssert.this.getFile();
    }
  }
}
