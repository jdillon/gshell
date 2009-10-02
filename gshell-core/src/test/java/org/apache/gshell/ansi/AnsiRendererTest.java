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

package org.apache.gshell.ansi;

import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for the {@link AnsiRenderer} class.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class AnsiRendererTest
{
    private AnsiRenderer renderer;

    @Before
    public void setUp() {
        renderer = new AnsiRenderer();
    }

    @After
    public void tearDown() {
        renderer = null;
    }

    @Test
    public void testTest() throws Exception {
        assertFalse(AnsiRenderer.test("foo"));
        assertTrue(AnsiRenderer.test("@|foo|"));
    }

    @Test
    public void testRender() {
        AnsiBuffer buff = new AnsiBuffer();
        buff.attrib(AnsiCode.BOLD);
        buff.append("foo");
        buff.attrib(AnsiCode.OFF);

        String str = renderer.render("@|bold foo|");
        assertEquals(buff.toString(), str);
    }

    @Test
    public void testRender2() {
        AnsiBuffer buff = new AnsiBuffer();
        buff.attrib(AnsiCode.BOLD);
        buff.attrib(AnsiCode.RED);
        buff.append("foo");
        buff.attrib(AnsiCode.OFF);

        String str = renderer.render("@|bold,red foo|");
        assertEquals(buff.toString(), str);
    }

    @Test
    public void testRender3() {
        AnsiBuffer buff = new AnsiBuffer();
        buff.attrib(AnsiCode.BOLD);
        buff.attrib(AnsiCode.RED);
        buff.append("foo bar baz");
        buff.attrib(AnsiCode.OFF);

        String str = renderer.render("@|bold,red foo bar baz|");
        assertEquals(buff.toString(), str);
    }

    @Test
    public void testRender4() {
        AnsiBuffer buff = new AnsiBuffer();
        buff.attrib(AnsiCode.BOLD);
        buff.attrib(AnsiCode.RED);
        buff.append("foo bar baz");
        buff.attrib(AnsiCode.OFF);
        buff.append(" ick ");
        buff.attrib(AnsiCode.BOLD);
        buff.attrib(AnsiCode.RED);
        buff.append("foo bar baz");
        buff.attrib(AnsiCode.OFF);

        String str = renderer.render("@|bold,red foo bar baz| ick @|bold,red foo bar baz|");
        assertEquals(buff.toString(), str);
    }

    @Test
    public void testRenderNothing() {
        assertEquals("foo", renderer.render("foo"));
    }

    @Test
    public void testRenderInvalidMissingEnd() {
        String str = renderer.render("@|bold foo");
        assertEquals("@|bold foo", str);
    }

    @Test
    public void testRenderInvalidMissingText() {
        String str = renderer.render("@|bold|");
        assertEquals("@|bold|", str);
    }
}