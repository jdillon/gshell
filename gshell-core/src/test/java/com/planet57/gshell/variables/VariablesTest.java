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
package com.planet57.gshell.variables;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Unit tests for the {@link Variables} class.
 * 
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class VariablesTest
{
    private Variables vars;

    private Variables parent;

    @Before
    public void setUp() {
        vars = new VariablesImpl();
        parent = new VariablesImpl();
    }

    @Test
    public void testSet() throws Exception {
        String name = "a";
        Object value = new Object();

        assertFalse(vars.contains(name));
        vars.set(name, value);
        assertTrue(vars.contains(name));

        Object obj = vars.get(name);
        assertEquals(value, obj);

        String str = vars.names().next();
        assertEquals(name, str);
    }

    @Test
    public void testSetAsImmutable() throws Exception {
        String name = "a";
        Object value = new Object();

        assertTrue(vars.isMutable(name));
        vars.set(name, value, false);
        assertFalse(vars.isMutable(name));

        try {
            vars.set(name, value);
            fail("Set an immutable variable");
        }
        catch (Variables.ImmutableVariableException expected) {
            // ignore
        }
    }

    @Test
    public void testSetAsImmutableInParent() throws Exception {
        Variables vars = new VariablesImpl(parent);
        String name = "a";
        Object value = new Object();

        parent.set(name, value, false);
        assertFalse(parent.isMutable(name));
        assertFalse(vars.isMutable(name));

        try {
            vars.set(name, value);
            fail("Set an immutable variable");
        }
        catch (Variables.ImmutableVariableException expected) {
            // ignore
        }
    }

    @Test
    public void testSetParentFromChild() throws Exception {
        Variables vars = new VariablesImpl(parent);
        String name = "a";
        Object value = new Object();

        // Make sure we can add to parent's scope from child
        vars.parent().set(name, value);
        assertEquals(value, parent.get(name));

        // Make sure the iter sees it
        assertTrue(vars.names().hasNext());
    }

    @Test
    public void testGet() throws Exception {
        String name = "a";
        Object value = new Object();

        Object obj1 = vars.get(name);
        assertNull(obj1);

        vars.set(name, value);
        Object obj2 = vars.get(name);
        assertSame(value, obj2);
    }

    @Test
    public void testGetUsingDefault() throws Exception {
        String name = "a";
        Object value = new Object();

        Object obj1 = vars.get(name);
        assertNull(obj1);

        Object obj2 = vars.get(name, value);
        assertSame(value, obj2);
    }

    @Test
    public void testGetCloaked() throws Exception {
        Variables vars = new VariablesImpl(parent);
        String name = "a";
        Object value = new Object();

        parent.set(name, value);
        Object obj1 = vars.get(name);
        assertEquals(value, obj1);

        Object value2 = new Object();
        vars.set(name, value2);

        Object obj2 = vars.get(name);
        assertSame(value2, obj2);
        assertNotSame(value, obj2);
    }

    @Test
    public void testUnsetAsImmutable() throws Exception {
        String name = "a";
        Object value = new Object();

        assertTrue(vars.isMutable(name));
        vars.set(name, value, false);
        assertFalse(vars.isMutable(name));

        try {
            vars.unset(name);
            fail("Unset an immutable variable");
        }
        catch (Variables.ImmutableVariableException expected) {
            // ignore
        }
    }

    @Test
    public void testUnsetAsImmutableInParent() throws Exception {
        Variables vars = new VariablesImpl(parent);
        String name = "a";
        Object value = new Object();

        parent.set(name, value, false);
        assertFalse(parent.isMutable(name));
        assertFalse(vars.isMutable(name));

        try {
            vars.unset(name);
            fail("Unset an immutable variable");
        }
        catch (Variables.ImmutableVariableException expected) {
            // ignore
        }
    }

    @Test
    public void testCloaking() throws Exception {
        Variables vars = new VariablesImpl(parent);
        String name = "a";
        Object value = new Object();

        parent.set(name, value);
        assertFalse(parent.isCloaked(name));
        assertFalse(vars.isCloaked(name));

        vars.set(name, new Object());
        assertTrue(vars.isCloaked(name));
    }

    @Test
    public void testParent() throws Exception {
        assertNull(parent.parent());

        Variables vars = new VariablesImpl(parent);
        assertNotNull(vars.parent());

        assertEquals(parent, vars.parent());
    }

    @Test
    public void testNames() throws Exception {
        Iterator<String> iter = vars.names();
        assertNotNull(iter);
        assertFalse(iter.hasNext());
    }

    @Test
    public void testNamesImmutable() throws Exception {
        vars.set("a", "b");

        Iterator<String> iter = vars.names();
        iter.next();

        try {
            iter.remove();
        }
        catch (UnsupportedOperationException expected) {
            // ignore
        }
    }
}
