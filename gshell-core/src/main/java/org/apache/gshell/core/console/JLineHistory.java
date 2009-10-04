/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.gshell.core.console;

import org.apache.gshell.History;
import org.apache.gshell.VariableNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

import jline.console.SimpleHistory;

/**
 * Implementation of {@link History} for <a href="http://jline.sf.net">JLine</a>.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 *
 * @since 2.0
 */
public class JLineHistory
    implements History, VariableNames
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private jline.console.History delegate = new SimpleHistory();

    private File storeFile;

    public JLineHistory() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    delegate.flushBuffer();
                }
                catch (IOException e) {
                    log.error("Failed to flush history buffer", e);
                }
            }
        });
    }

    public jline.console.History getDelegate() {
        return delegate;
    }

    public void setStoreFile(final File file) throws IOException {
        assert file != null;

        File dir = file.getParentFile();
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                log.debug("Unable to create directory: {}", dir);
            }
        }

        this.storeFile = file;
        log.debug("History file: {}", storeFile);

        delegate.setHistoryFile(storeFile);
    }

    public void add(final String element) {
        assert element != null;
        delegate.addToHistory(element);
    }

    public void clear() {
        delegate.clear();
    }

    public void purge() throws IOException {
        clear();

        if (storeFile == null) {
            log.warn("History storage file not configured; nothing to purge");
        }
        else {
            log.debug("Purging history file: {}", storeFile);

            delegate.flushBuffer();

            File originalFile = storeFile;

            // Create and install a new history file, to deal with windows shit locking
            // and lack of real control over the streams used by jline.History
            File tmp = File.createTempFile(getClass().getSimpleName(), "temp");
            tmp.deleteOnExit();
            setStoreFile(tmp);

            // Delete the original then re-install it
            if (!originalFile.delete()) {
                log.warn("Unable to remove history file: {}", originalFile);
            }
            setStoreFile(originalFile);

            if (!tmp.delete()) {
                log.warn("Unable to remove file: {}", tmp);
            }
        }
    }

    public int size() {
        return delegate.size();
    }

    @SuppressWarnings({"unchecked"})
    public List<String> elements() {
        return (List<String>)delegate.getHistoryList();
    }
}