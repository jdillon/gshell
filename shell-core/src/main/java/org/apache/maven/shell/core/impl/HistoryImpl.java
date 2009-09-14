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

package org.apache.maven.shell.core.impl;

import jline.History;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.maven.shell.VariableNames;

import java.io.File;
import java.io.IOException;

/**
 * Default implementation of the {@link jline.History} component.
 *
 * @version $Rev$ $Date$
 */
@Component(role=History.class)
public class HistoryImpl
    extends History
    implements Initializable, VariableNames
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private static final String FILENAME = "mvnsh.history";

    private File historyFile;

    public void initialize() throws InitializationException {
        historyFile = getDefaultHistoryFile();
        log.debug("History file: {}", historyFile);

        try {
            setHistoryFile(historyFile);
        }
        catch (Exception e) {
            throw new InitializationException(e.getMessage(), e);
        }
    }

    private File getDefaultHistoryFile() {
        // FIXME: Use MVNSH_USER_DIR here
        File dir = new File(new File(System.getProperty("user.home")), ".m2");
        return new File(dir, FILENAME);
    }

    public void setHistoryFile(final File file) throws IOException {
        assert file != null;

        File dir = file.getParentFile();

        if (!dir.exists()) {
            // noinspection ResultOfMethodCallIgnored
            dir.mkdirs();
        }

        log.debug("History file: {}", file);

        super.setHistoryFile(file);
    }

    public void purge() throws IOException {
        clear();
        flushBuffer();

        File tmp = File.createTempFile(FILENAME, "temp");
        tmp.deleteOnExit();
        setHistoryFile(tmp);

        if (!historyFile.delete()) {
            log.warn("Unable to remove history file: {}", historyFile);
        }
        setHistoryFile(historyFile);
        
        //noinspection ResultOfMethodCallIgnored
        tmp.delete();
    }
}