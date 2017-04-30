/*
 * Copyright (c) 2009-present the original author or authors.
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
package com.planet57.gshell.command.resolver;

import java.util.Collection;
import java.util.Iterator;

import com.planet57.gshell.command.CommandAction;
import com.planet57.gshell.command.ChangeGroupAction;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sonatype.goodies.testsupport.TestSupport;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

/**
 * Tests for {@link Node}.
 */
public class NodeTest
  extends TestSupport
{
  private Node underTest;

  @Before
  public void setUp() {
    underTest = new Node(Node.ROOT, new ChangeGroupAction(Node.ROOT));
  }

  @After
  public void tearDown() {
    underTest = null;
  }

  @Test
  public void testAdd1() {
    underTest.add("test", new DummyAction());
    assertEquals(1, underTest.children().size());
  }

  @Test
  public void testAdd2() {
    underTest.add("/test", new DummyAction());
    assertEquals(1, underTest.children().size());
  }

  @Test
  public void testAdd3() {
    underTest.add("/foo", new DummyAction());
    underTest.add("/bar", new DummyAction());
    assertEquals(2, underTest.children().size());
  }

  @Test
  public void testFind1() {
    CommandAction action = new DummyAction();
    underTest.add("group/test", action);
    assertEquals(1, underTest.children().size());

    Node node;

    node = underTest.children().iterator().next();
    assertTrue(node.isGroup());
    assertFalse(node.isLeaf());
    assertEquals("group", node.getName());
    assertEquals(1, node.children().size());

    node = node.children().iterator().next();
    assertFalse(node.isGroup());
    assertTrue(node.isLeaf());
    assertEquals("test", node.getName());
    Assert.assertEquals(action, node.getAction());
  }

  @Test
  public void testFind2() {
    assertEquals(underTest, underTest.find(Node.CURRENT));
  }

  @Test
  public void testFind3() {
    assertEquals(underTest, underTest.find(Node.ROOT));
  }

  @Test
  public void testFind4() {
    assertEquals(underTest, underTest.find("./"));
  }

  @Test
  public void testFind5() {
    underTest.add("/a1", new DummyAction());
    underTest.add("/a2", new DummyAction());
    underTest.add("/b1", new DummyAction());

    System.out.println(".....");
    assertNull(underTest.find("a1/"));
    System.out.println(".....");
  }

  @Test
  public void testPath1() {
    underTest.add("group/test", new DummyAction());

    Node node;

    node = underTest.children().iterator().next();
    assertEquals("/group", node.getPath());

    node = node.children().iterator().next();
    assertEquals("/group/test", node.getPath());
  }

  @Test
  public void testPath2() {
    underTest.add("group/sub/sub/test", new DummyAction());

    Node node;

    node = underTest.children().iterator().next();
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
    underTest.add("/a1", new DummyAction());
    underTest.add("/a2", new DummyAction());
    underTest.add("/b1", new DummyAction());

    Collection<Node> children = underTest.children();
    assertEquals(3, children.size());
  }

  @Test
  public void testChildren2() {
    underTest.add("/a1", new DummyAction());
    underTest.add("/a2", new DummyAction());
    underTest.add("/b1", new DummyAction());

    Collection<Node> children = underTest.children("a");
    assertEquals(2, children.size());

    Iterator<Node> iter = children.iterator();
    assertEquals("a1", iter.next().getName());
    assertEquals("a2", iter.next().getName());
  }
}
