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

package org.apache.gshell.core.completer;

import org.apache.gshell.ShellHolder;
import org.apache.gshell.VariableNames;
import org.apache.gshell.Variables;

import java.io.File;
import java.util.Collections;
import java.util.List;

import jline.console.Completer;

/**
 * {@link Completor} for file names.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 *
 * @since 2.0
 */
public class FileNameCompleter
    implements Completer, VariableNames
{
   public int complete(final String buf, final int cursor, final List<String> candidates) {
        String buffer = (buf == null) ? "" : buf;
        String translated = buffer;

        Variables vars = ShellHolder.get().getVariables();
        File homeDir = vars.get(SHELL_USER_HOME, File.class);

        // special character: ~ maps to the user's home directory
        if (translated.startsWith("~" + File.separator)) {
            translated = homeDir.getPath() + translated.substring(1);
        }
        else if (translated.startsWith("~")) {
            translated = homeDir.getParentFile().getAbsolutePath();
        }
        else if (!(translated.startsWith(File.separator))) {
            String cwd = vars.get(SHELL_USER_DIR, String.class);
            translated = cwd + File.separator + translated;
        }

        File file = new File(translated);

        final File dir;

        if (translated.endsWith(File.separator)) {
            dir = file;
        }
        else {
            dir = file.getParentFile();
        }

        final File[] entries = (dir == null) ? new File[0] : dir.listFiles();

        try {
            return matchFiles(buffer, translated, entries, candidates);
        }
        finally {
            sortFileNames(candidates);
        }
    }

    protected void sortFileNames(final List<String> fileNames) {
        Collections.sort(fileNames);
    }

    public int matchFiles(final String buffer, final String translated, final File[] files, final List<String> candidates) {
        if (files == null) {
            return -1;
        }

        int matches = 0;

        // first pass: just count the matches
        for (File file : files) {
            if (file.getAbsolutePath().startsWith(translated)) {
                matches++;
            }
        }
        for (File file : files) {
            if (file.getAbsolutePath().startsWith(translated)) {
                String name = file.getName() + (((matches == 1) && file.isDirectory()) ? File.separator : " ");
                candidates.add(name);
            }
        }

        final int index = buffer.lastIndexOf(File.separator);

        return index + File.separator.length();
    }
}