/*
 * Copyright (C) 2010 the original author or authors.
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

package org.sonatype.gshell.file;

import javax.inject.Inject;
import javax.inject.Provider;
import org.codehaus.plexus.util.Os;
import org.sonatype.gshell.variables.Variables;

import java.io.File;
import java.io.IOException;

import static org.sonatype.gshell.variables.VariableNames.SHELL_HOME;
import static org.sonatype.gshell.variables.VariableNames.SHELL_USER_DIR;
import static org.sonatype.gshell.variables.VariableNames.SHELL_USER_HOME;

/**
 * {@link FileSystemAccess} component.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.3
 */
public class FileSystemAccessImpl
    implements FileSystemAccess
{
    private final Provider<Variables> variables;

    @Inject
    public FileSystemAccessImpl(final Provider<Variables> variables) {
        assert variables != null;
        this.variables = variables;
    }

    public File resolveDir(final String name) throws IOException {
        assert name != null;
        return variables.get().get(name, File.class).getCanonicalFile();
    }

    public File getShellHomeDir() throws IOException {
        return resolveDir(SHELL_HOME);
    }

    public File getUserDir() throws IOException {
        return resolveDir(SHELL_USER_DIR);
    }

    public File getUserHomeDir() throws IOException {
        return resolveDir(SHELL_USER_HOME);
    }

    public File resolveFile(File baseDir, final String path) throws IOException {
        // baseDir may be null
        // path may be null

        File userDir = getUserDir();

        if (baseDir == null) {
            baseDir = userDir;
        }

        File file;
        if (path == null) {
            file = baseDir;
        }
        else if (path.startsWith("~")) {
            File userHome = getUserHomeDir();
            file = new File(userHome.getPath() + path.substring(1));
        }
        else {
            file = new File(path);
        }

        // support paths like "<drive>:" on windows
        if (Os.isFamily(Os.FAMILY_WINDOWS)) {
            String tmp = file.getPath();
            if (tmp.length() == 2 && tmp.charAt(1) == ':') {
                return file.getCanonicalFile();
            }
        }

        if (!file.isAbsolute()) {
            file = new File(baseDir, file.getPath());
        }

        return file.getCanonicalFile();
    }

    public File resolveFile(final String path) throws IOException {
        return resolveFile(null, path);
    }

    public boolean hasChildren(final File file) {
        assert file != null;

        if (file.isDirectory()) {
            File[] children = file.listFiles();

            if (children != null && children.length != 0) {
                return true;
            }
        }

        return false;
    }
}