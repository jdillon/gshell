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

import javax.inject.Inject;
import javax.inject.Provider;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.provider.DelegateFileObject;
import org.apache.commons.vfs.provider.local.LocalFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.gshell.variables.Variables;

import java.io.File;
import java.lang.reflect.Field;

/**
 * {@link FileSystemAccess} component.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class FileSystemAccessImpl
    implements FileSystemAccess
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final FileSystemManager fileSystemManager;

    private final Provider<Variables> variables;

    @Inject
    public FileSystemAccessImpl(final FileSystemManager fileSystemManager, final Provider<Variables> variables) {
        assert fileSystemManager != null;
        this.fileSystemManager = fileSystemManager;
        assert variables != null;
        this.variables = variables;
    }

    public FileSystemManager getManager() {
        return fileSystemManager;
    }

    public FileObject getCurrentDirectory(final Variables vars) throws FileSystemException {
        assert vars != null;

        FileObject cwd = null;

        Object var = vars.get(CWD);
        if (var instanceof String) {
            log.trace("Resolving CWD from string: {}", var);

            cwd = getManager().resolveFile((String)var);
        }
        else if (var instanceof FileObject) {
            cwd = (FileObject)var;
        }
        else if (var != null) {
            throw new RuntimeException("Invalid variable type for '" + CWD + "'; expected String or FileObject; found: " + var.getClass());
        }

        if (cwd == null) {
            log.trace("CWD not set, resolving from user.dir");

            // TODO: May need to ask the Application for this, as it might be different depending on the context (ie. remote user, etc)
            String userDir = "file://" + System.getProperty("user.dir");
            cwd = getManager().resolveFile(userDir);
        }

        return cwd;
    }

    public FileObject getCurrentDirectory() throws FileSystemException {
        log.trace("Resolving CWD from application variables");

        return getCurrentDirectory(variables.get());
    }

    public void setCurrentDirectory(final Variables vars, final FileObject dir) throws FileSystemException {
        assert vars != null;
        assert dir != null;

        log.trace("Setting CWD: {}", dir);

        // Make sure that the given file object exists and is really a directory
        if (!dir.exists()) {
            throw new RuntimeException("Directory not found: " + dir.getName());
        }
        else if (!dir.getType().hasChildren()) {
            throw new RuntimeException("File can not contain children: " + dir.getName());
        }

        vars.parent().set(CWD, dir);
    }

    public void setCurrentDirectory(final FileObject dir) throws FileSystemException {
        assert dir != null;

        log.trace("Setting CWD to application variables");

        setCurrentDirectory(variables.get(), dir);
    }

    public FileObject resolveFile(final FileObject baseFile, final String name) throws FileSystemException {
        FileObject f = getManager().resolveFile(baseFile, name);
        FileObject d = dereference(f);
        if (d != null) {
            d.refresh();
        }
        return f;
    }

    public FileObject resolveFile(final String name) throws FileSystemException {
        FileObject f = getManager().resolveFile(getCurrentDirectory(), name);
        FileObject d = dereference(f);
        if (d != null) {
            d.refresh();
        }
        return f;
    }

    public boolean isLocalFile(final FileObject file) {
        return file instanceof LocalFile;
    }

    public File getLocalFile(final FileObject file) throws FileSystemException {
        if (!isLocalFile(file)) {
            throw new FileSystemException("Unable to get local file from: " + file.getClass());
        }

        try {
            file.refresh();
            Field field = LocalFile.class.getDeclaredField("file");

            try {
                return (File)field.get(file);
            }
            catch (IllegalAccessException ignore) {
                // try again
                field.setAccessible(true);
                return (File)field.get(file);
            }
        }
        catch (Exception e) {
            throw new FileSystemException(e);
        }
    }

    //
    // HACK: dereference() is only here because the DelegateFileObject impl is kinda broken.
    //

    public FileObject dereference(final FileObject file) throws FileSystemException {
        assert file != null;

        if (file instanceof DelegateFileObject) {
            try {
                file.refresh();
                Field field = DelegateFileObject.class.getDeclaredField("file");

                try {
                    return (FileObject)field.get(file);
                }
                catch (IllegalAccessException ignore) {
                    // try again
                    field.setAccessible(true);
                    return (FileObject)field.get(file);
                }
            }
            catch (Exception e) {
                throw new FileSystemException(e);
            }
        }

        return file;
    }

    public FileObject createVirtualFileSystem(final String rootUri) throws FileSystemException {
        assert rootUri != null;
        FileObject file = getManager().resolveFile(rootUri);
        return getManager().createVirtualFileSystem(file);
    }

    public FileObject createVirtualFileSystem(final FileObject rootFile) throws FileSystemException {
        assert rootFile != null;
        return getManager().createVirtualFileSystem(rootFile);
    }

}