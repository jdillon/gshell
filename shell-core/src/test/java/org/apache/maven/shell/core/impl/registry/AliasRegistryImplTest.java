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

package org.apache.maven.shell.core.impl.registry;

import org.apache.maven.shell.registry.AliasRegistry;
import org.apache.maven.shell.registry.NoSuchAliasException;
import org.apache.maven.shell.testsupport.PlexusTestSupport;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import java.util.Collection;

/**
 * Tests for the {@link AliasRegistryImpl}.
 *
 * @version $Rev$ $Date$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class AliasRegistryImplTest
{
    private PlexusTestSupport plexus;

    private AliasRegistryImpl registry;

    @Before
    public void setUp() throws Exception {
        plexus = new PlexusTestSupport(this);
        registry = (AliasRegistryImpl)plexus.lookup(AliasRegistry.class);
    }

    @After
    public void tearDown() {
        registry = null;
        plexus.destroy();
        plexus = null;
    }

    @Test
    public void testRegisterAliasInvalid() throws Exception {
        try {
            registry.registerAlias(null, null);
            fail();
        }
        catch (AssertionError e) {
            // ignore
        }

        try {
            registry.registerAlias("foo", null);
            fail();
        }
        catch (AssertionError e) {
            // ignore
        }

        try {
            registry.registerAlias(null, "foo");
            fail();
        }
        catch (AssertionError e) {
            // ignore
        }
    }

    @Test
    public void testRemoveAliasInvalid() throws Exception {
        try {
            registry.removeAlias(null);
            fail();
        }
        catch (AssertionError e) {
            // ignore
        }
    }

    @Test
    public void testGetAliasInvalid() throws Exception {
        try {
            registry.getAlias(null);
            fail();
        }
        catch (AssertionError e) {
            // ignore
        }
    }

    @Test
    public void testContainsAliasInvalid() throws Exception {
        try {
            registry.containsAlias(null);
            fail();
        }
        catch (AssertionError e) {
            // ignore
        }
    }

    @Test
    public void testRegisterAlias() throws Exception {
        assertFalse(registry.containsAlias("foo"));

        registry.registerAlias("foo", "bar");

        assertTrue(registry.containsAlias("foo"));

        String alias = registry.getAlias("foo");
        assertEquals("bar", alias);
    }

    @Test
    public void testReRegisterAlias() throws Exception {
        testRegisterAlias();

        assertTrue(registry.containsAlias("foo"));

        registry.registerAlias("foo", "baz");

        assertTrue(registry.containsAlias("foo"));

        String alias = registry.getAlias("foo");
        assertEquals("baz", alias);
    }

    @Test
    public void testRemoveAlias() throws Exception {
        assertFalse(registry.containsAlias("foo"));

        try {
            registry.removeAlias("foo");
        }
        catch (NoSuchAliasException e) {
            // expected
        }

        testRegisterAlias();
        registry.removeAlias("foo");
        assertFalse(registry.containsAlias("foo"));
    }

    @Test
    public void testGetAliasNotRegistered() throws Exception {
        assertFalse(registry.containsAlias("foo"));

        try {
            registry.getAlias("foo");
        }
        catch (NoSuchAliasException e) {
            // expected
        }
    }

    @Test
    public void testGetAliasNames() throws Exception {
        Collection<String> names;

        names = registry.getAliasNames();
        assertNotNull(names);
        assertTrue(names.isEmpty());

        registry.registerAlias("foo", "bar");
        names = registry.getAliasNames();
        assertNotNull(names);
        assertEquals(1, names.size());
        assertEquals("foo", names.iterator().next());
    }
}