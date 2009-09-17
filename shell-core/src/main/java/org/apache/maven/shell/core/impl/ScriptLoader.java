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
import org.apache.maven.shell.Branding;
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
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class ScriptLoader
    implements VariableNames
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final Shell shell;

    private final Branding branding;

    public ScriptLoader(final Shell shell) {
        assert shell != null;
        this.shell = shell;
        this.branding = shell.getBranding();
    }

    public void loadProfileScripts() throws Exception {
        log.debug("Loading profile scripts");
        String fileName = shell.getBranding().getProfileScriptName();
        loadSharedScript(fileName);
        loadUserScript(fileName);
    }

    public void loadInteractiveScripts() throws Exception {
        log.debug("Loading interactive scripts");
        String fileName = shell.getBranding().getInteractiveScriptName();
        loadSharedScript(fileName);
        loadUserScript(fileName);
    }

    public void loadScript(final File file) throws Exception {
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

    public void loadUserScript(final String fileName) throws Exception {
        assert fileName != null;

        File file = new File(branding.getUserContextDir(), fileName);
        if (file.exists()) {
            log.debug("Loading user-script: {}", file);
            loadScript(file);
        }
        else {
            log.debug("User script is not present: {}", file);
        }
    }

    public void loadSharedScript(final String fileName) throws Exception {
        assert fileName != null;

        File file = new File(branding.getShellContextDir(), fileName);
        if (file.exists()) {
            log.debug("Loading shared-script: {}", file);
            loadScript(file);
        }
        else {
            log.debug("Shared script is not present: {}", file);
        }
    }
}