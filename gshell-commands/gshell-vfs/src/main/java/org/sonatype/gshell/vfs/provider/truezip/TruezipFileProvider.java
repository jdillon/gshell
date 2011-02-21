/**
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
package org.sonatype.gshell.vfs.provider.truezip;

import org.apache.commons.vfs.Capability;
import org.apache.commons.vfs.FileName;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystem;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemOptions;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.provider.AbstractLayeredFileProvider;
import org.apache.commons.vfs.provider.LayeredFileName;
import org.apache.commons.vfs.provider.local.LocalFileName;
import org.apache.commons.vfs.provider.local.LocalFileNameParser;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * <h href="https://truezip.dev.java.net">TrueZIP</a> file provider.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class TruezipFileProvider
    extends AbstractLayeredFileProvider
{
    public static final Collection<Capability> CAPABILITIES = Collections.unmodifiableCollection(Arrays.asList(
        Capability.CREATE,
        Capability.DELETE,
        Capability.RENAME,
        Capability.GET_TYPE,
        Capability.GET_LAST_MODIFIED,
        Capability.SET_LAST_MODIFIED_FILE,
        Capability.SET_LAST_MODIFIED_FOLDER,
        Capability.LIST_CHILDREN,
        Capability.READ_CONTENT,
        Capability.URI,
        Capability.WRITE_CONTENT,
        Capability.APPEND_CONTENT
    ));

    /**
     * Creates a layered file system.  This method is called if the file system is not cached.
     *
     * @param scheme    The URI scheme.
     * @param file      The file to create the file system on top of.
     * @return          The file system.
     */
    protected FileSystem doCreateFileSystem(final String scheme, final FileObject file, final FileSystemOptions options) throws FileSystemException {
        FileName name = new LayeredFileName(scheme, file.getName(), FileName.ROOT_PATH, FileType.FOLDER);
        return new TruezipFileSystem(name, file, options);
    }

    /**
     * Determines if a name is an absolute file name.
     */
    public boolean isAbsoluteLocalName(final String name) {
        return ((LocalFileNameParser) getFileNameParser()).isAbsoluteName(name);
    }

    /**
     * Creates the filesystem.
     */
    protected FileSystem doCreateFileSystem(final FileName name, final FileSystemOptions options) throws FileSystemException {
        LocalFileName rootName = (LocalFileName) name;
        return new TruezipFileSystem(rootName, rootName.getRootFile(), options);
    }

    public Collection getCapabilities() {
        return CAPABILITIES;
    }
}