/**
 * Copyright (c) 2009-2011 the original author or authors.
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
package org.sonatype.gshell.commands.vfs;

import org.apache.commons.vfs.FileContent;
import org.apache.commons.vfs.FileContentInfo;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileType;
import org.sonatype.gshell.command.Command;
import org.sonatype.gshell.command.CommandContext;
import org.sonatype.gshell.command.IO;
import org.sonatype.gshell.util.cli2.Argument;
import org.sonatype.gshell.vfs.FileObjects;

import java.security.cert.Certificate;
import java.text.DateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Display information about a file.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
@Command(name="vfs/fileinfo")
public class FileInfoCommand
    extends VfsCommandSupport
{
    @Argument(required=true)
    private String path;

    public Object execute(final CommandContext context) throws Exception {
        assert context != null;
        IO io = context.getIo();

        FileObject file = resolveFile(context, path);

        io.println("URL: {}", file.getURL());
        io.println("Name: {}", file.getName());
        io.println("BaseName: {}", file.getName().getBaseName());
        io.println("Extension: {}", file.getName().getExtension());
        io.println("Path: {}", file.getName().getPath());
        io.println("Scheme: {}", file.getName().getScheme());
        io.println("URI: {}", file.getName().getURI());
        io.println("Root URI: {}", file.getName().getRootURI());
        io.println("Parent: {}", file.getName().getParent());
        io.println("Type: {}", file.getType());
        io.println("Exists: {}", file.exists());
        io.println("Readable: {}", file.isReadable());
        io.println("Writeable: {}", file.isWriteable());
        io.println("Root path: {}", file.getFileSystem().getRoot().getName().getPath());

        if (file.exists()) {
            FileContent content = file.getContent();
            FileContentInfo contentInfo = content.getContentInfo();
            io.println("Content type: {}", contentInfo.getContentType());
            io.println("Content encoding: {}", contentInfo.getContentEncoding());

            try {
                // noinspection unchecked
                Map<String,Object> attrs = content.getAttributes();
                if (attrs != null && !attrs.isEmpty()) {
                    io.println("Attributes:");
                    for (Map.Entry<String,Object> entry : attrs.entrySet()) {
                        io.println("    {}='{}'", entry.getKey(), entry.getValue());
                    }
                }
            }
            catch (FileSystemException e) {
                io.println("File attributes are NOT supported");
            }

            try {
                Certificate[] certs = content.getCertificates();
                if (certs != null && certs.length != 0) {
                    io.println("Certificate:");
                    for (Certificate cert : certs) {
                        io.println("    {}", cert);
                    }
                }
            }
            catch (FileSystemException e) {
                io.println("File certificates are NOT supported");
            }

            if (file.getType().equals(FileType.FILE)) {
                io.println("Size: {} bytes", content.getSize());
            }
            else if (file.getType().hasChildren() && file.isReadable()) {
                FileObject[] children = file.getChildren();
                io.println("Directory with {} files", children.length);

                for (int iterChildren = 0; iterChildren < children.length; iterChildren++) {
                    io.println("#{}:{}", iterChildren, children[iterChildren].getName());
                    if (iterChildren > 5) {
                        break;
                    }
                }
            }

            io.println("Last modified: {}", DateFormat.getInstance().format(new Date(content.getLastModifiedTime())));
        }
        else {
            io.println("The file does not exist");
        }

        FileObjects.close(file);
        
        return Result.SUCCESS;
    }
}
