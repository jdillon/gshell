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

package org.apache.maven.shell.ansi;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;

/**
 * Tests for the {@link AnsiRenderWriter} class.
 *
 * @version $Rev$ $Date$
 */
public class AnsiRenderWriterTest
{
    private ByteArrayOutputStream baos;

    private AnsiRenderWriter out;

    @Before
    public void setup() {
        baos = new ByteArrayOutputStream();
        out = new AnsiRenderWriter(baos);
    }

    @After
    public void teardown() {
        out = null;
        baos = null;
    }

    @Test
    public void testRenderNothing() {
        out.print("foo");
        out.flush();

        String result = new String(baos.toByteArray());
        assertEquals("foo", result);
    }
}