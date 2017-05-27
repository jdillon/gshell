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

import com.planet57.gshell.util.i18n.I18N;
import com.planet57.gshell.util.i18n.MessageBundle;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Helper to assert various file requirements.
 *
 * @since 2.0
 */
public class FileAssert
{
  private interface Messages
    extends MessageBundle
  {
    @DefaultMessage("File not found: %s")
    String NOT_FOUND(File file);

    @DefaultMessage("File exists: %s")
    String EXISTS(File file);

    @DefaultMessage("Not a file: %s")
    String NOT_FILE(File file);

    @DefaultMessage("Is a file: %s")
    String IS_FILE(File file);

    @DefaultMessage("File is not a directory: %s")
    String NOT_DIRECTORY(File file);

    @DefaultMessage("File is s a directory: %s")
    String IS_DIRECTORY(File file);

    @DefaultMessage("File is not readable: %s")
    String NOT_READABLE(File file);

    @DefaultMessage("File is readable: %s")
    String IS_READABLE(File file);

    @DefaultMessage("File is not writable %s")
    String NOT_WRITABLE(File file);

    @DefaultMessage("File is s writable: %s")
    String IS_WRITABLE(File file);

    @DefaultMessage("File is not hidden: %s")
    String NOT_HIDDEN(File file);

    @DefaultMessage("File is hidden: %s")
    String IS_HIDDEN(File file);

    @DefaultMessage("File is not executable: %s")
    String NOT_EXECUTABLE(File file);

    @DefaultMessage("File is executable: %s")
    String IS_EXECUTABLE(File file);

    @DefaultMessage("File is not absolute: %s")
    String NOT_ABSOLUTE(File file);

    @DefaultMessage("File is absolute: %s")
    String IS_ABSOLUTE(File file);
  }

  private static final Messages messages = I18N.create(Messages.class);

  private final File file;

  public FileAssert(final File file) {
    this.file = checkNotNull(file);
  }

  public File getFile() {
    return file;
  }

  public FileAssert exists(final boolean flag) {
    if (getFile().exists() != flag) {
      if (flag) {
        throw new AssertionException(messages.NOT_FOUND(getFile()));
      }
      else {
        throw new AssertionException(messages.EXISTS(getFile()));
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
        throw new AssertionException(messages.NOT_FILE(getFile()));
      }
      else {
        throw new AssertionException(messages.IS_FILE(getFile()));
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
        throw new AssertionException(messages.NOT_DIRECTORY(getFile()));
      }
      else {
        throw new AssertionException(messages.IS_DIRECTORY(getFile()));
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
        throw new AssertionException(messages.NOT_READABLE(getFile()));
      }
      else {
        throw new AssertionException(messages.IS_READABLE(getFile()));
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
        throw new AssertionException(messages.NOT_WRITABLE(getFile()));
      }
      else {
        throw new AssertionException(messages.IS_WRITABLE(getFile()));
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
        throw new AssertionException(messages.NOT_HIDDEN(getFile()));
      }
      else {
        throw new AssertionException(messages.IS_HIDDEN(getFile()));
      }
    }
    return this;
  }

  public FileAssert isHidden() {
    return isHidden(true);
  }

  public FileAssert isExecutable(final boolean flag) {
      if (getFile().canExecute() != flag) {
          if (flag) {
              throw new AssertionException(messages.NOT_EXECUTABLE(getFile()));
          }
          else {
              throw new AssertionException(messages.IS_EXECUTABLE(getFile()));
          }
      }
      return this;
  }

  public FileAssert isExecutable() {
      return isExecutable(true);
  }

  public FileAssert isAbsolute(final boolean flag) {
    if (getFile().isAbsolute() != flag) {
      if (flag) {
        throw new AssertionException(messages.NOT_ABSOLUTE(getFile()));
      }
      else {
        throw new AssertionException(messages.IS_ABSOLUTE(getFile()));
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
