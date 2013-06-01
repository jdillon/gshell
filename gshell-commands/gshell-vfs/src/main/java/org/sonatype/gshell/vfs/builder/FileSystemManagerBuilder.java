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
package org.sonatype.gshell.vfs.builder;

import javax.inject.Inject;
import org.apache.commons.vfs.CacheStrategy;
import org.apache.commons.vfs.FileContentInfoFactory;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.FilesCache;
import org.apache.commons.vfs.cache.SoftRefFilesCache;
import org.apache.commons.vfs.impl.DefaultFileReplicator;
import org.apache.commons.vfs.impl.FileContentInfoFilenameFactory;
import org.apache.commons.vfs.impl.PrivilegedFileReplicator;
import org.apache.commons.vfs.provider.FileProvider;
import org.apache.commons.vfs.provider.FileReplicator;
import org.apache.commons.vfs.provider.TemporaryFileStore;
import org.apache.commons.vfs.provider.url.UrlFileProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link FileSystemManager} builder.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.3
 */
public class FileSystemManagerBuilder
{
    private static final Logger log = LoggerFactory.getLogger(FileSystemManagerBuilder.class);

    private FilesCache filesCache;

    private CacheStrategy cacheStrategy = CacheStrategy.ON_RESOLVE;

    private FileReplicator fileReplicator;

    private TemporaryFileStore temporaryFileStore;

    private FileContentInfoFactory fileContentInfoFactory;

    private FileProvider defaultProvider;

    // FileObjectDecorator (Class/Constructor of DecoratedFileObject? or make a factory?)

    @Inject(optional=true)
    public FileSystemManagerBuilder setFilesCache(final FilesCache cache) {
        this.filesCache = cache;
        return this;
    }

    @Inject(optional=true)
    public FileSystemManagerBuilder setCacheStrategy(final CacheStrategy strategy) {
        this.cacheStrategy = strategy;
        return this;
    }

    @Inject(optional=true)
    public FileSystemManagerBuilder setFileReplicator(final FileReplicator replicator) {
        this.fileReplicator = replicator;
        return this;
    }

    @Inject(optional=true)
    public FileSystemManagerBuilder setTemporaryFileStore(final TemporaryFileStore store) {
        this.temporaryFileStore = store;
        return this;
    }

    @Inject(optional=true)
    public FileSystemManagerBuilder setFileContentInfoFactory(final FileContentInfoFactory factory) {
        this.fileContentInfoFactory = factory;
        return this;
    }

    @Inject(optional=true)
    public FileSystemManagerBuilder setDefaultProvider(final FileProvider provider) {
        this.defaultProvider = provider;
        return this;
    }

    private void installDefaults() {
        if (filesCache == null) {
            filesCache = new SoftRefFilesCache();
        }

        if (fileReplicator == null || temporaryFileStore == null) {
            DefaultFileReplicator replicator = new DefaultFileReplicator();
            if (fileReplicator == null) {
                fileReplicator = new PrivilegedFileReplicator(replicator);
            }
            if (temporaryFileStore == null) {
                temporaryFileStore = replicator;
            }
        }

        if (fileContentInfoFactory == null) {
            fileContentInfoFactory = new FileContentInfoFilenameFactory();
        }

        if (defaultProvider == null) {
            defaultProvider = new UrlFileProvider();
        }
    }

    public FileSystemManager create() throws Exception {
        installDefaults();

        ConfigurableFileSystemManager fsm = new ConfigurableFileSystemManager();
        log.debug("Creating FSM: {}", fsm);

        assert fileReplicator != null;
        log.debug("  File replicator: {}", fileReplicator);
        fsm.setReplicator(fileReplicator);

        assert temporaryFileStore != null;
        log.debug("  Temporary file store: {}", temporaryFileStore);
        fsm.setTemporaryFileStore(temporaryFileStore);

        assert filesCache != null;
        log.debug("  Files cache: {}", filesCache);
        fsm.setFilesCache(filesCache);

        assert cacheStrategy != null;
        log.debug("  Cache strategy: {}", cacheStrategy);
        fsm.setCacheStrategy(cacheStrategy);

        assert fileContentInfoFactory != null;
        log.debug("  File content info factory: {}", fileContentInfoFactory);
        fsm.setFileContentInfoFactory(fileContentInfoFactory);

        assert defaultProvider != null;
        log.debug("  Default provider: {}", defaultProvider);
        fsm.setDefaultProvider(defaultProvider);

        // Finally init the manager
        fsm.init();

        return fsm;
    }
}