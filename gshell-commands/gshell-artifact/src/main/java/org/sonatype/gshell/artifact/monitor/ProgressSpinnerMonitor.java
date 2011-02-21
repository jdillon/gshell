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
package org.sonatype.gshell.artifact.monitor;

import org.apache.maven.repository.ArtifactTransferEvent;
import org.apache.maven.repository.ArtifactTransferListener;
import org.fusesource.jansi.Ansi;
import org.sonatype.gshell.command.IO;

/**
 * A transfer monitor providing a simple spinning progress interface.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 3.0
 */
public class ProgressSpinnerMonitor
    implements ArtifactTransferListener
{
    private static final String CARRIAGE_RETURN = "\r";

    private final IO io;

    private final ProgressSpinner spinner = new ProgressSpinner();

    private long complete;

    public ProgressSpinnerMonitor(final IO io) {
        assert io != null;
        this.io = io;
    }

    private void print(final String message) {
        if (!io.isQuiet()) {
            io.out.print(message);
            io.out.print(CARRIAGE_RETURN);
            io.out.flush();
        }
    }

    private void println(final String message) {
        if (!io.isQuiet()) {
            io.out.println(message);
            io.out.flush();
        }
    }

    public boolean isShowChecksumEvents() {
        return false;
    }

    public void setShowChecksumEvents(final boolean b) {
        // ???
    }

    public void transferInitiated(final ArtifactTransferEvent event) {
        assert event != null;

        // ???
    }

    public void transferStarted(final ArtifactTransferEvent event) {
        assert event != null;

        complete = 0;

        spinner.reset();

        String type = renderRequestType(event);
        String location = event.getResource().getUrl();
        String message = type + ": " + location;

        println(message);
    }

    public void transferProgress(final ArtifactTransferEvent event) {
        assert event != null;

        long total = event.getResource().getContentLength();
        complete += event.getTransferredBytes();

        String message = renderProgressBytes(complete, total);

        print(spinner.spin(message));
    }

    public void transferCompleted(final ArtifactTransferEvent event) {
        assert event != null;

        spinner.stop();

        long total = event.getTransferredBytes();
        String type = renderRequestTypeFinished(event);
        String bytes = renderBytes(total);

        print(Ansi.ansi().eraseLine().toString());
        print(type + " " + bytes);

    }

    private String renderRequestType(final ArtifactTransferEvent event) {
        assert event != null;

        return event.getRequestType() == ArtifactTransferEvent.REQUEST_PUT ? "Uploading" : "Downloading";
    }

    private String renderRequestTypeFinished(final ArtifactTransferEvent event) {
        assert event != null;

        return event.getRequestType() == ArtifactTransferEvent.REQUEST_PUT ? "Uploaded" : "Downloaded";
    }

    private String renderProgressBytes(final long length, final long total) {
        if (total > 1024) {
            return length / 1024 + "/" + (total == -1 ? "?" : total / 1024 + "K");
        }
        else {
            return length + "/" + (total == -1 ? "?" : total + "b");
        }
    }

    private String renderBytes(final long length) {
        if (length > 1024) {
            return length / 1024 + "K";
        }
        else {
            return length + "b";
        }
    }
}