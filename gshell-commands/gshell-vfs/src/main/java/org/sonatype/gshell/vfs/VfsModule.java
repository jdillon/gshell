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

package org.sonatype.gshell.vfs;

import com.google.inject.AbstractModule;
import org.apache.commons.vfs.FileContentInfoFactory;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.FilesCache;
import org.apache.commons.vfs.cache.SoftRefFilesCache;
import org.apache.commons.vfs.impl.FileContentInfoFilenameFactory;
import org.sonatype.gshell.vfs.builder.FileSystemManagerProvider;

/**
 * VFS module.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.3
 */
public class VfsModule
    extends AbstractModule
{
    @Override
    protected void configure() {
        bind(FileSystemAccess.class).to(FileSystemAccessImpl.class);
        bind(FileSystemManager.class).toProvider(FileSystemManagerProvider.class);
        bind(FilesCache.class).to(SoftRefFilesCache.class);
        bind(FileContentInfoFactory.class).to(FileContentInfoFilenameFactory.class);
        
        // TODO: Add more bindings to setup desired VFS components:
        //      DefaultFileReplicator
        //      PrivilegedFileReplicator

        // TODO: Configure VFS providers
    }
}