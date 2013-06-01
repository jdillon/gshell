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
package org.sonatype.gshell.commands.ssh;

import org.apache.sshd.SshServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ???
 *
 * @since 2.3
 */
public class SshServerFactory
{
    private final Logger log = LoggerFactory.getLogger(SshServerFactory.class);

    private SshServer server;

    private boolean start;

    public SshServerFactory(final SshServer server) {
        assert server != null;
        this.server = server;
    }

    public boolean isStart() {
        return start;
    }

    public void setStart(final  boolean start) {
        this.start = start;
    }

    public void start() throws Exception {
        if (start) {
            try {
                server.start();
            }
            catch (Exception e) {
                log.error("Failed to start server", e);
                throw e;
            }
        }
    }

    public void stop() throws Exception {
        if (start) {
            server.stop();
        }
    }

}
