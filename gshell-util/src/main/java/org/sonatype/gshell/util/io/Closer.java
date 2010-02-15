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

package org.sonatype.gshell.util.io;

import org.slf4j.Logger;
import org.sonatype.gossip.Log;

import java.io.Closeable;
import java.io.IOException;

/**
 * Quietly closes {@link Closeable} objects.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class Closer
{
    private static final Logger log = Log.getLogger(Closer.class);
    
    public static void close(final Closeable... targets) {
        if (targets != null) {
            for (Closeable c : targets) {
                if (c == null) {
                    continue;
                }

                try {
                    c.close();
                }
                catch (IOException e) {
                    log.trace(e.getMessage(), e);
                }
            }
        }
    }
}