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
import org.sonatype.gshell.command.support.CommandActionSupport;
import org.sonatype.gshell.command.CommandContext;

import static org.sonatype.gshell.command.resolver.Node.ROOT;
import static junit.framework.Assert.*;

/**
 * Tests for {@link Node}.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class NodeTest
{
    private Node root;

    @Before
    public void setUp() throws Exception {
        root = new Node(ROOT, new GroupAction(ROOT), null);
    }

    @After
    public void tearDown() {
        root = null;
    }

    @Test
    public void testFind1() {
        root.add("test", new MockAction());
        assertEquals(1, root.getChildren().size());
    }

    @Test
    public void testFind2() {
        CommandAction action = new MockAction();
        root.add("group/test", action);
        assertEquals(1, root.getChildren().size());

        Node node;

        node = root.getChildren().iterator().next();
        assertTrue(node.isGroup());
        assertFalse(node.isLeaf());
        assertEquals("group", node.getName());
        assertEquals(1, node.getChildren().size());

        node = node.getChildren().iterator().next();
        assertFalse(node.isGroup());
        assertTrue(node.isLeaf());
        assertEquals("test", node.getName());
        assertEquals(action, node.getAction());
    }

    @Test
    public void testFind3() {
        root.add("/test", new MockAction());
        assertEquals(1, root.getChildren().size());
    }

    private static class MockAction
        extends CommandActionSupport
    {
        public Object execute(CommandContext context) throws Exception {
            return null;
        }
    }
}