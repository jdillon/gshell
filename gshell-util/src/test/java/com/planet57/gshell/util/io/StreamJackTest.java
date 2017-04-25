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
package com.planet57.gshell.util.io;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sonatype.goodies.testsupport.TestSupport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests for the {@link StreamJack} class.
 */
public class StreamJackTest
  extends TestSupport
{
  private ByteArrayOutputStream buff;

  private InputStream in;

  private PrintStream out;

  @Before
  public void setUp() throws Exception {
    in = System.in;

    buff = new ByteArrayOutputStream();
    out = new PrintStream(buff);

    if (StreamJack.isInstalled()) {
      StreamJack.uninstall();
    }
  }

  @After
  public void tearDown() throws Exception {
    buff = null;
    out = null;

    if (StreamJack.isInstalled()) {
      StreamJack.uninstall();
    }
  }

  private void installOut() throws Exception {
    assertFalse(StreamJack.isRegistered());

    StreamJack.install(in, out);

    assertTrue(StreamJack.isInstalled());
    assertTrue(StreamJack.isRegistered());
  }

  private void deregisterAndUninstall() throws Exception {
    StreamJack.deregister();

    assertFalse(StreamJack.isRegistered());

    StreamJack.uninstall();

    assertFalse(StreamJack.isInstalled());
  }

  @Test
  public void testInstallWithStream() throws Exception {
    System.out.println("before");

    installOut();

    try {
      System.out.print("hijacked");
    }
    finally {
      deregisterAndUninstall();
    }

    System.out.println("after");

    String msg = new String(buff.toByteArray());

    assertEquals("hijacked", msg);
  }

  @Test
  public void testInstallRegisterWithStream() throws Exception {
    System.out.println("before");

    StreamJack.install();
    assertTrue(StreamJack.isInstalled());

    assertFalse(StreamJack.isRegistered());
    StreamJack.register(in, out);
    assertTrue(StreamJack.isRegistered());

    try {
      System.out.print("hijacked");
    }
    finally {
      deregisterAndUninstall();
    }

    System.out.println("after");

    String msg = new String(buff.toByteArray());

    assertEquals("hijacked", msg);
  }

  @Test
  public void testDualStreams() throws Exception {
    ByteArrayOutputStream errBuff = new ByteArrayOutputStream();
    PrintStream err = new PrintStream(errBuff);

    System.out.println("before");
    System.err.println("BEFORE");

    StreamJack.install(in, out, err);

    assertTrue(StreamJack.isInstalled());
    assertTrue(StreamJack.isRegistered());

    try {
      System.out.print("hijacked");
      System.err.print("HIJACKED");
    }
    finally {
      deregisterAndUninstall();
    }

    System.out.println("after");
    System.err.println("AFTER");

    assertEquals("hijacked", new String(buff.toByteArray()));
    assertEquals("HIJACKED", new String(errBuff.toByteArray()));
  }

  @Test
  public void testChildThreads() throws Exception {
    System.out.println("before");

    installOut();

    Runnable task = new Runnable()
    {
      public void run() {
        System.out.print("hijacked");
      }
    };

    try {
      System.out.print("<");

      Thread t = new Thread(task);
      t.start();
      t.join();

      System.out.print(">");
    }
    finally {
      deregisterAndUninstall();
    }

    System.out.println("after");

    String msg = new String(buff.toByteArray());

    assertEquals("<hijacked>", msg);
  }

  @Test
  public void testNestedRegistration() throws Exception {
    System.out.println("before");

    installOut();

    try {
      System.out.print("hijacked");

      ByteArrayOutputStream childBuff = new ByteArrayOutputStream();
      PrintStream childOut = new PrintStream(childBuff);

      System.out.print("!");

      StreamJack.register(in, childOut);

      try {
        System.out.print("child");
      }
      finally {
        StreamJack.deregister();
      }

      System.out.print("!");

      assertEquals("child", new String(childBuff.toByteArray()));
    }
    finally {
      deregisterAndUninstall();
    }

    System.out.println("after");

    String msg = new String(buff.toByteArray());

    assertEquals("hijacked!!", msg);
  }
}
