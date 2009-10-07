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

import jline.console.FileHistory;

/**
 * Implementation of {@link History} for <a href="http://jline.sf.net">JLine</a>.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 *
 * @since 2.0
 */
public class HistoryImpl
    implements History, VariableNames
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private FileHistory delegate;

    public HistoryImpl(final File file) throws IOException {
        assert file != null;

        this.delegate = new FileHistory(file);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    delegate.flush();
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

    public void add(final String element) {
        delegate.add(element);
    }

    public void clear() {
        delegate.clear();
    }

    public void purge() throws IOException {
        delegate.purge();
    }

    public int size() {
        return delegate.size();
    }

    public List<String> items() {
        return delegate.items();
    }
}