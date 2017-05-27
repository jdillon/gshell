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
import java.io.IOException;

/**
 * Provides access to the file system.
 *
 * @since 2.3
 */
public interface FileSystemAccess
{
  File resolveDir(final String name) throws IOException;

  File getShellHomeDir() throws IOException;

  File getUserDir() throws IOException;

  /**
   * @since 3.0
   */
  void setUserDir(final File dir);

  File getUserHomeDir() throws IOException;

  File resolveFile(File baseDir, final String path) throws IOException;

  File resolveFile(final String path) throws IOException;

  boolean hasChildren(final File file);

  /**
   * @since 3.0
   */
  void mkdir(final File dir) throws IOException;

  /**
   * @since 3.0
   */
  void deleteDirectory(final File dir) throws IOException;

  /**
   * @since 3.0
   */
  void deleteFile(final File file) throws IOException;

  /**
   * @since 3.0
   */
  void copyFile(final File source, final File target) throws IOException;

  /**
   * @since 3.0
   */
  void copyDirectory(final File source, final File target) throws IOException;

  /**
   * @since 3.0
   */
  void copyToDirectory(final File source, final File target) throws IOException;
}
