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

package org.apache.maven.shell.i18n;

import junit.framework.TestCase;

/**
 * Unit tests for the {@link ResourceBundleMessageSource} class.
 *
 * @version $Rev$ $Date$
 */
public class ResourceBundleMessageSourceTest
    extends TestCase
{
    MessageSource messages;

    protected void setUp() throws Exception {
        messages = new ResourceBundleMessageSource(getClass());
    }

    public void testLoadAndGetMessage() {
        String a = messages.getMessage("a");
        assertEquals("1", a);

        String b = messages.getMessage("b");
        assertEquals("2", b);

        String c = messages.getMessage("c");
        assertEquals("3", c);

        String f = messages.format("f", a, b, c);
        assertEquals("1 2 3", f);
    }

    public void testMissingResource() throws Exception {
        try {
            messages.getMessage("no-such-code");
        }
        catch (ResourceNotFoundException expected) {}
    }
}