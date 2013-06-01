/*
 * Copyright (c) 2009-2011 the original author or authors.
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility methods for operating on {@link FileObject} instances.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class FileObjects
{
    private static final Logger log = LoggerFactory.getLogger(FileObjects.class);

    public static boolean hasChildren(final FileObject file) throws FileSystemException {
        assert file != null;

        if (file.getType().hasChildren()) {
            FileObject[] children = file.getChildren();

            if (children != null && children.length != 0) {
                return true;
            }
        }

        return false;
    }

    public static void close(final FileObject... files) {
        if (files != null && files.length != 0) {
            for (FileObject file : files) {
                if (file != null) {
                    try {
                        file.close();
                    }
                    catch (FileSystemException e) {
                        log.trace("Failed to close file: " + file, e);
                    }
                }
            }
        }
    }
}