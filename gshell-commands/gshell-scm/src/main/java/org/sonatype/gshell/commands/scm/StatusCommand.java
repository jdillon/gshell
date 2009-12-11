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

package org.sonatype.gshell.commands.scm;

import com.google.inject.Inject;
import org.apache.maven.scm.ScmFile;
import org.apache.maven.scm.command.status.StatusScmResult;
import org.apache.maven.scm.repository.ScmRepository;
import org.codehaus.plexus.util.StringUtils;
import org.sonatype.gshell.command.Command;
import org.sonatype.gshell.command.CommandContext;
import org.sonatype.gshell.command.IO;
import org.sonatype.gshell.plexus.PlexusRuntime;

import java.io.File;
import java.util.Iterator;

/**
 * Display the modification status of the files.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 3.0
 */
@Command(name="scm/status")
public class StatusCommand
    extends ScmCommandSupport
{
    @Inject
    public StatusCommand(final PlexusRuntime plexus) {
        super(plexus);
    }

    public Object execute(final CommandContext context) throws Exception {
        assert context != null;
        IO io = context.getIo();

        ScmRepository repository = getScmRepository();
        StatusScmResult result = getScmManager().status(repository, getFileSet());
        checkResult(result);

        File baseDir = getFileSet().getBasedir();

        // Determine the maximum length of the status column
        int maxLen = 0;

        for (Iterator iter = result.getChangedFiles().iterator(); iter.hasNext();) {
            ScmFile file = (ScmFile) iter.next();
            maxLen = Math.max(maxLen, file.getStatus().toString().length());
        }

        for (Iterator iter = result.getChangedFiles().iterator(); iter.hasNext();) {
            ScmFile file = (ScmFile) iter.next();

            // right align all of the statuses
            io.info("{} status for {}", StringUtils.leftPad(file.getStatus().toString(), maxLen), getRelativePath(baseDir, file.getPath()));
        }

        return Result.SUCCESS;
    }

    /**
     * Formats the filename so that it is a relative directory from the base.
     *
     * @param baseDir
     * @param path
     * @return The relative path
     */
    protected String getRelativePath(File baseDir, String path) {
        if (path.equals(baseDir.getAbsolutePath())) {
            return ".";
        }
        else if (path.indexOf(baseDir.getAbsolutePath()) == 0) {
            // the + 1 gets rid of a leading file separator
            return path.substring(baseDir.getAbsolutePath().length() + 1);
        }
        else {
            return path;
        }
    }
}