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

package org.sonatype.gshell.commands.vfs;

import com.google.inject.Inject;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.sonatype.gshell.command.CommandActionSupport;
import org.sonatype.gshell.command.CommandContext;
import org.sonatype.gshell.notification.ResultNotification;
import org.sonatype.gshell.vfs.FileObjects;
import org.sonatype.gshell.vfs.FileSystemAccess;

/**
 * Support for VFS command actions.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public abstract class VfsCommandSupport
    extends CommandActionSupport
{
    private FileSystemAccess fileSystemAccess;

    @Inject
    public void setFileSystemAccess(final FileSystemAccess fileSystemAccess) {
        assert fileSystemAccess != null;
        this.fileSystemAccess = fileSystemAccess;
    }

    protected FileSystemAccess getFileSystemAccess() {
        assert fileSystemAccess != null;
        return fileSystemAccess;
    }

    protected FileObject getCurrentDirectory(final CommandContext context) throws FileSystemException {
        assert context != null;

        return getFileSystemAccess().getCurrentDirectory(context.getVariables());
    }

    protected void setCurrentDirectory(final CommandContext context, final FileObject dir) throws FileSystemException {
        assert context != null;

        getFileSystemAccess().setCurrentDirectory(context.getVariables(), dir);
    }

    protected FileObject resolveFile(final CommandContext context, final String path) throws FileSystemException {
        assert context != null;
        assert path != null;

        log.trace("Resolving path: {}", path);

        FileObject cwd = getCurrentDirectory(context);
        return getFileSystemAccess().resolveFile(cwd, path);
    }

    //
    // TODO: Make these more generally available to other plugins.  Maybe even to FileObjects?
    //

    protected void ensureFileExists(final FileObject file) throws FileSystemException {
        assert file != null;

        if (!file.exists()) {
            FileObjects.close(file);
            throw new ResultNotification("File not found: " + file.getName(), Result.FAILURE);
        }
    }

    protected void ensureFileHasContent(final FileObject file) throws FileSystemException {
        assert file != null;

        if (!file.getType().hasContent()) {
            FileObjects.close(file);
            throw new ResultNotification("File has no content: " + file.getName(), Result.FAILURE);
        }
    }

    protected void ensureFileHasChildren(final FileObject file) throws FileSystemException {
        assert file != null;

        if (!file.getType().hasChildren()) {
            FileObjects.close(file);
            throw new ResultNotification("File has no children: " + file.getName(), Result.FAILURE);
        }
    }

    protected void ensureFileIsReadable(final FileObject file) throws FileSystemException {
        assert file != null;

        if (!file.getType().hasChildren()) {
            FileObjects.close(file);
            throw new ResultNotification("File is not readable: " + file.getName(), Result.FAILURE);
        }
    }

    protected void ensureFileIsWritable(final FileObject file) throws FileSystemException {
        assert file != null;

        if (!file.getType().hasChildren()) {
            FileObjects.close(file);
            throw new ResultNotification("File is not writable: " + file.getName(), Result.FAILURE);
        }
    }
}