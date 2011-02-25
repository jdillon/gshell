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
package org.sonatype.gshell.vfs.provider.truezip;

import de.schlichtherle.io.ArchiveDetector;
import de.schlichtherle.io.File;
import de.schlichtherle.io.FileInputStream;
import de.schlichtherle.io.FileOutputStream;
import org.apache.commons.vfs.FileName;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.RandomAccessContent;
import org.apache.commons.vfs.VFS;
import org.apache.commons.vfs.impl.DefaultFileSystemManager;
import org.apache.commons.vfs.provider.AbstractFileObject;
import org.apache.commons.vfs.provider.LayeredFileName;
import org.apache.commons.vfs.provider.UriParser;
import org.apache.commons.vfs.util.RandomAccessMode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * <h href="https://truezip.dev.java.net">TrueZIP</a> file object.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class TruezipFileObject
    extends AbstractFileObject
    implements FileObject
{
    private File file;

    private FileObject fileObject;

    /**
     * Creates a non-root file.
     */
    protected TruezipFileObject(final TruezipFileSystem fileSystem, final FileName name) throws FileSystemException {
        super(name, fileSystem);
    }

    /**
     * Returns the local file that this file object represents.
     */
    protected File getLocalFile() {
        return file;
    }

    /**
     * Attaches this file object to its file resource.
     */
    protected void doAttach() throws Exception {
        if (file == null) {
            LayeredFileName layeredFileName = (LayeredFileName) getName();
            String fileName = layeredFileName.getOuterName().getRootURI() + layeredFileName.getOuterName().getPathDecoded();

            FileObject outer = getFileSystem().resolveFile(fileName);
            if (outer instanceof TruezipFileObject) {
                fileName = layeredFileName.getOuterName().getPathDecoded() + getName().getPathDecoded();
                file = new File(fileName, ArchiveDetector.ALL);
            }
            else {
                fileObject = outer;
                DefaultFileSystemManager dfsMgr = (DefaultFileSystemManager) VFS.getManager();
                file = new File(dfsMgr.getTemporaryFileStore().allocateFile(getName().getBaseName()));
            }
        }
    }

    /**
     * Returns the file's type.
     */
    protected FileType doGetType() throws Exception {
        if (fileObject != null) {
            return fileObject.getType();
        }
        if (!file.exists() && file.length() < 1) {
            return FileType.IMAGINARY;
        }
        if (file.isDirectory()) {
            return FileType.FOLDER;
        }
        return FileType.FILE;
    }

    /**
     * Returns the children of the file.
     */
    protected String[] doListChildren() throws Exception {
        return UriParser.encode(file.list());
    }

    /**
     * Deletes this file, and all children.
     */
    protected void doDelete() throws Exception {
        if (!file.deleteAll()) {
            throw new FileSystemException("vfs.provider.truezip/delete-file.error", file);
        }
    }

    /**
     * Rename this file
     */
    protected void doRename(FileObject newfile) throws Exception {
        if (!file.renameTo(((TruezipFileObject) newfile).getLocalFile())) {
            throw new FileSystemException("vfs.provider.truezip/rename-file.error", new String[]{file.toString(), newfile.toString()});
        }
    }

    /**
     * Creates this folder.
     */
    protected void doCreateFolder() throws Exception {
        if (!file.mkdirs()) {
            throw new FileSystemException("vfs.provider.truezip/create-folder.error", file);
        }
    }

    /**
     * Determines if this file can be written to.
     */
    protected boolean doIsWriteable() throws FileSystemException {
        return file.canWrite();
    }

    /**
     * Determines if this file is hidden.
     */
    protected boolean doIsHidden() {
        return file.isHidden();
    }

    /**
     * Determines if this file can be read.
     */
    protected boolean doIsReadable() throws FileSystemException {
        return file.canRead();
    }

    /**
     * Gets the last modified time of this file.
     */
    protected long doGetLastModifiedTime() throws FileSystemException {
        return file.lastModified();
    }

    /**
     * Sets the last modified time of this file.
     */
    protected void doSetLastModifiedTime(final long modtime) throws FileSystemException {
        // noinspection ResultOfMethodCallIgnored
        file.setLastModified(modtime);
    }

    /**
     * Creates an input stream to read the content from.
     */
    protected InputStream doGetInputStream() throws Exception {
        return new FileInputStream(file);
    }

    /**
     * Creates an output stream to write the file content to.
     */
    protected OutputStream doGetOutputStream(boolean bAppend) throws Exception {
        return new FileOutputStream(file, bAppend);
    }

    /**
     * Returns the size of the file content (in bytes).
     */
    protected long doGetContentSize() throws Exception {
        return file.length();
    }

    protected RandomAccessContent doGetRandomAccessContent(final RandomAccessMode mode) throws Exception {
        throw new IOException("Not implemented"); //return new LocalFileRandomAccessContent(file, mode);
    }
}