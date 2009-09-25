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

package org.apache.gshell.cli2;

/**
 * Thrown to indicate that while processing an illegal annotation definition was discovered.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class IllegalAnnotationError
    extends Error
{
    ///CLOVER:OFF
    
    private static final long serialVersionUID = 1;

    public IllegalAnnotationError(String msg) {
        super(msg);
    }

    public IllegalAnnotationError(String msg, Throwable cause) {
        super(msg, cause);
    }

    public IllegalAnnotationError(Throwable cause) {
        super(cause);
    }
}
