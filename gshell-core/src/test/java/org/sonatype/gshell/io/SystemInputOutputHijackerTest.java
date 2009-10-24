/*
 * Copyright (C) 2009 the original author(s).
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

package org.sonatype.gshell.io;

import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

/**
 * Tests for the {@link SystemInputOutputHijacker} class.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class SystemInputOutputHijackerTest
{
    private ByteArrayOutputStream buff;

    private InputStream in;

    private PrintStream out;

    @Before
    public void setUp() throws Exception {
        in = System.in;

        buff = new ByteArrayOutputStream();
        out = new PrintStream(buff);

        if (SystemInputOutputHijacker.isInstalled()) {
            SystemInputOutputHijacker.uninstall();
        }
    }

    @After
    public void tearDown() throws Exception {
        buff = null;
        out = null;

        if (SystemInputOutputHijacker.isInstalled()) {
            SystemInputOutputHijacker.uninstall();
        }
    }

    private void installOut() throws Exception {
        assertFalse(SystemInputOutputHijacker.isRegistered());

        SystemInputOutputHijacker.install(in, out);

        assertTrue(SystemInputOutputHijacker.isInstalled());
        assertTrue(SystemInputOutputHijacker.isRegistered());
    }

    private void deregisterAndUninstall() throws Exception {
        SystemInputOutputHijacker.deregister();

        assertFalse(SystemInputOutputHijacker.isRegistered());

        SystemInputOutputHijacker.uninstall();

        assertFalse(SystemInputOutputHijacker.isInstalled());
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

        SystemInputOutputHijacker.install();
        assertTrue(SystemInputOutputHijacker.isInstalled());

        assertFalse(SystemInputOutputHijacker.isRegistered());
        SystemInputOutputHijacker.register(in, out);
        assertTrue(SystemInputOutputHijacker.isRegistered());

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

        SystemInputOutputHijacker.install(in, out, err);

        assertTrue(SystemInputOutputHijacker.isInstalled());
        assertTrue(SystemInputOutputHijacker.isRegistered());

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

        Runnable task = new Runnable() {
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

            SystemInputOutputHijacker.register(in, childOut);

            try {
                System.out.print("child");
            }
            finally {
                SystemInputOutputHijacker.deregister();
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