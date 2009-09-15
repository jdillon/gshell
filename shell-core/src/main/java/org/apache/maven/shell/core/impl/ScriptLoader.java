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

import org.apache.maven.shell.Shell;
import org.apache.maven.shell.VariableNames;
import org.apache.maven.shell.io.Closer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Helper to load profile scripts.
 *
 * @version $Rev$ $Date$
 */
public class ScriptLoader
    implements  VariableNames
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    public static final String MVNSH_PROFILE = "mvnsh.profile";

    public static final String MVNSH_RC = "mvnsh.rc";

    private final Shell shell;

    public ScriptLoader(final Shell shell) {
        assert shell != null;

        this.shell = shell;
    }

    public void loadProfileScripts() throws Exception {
        log.debug("Loading profile scripts");

        loadSharedScript(MVNSH_PROFILE);
        loadUserScript(MVNSH_PROFILE);
    }

    public void loadInteractiveScripts() throws Exception {
        log.debug("Loading interactive scripts");

        loadSharedScript(MVNSH_RC);
        loadUserScript(MVNSH_RC);
    }

    private void loadScript(final File file) throws Exception {
        assert file != null;

        BufferedReader reader = new BufferedReader(new FileReader(file));

        try {
            String line;

            while ((line = reader.readLine()) != null) {
                shell.execute(line);
            }
        }
        finally {
            Closer.close(reader);
        }
    }

    private File getUserDir() {
        return new File(new File(shell.getVariables().get(MVNSH_USER_HOME, String.class)), ".m2");
    }

    private File getSharedDir() {
        return new File(new File(shell.getVariables().get(MVNSH_HOME, String.class)), "etc");
    }

    private void loadUserScript(final String fileName) throws Exception {
        assert fileName != null;

        File file = new File(getUserDir(), fileName);

        if (file.exists()) {
            log.debug("Loading user-script: {}", file);

            loadScript(file);
        }
        else {
            log.debug("User script is not present: {}", file);
        }
    }

    private void loadSharedScript(final String fileName) throws Exception {
        assert fileName != null;

        File file = new File(getSharedDir(), fileName);

        if (file.exists()) {
            log.debug("Loading shared-script: {}", file);

            loadScript(file);
        }
        else {
            log.debug("Shared script is not present: {}", file);
        }
    }
}