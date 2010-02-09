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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sonatype.gshell.command.CommandAction;
import org.sonatype.gshell.command.DummyAction;
import org.sonatype.gshell.command.GroupAction;

import java.util.Collection;
import java.util.Iterator;

import static junit.framework.Assert.*;
import static org.sonatype.gshell.command.resolver.Node.CURRENT;
import static org.sonatype.gshell.command.resolver.Node.ROOT;

/**
 * Tests for {@link Node}.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class NodeTest
{
    private Node root;

    @Before
    public void setUp() {
        root = new Node(ROOT, new GroupAction(ROOT));
    }

    @After
    public void tearDown() {
        root = null;
    }

    @Test
    public void testAdd1() {
        root.add("test", new DummyAction());
        assertEquals(1, root.children().size());
    }

    @Test
    public void testAdd2() {
        root.add("/test", new DummyAction());
        assertEquals(1, root.children().size());
    }

    @Test
    public void testAdd3() {
        root.add("/foo", new DummyAction());
        root.add("/bar", new DummyAction());
        assertEquals(2, root.children().size());
    }

    @Test
    public void testFind1() {
        CommandAction action = new DummyAction();
        root.add("group/test", action);
        assertEquals(1, root.children().size());

        Node node;

        node = root.children().iterator().next();
        assertTrue(node.isGroup());
        assertFalse(node.isLeaf());
        assertEquals("group", node.getName());
        assertEquals(1, node.children().size());

        node = node.children().iterator().next();
        assertFalse(node.isGroup());
        assertTrue(node.isLeaf());
        assertEquals("test", node.getName());
        assertEquals(action, node.getAction());
    }

    @Test
    public void testFind2() {
        assertEquals(root, root.find(CURRENT));
    }

    @Test
    public void testFind3() {
        assertEquals(root, root.find(ROOT));
    }

    @Test
    public void testFind4() {
        assertEquals(root, root.find("./"));
    }

    @Test
    public void testFind5() {
        root.add("/a1", new DummyAction());
        root.add("/a2", new DummyAction());
        root.add("/b1", new DummyAction());

        System.out.println(".....");
        assertNull(root.find("a1/"));
        System.out.println(".....");
    }

    @Test
    public void testPath1() {
        root.add("group/test", new DummyAction());

        Node node;

        node = root.children().iterator().next();
        assertEquals("/group", node.getPath());

        node = node.children().iterator().next();
        assertEquals("/group/test", node.getPath());
    }

    @Test
    public void testPath2() {
        root.add("group/sub/sub/test", new DummyAction());

        Node node;

        node = root.children().iterator().next();
        assertEquals("/group", node.getPath());

        node = node.children().iterator().next();
        assertEquals("/group/sub", node.getPath());

        node = node.children().iterator().next();
        assertEquals("/group/sub/sub", node.getPath());

        node = node.children().iterator().next();
        assertEquals("/group/sub/sub/test", node.getPath());
    }

    @Test
    public void testChildren1() {
        root.add("/a1", new DummyAction());
        root.add("/a2", new DummyAction());
        root.add("/b1", new DummyAction());

        Collection<Node> children = root.children();
        assertEquals(3, children.size());
    }

    @Test
    public void testChildren2() {
        root.add("/a1", new DummyAction());
        root.add("/a2", new DummyAction());
        root.add("/b1", new DummyAction());

        Collection<Node> children = root.children("a");
        assertEquals(2, children.size());

        Iterator<Node> iter = children.iterator();
        assertEquals("a1", iter.next().getName());
        assertEquals("a2", iter.next().getName());
    }
}