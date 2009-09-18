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

package org.apache.maven.shell.core.impl.completer;

import jline.Completor;
import org.apache.maven.shell.ShellHolder;
import org.apache.maven.shell.VariableNames;
import org.apache.maven.shell.Variables;
import org.codehaus.plexus.component.annotations.Component;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * {@link Completor} for file names.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @version $Rev$ $Date$
 */
@Component(role=Completor.class, hint="file-name")
public class FileNameCompleter
    implements Completor, VariableNames
{
   public int complete(final String buf, final int cursor, final List candidates) {
        String buffer = (buf == null) ? "" : buf;
        String translated = buffer;

        Variables vars = ShellHolder.get().getVariables();
        String home = vars.get(SHELL_USER_HOME, String.class);

        // special character: ~ maps to the user's home directory
        if (translated.startsWith("~" + File.separator)) {
            translated = home + translated.substring(1);
        }
        else if (translated.startsWith("~")) {
            translated = new File(home).getParentFile().getAbsolutePath();
        }
        else if (!(translated.startsWith(File.separator))) {
            translated = new File("").getAbsolutePath() + File.separator + translated;
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

    @SuppressWarnings({ "unchecked" })
    protected void sortFileNames(final List fileNames) {
        Collections.sort(fileNames);
    }

    @SuppressWarnings({ "unchecked" })
    public int matchFiles(final String buffer, final String translated, final File[] files, final List candidates) {
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