/*
 * Copyright (C) 2010 the original author or authors.
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

package org.sonatype.gshell.command.resolver;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link NodePath}.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class NodePathTest
{
    private void assertParent(final String expected, final String path) {
        assertEquals(expected, new NodePath(path).parent().toString());
    }

    @Test
    public void testParent1() {
        assertParent("/foo/bar", "/foo/bar/baz");
    }

    @Test
    public void testParent2() {
        assertParent("foo/bar", "foo/bar/baz");
    }

    @Test
    public void testParent3() {
        assertParent("/", "/");
    }

    @Test
    public void testParent4() {
        assertParent("..", "..");
    }

    private void assertNormalized(final String expected, final String path) {
        assertEquals(expected, new NodePath(path).normalize().toString());
    }
    
    @Test
    public void testNormalize1() {
        assertNormalized("foo/bar/baz", "foo/bar/baz");
    }

    @Test
    public void testNormalize2() {
        assertNormalized("foo/baz", "foo/bar/../baz");
    }

    @Test
    public void testNormalize3() {
        assertNormalized("baz", "foo/bar/../../baz");
    }

    @Test
    public void testNormalize4() {
        assertNormalized("/baz", "/foo/bar/../../baz");
    }

    @Test
    public void testNormalize5() {
        assertNormalized("../baz", "../foo/bar/../../baz");
    }

    @Test
    public void testNormalize6() {
        assertNormalized("/../baz", "/../foo/bar/../../baz");
    }

    @Test
    public void testNormalize7() {
        assertNormalized("../../../../../../baz", "../../../../../../foo/bar/../../baz");
    }

    @Test
    public void testNormalize8() {
        assertNormalized("/../../../../../../baz", "/../../../../../../foo/bar/../../baz");
    }

    @Test
    public void testNormalize9() {
        assertNormalized("foo/bar", "foo/////bar");
    }

    @Test
    public void testNormalize10() {
        assertNormalized(".", ".");
    }

    @Test
    public void testNormalize11() {
        assertNormalized("./foo", "./foo");
    }

    @Test
    public void testNormalize12() {
        assertNormalized("./foo/", "./foo/././.");
    }

    @Test
    public void testNormalize13() {
        assertNormalized("./foo/", "./././././foo/././.");
    }

    private void assertSplit(final String path, final String... expected) {
        String[] elements = new NodePath(path).split();
        assertEquals(expected.length, elements.length);

        int i=0;
        for (String expect : expected) {
            assertEquals(expect, elements[i++]);
        }
    }

    @Test
    public void testSplit1() {
        assertSplit("/", "/");
    }

    @Test
    public void testSplit2() {
        assertSplit("foo", "foo");
    }

    @Test
    public void testSplit3() {
        assertSplit("foo/bar", "foo", "bar");
    }

    @Test
    public void testSplit4() {
        assertSplit("/foo/bar", "/", "foo", "bar");
    }
}