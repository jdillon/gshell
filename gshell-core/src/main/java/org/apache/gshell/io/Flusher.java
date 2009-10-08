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

package org.apache.gshell.io;

import org.apache.gshell.internal.Log;

import java.io.Flushable;
import java.io.IOException;

/**
 * Quietly flushes {@link Flushable}s.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 *
 * @since 2.0
 */
public class Flusher
{
    public static void flush(final Flushable... flushable) {
        if (flushable != null) {
            for (Flushable f : flushable) {
                if (f == null) continue;
                
                try {
                    f.flush();
                }
                catch (IOException e) {
                    Log.trace("Failed to flush: ", f, e);
                }
            }
        }
    }
}