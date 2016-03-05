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
package org.sonatype.gshell.commands.file;

import java.io.File;

import jline.console.completer.Completer;

import org.codehaus.plexus.util.FileUtils;
import org.sonatype.gshell.command.Command;
import org.sonatype.gshell.command.CommandContext;
import org.sonatype.gshell.command.support.CommandActionSupport;
import org.sonatype.gshell.file.FileSystemAccess;
import org.sonatype.gshell.util.cli2.Argument;
import org.sonatype.gshell.util.cli2.Option;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Copy file or directory
 *
 * @author <a href="mailto:olamy@apache.org">Olivier Lamy</a>
 * @since 2.6.3
 */
@Command(name="cp")
public class CopyCommand
    extends CommandActionSupport
{
    private final FileSystemAccess fileSystem;
    
    @Argument(required=true,index=0)
    private String source;

    @Argument(required=true,index=1)
    private String target;    
    
    @Option(name="r", longName="recursive")
    private boolean recursive;    
    
    @Inject
    public CopyCommand(final FileSystemAccess fileSystem) {
        assert fileSystem != null;
        this.fileSystem = fileSystem;
    }    
    
    @Inject
    public CopyCommand installCompleters(final @Named("file-name") Completer c1) {
        assert c1 != null;
        // Add completer for source and target
        setCompleters(c1, c1, null);
        return this;
    }    
    
    public Object execute(final CommandContext context) throws Exception {
        assert context != null;
        
        File sourceFile = fileSystem.resolveFile( source );
        File targetFile = fileSystem.resolveFile( target );

        if (sourceFile.isDirectory()) {
            // for cp -r /tmp/foo /home : we must create first the directory /home/foo
            targetFile = new File(targetFile, sourceFile.getName());
            if (!targetFile.exists()) {
                targetFile.mkdirs();
            }
            if (recursive) {
                FileUtils.copyDirectoryStructure(sourceFile, targetFile);
            }
            else {
                FileUtils.copyDirectory(sourceFile, targetFile);
            }
        }
        else {
            if (targetFile.isDirectory()) {
                FileUtils.copyFileToDirectory(sourceFile, targetFile);
            }
            else {
                FileUtils.copyFile(sourceFile, targetFile);
            }
        }
   
        return Result.SUCCESS;
    }
    
}
