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
import org.apache.commons.vfs.FileSystemManager;
import org.sonatype.gshell.variables.Variables;

import java.io.File;

/**
 * Provides access to VFS file systems.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public interface FileSystemAccess
{
    String CWD = "vfs.cwd";

    FileSystemManager getManager();

    FileObject getCurrentDirectory(Variables vars) throws FileSystemException;

    FileObject getCurrentDirectory() throws FileSystemException;

    void setCurrentDirectory(Variables vars, FileObject dir) throws FileSystemException;

    FileObject resolveFile(FileObject baseFile, String name) throws FileSystemException;

    FileObject resolveFile(String name) throws FileSystemException;

    boolean isLocalFile(FileObject file);

    File getLocalFile(FileObject file) throws FileSystemException;

    FileObject dereference(FileObject file) throws FileSystemException;

    FileObject createVirtualFileSystem(String rootUri) throws FileSystemException;

    FileObject createVirtualFileSystem(FileObject rootFile) throws FileSystemException;
}