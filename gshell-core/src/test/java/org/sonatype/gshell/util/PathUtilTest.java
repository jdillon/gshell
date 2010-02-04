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

package org.sonatype.gshell.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for the {@link PathUtil}.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class PathUtilTest
{
    private void chewPath(final String path, final String expected) {
        assertEquals(expected, PathUtil.normalize(path));
    }

    @Test
    public void testNormalize1() {
        chewPath("foo/bar/baz", "foo/bar/baz");
    }

    @Test
    public void testNormalize2() {
        chewPath("foo/bar/../baz", "foo/baz");
    }

    @Test
    public void testNormalize3() {
        chewPath("foo/bar/../../baz", "baz");
    }

    @Test
    public void testNormalize4() {
        chewPath("/foo/bar/../../baz", "/baz");
    }

    @Test
    public void testNormalize5() {
        chewPath("../foo/bar/../../baz", "baz");
    }

    @Test
    public void testNormalize6() {
        chewPath("/../foo/bar/../../baz", "/baz");
    }

    @Test
    public void testNormalize7() {
        chewPath("../../../../../../foo/bar/../../baz", "baz");
    }

    @Test
    public void testNormalize8() {
        chewPath("/../../../../../../foo/bar/../../baz", "/baz");
    }
}