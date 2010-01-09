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


package org.sonatype.gshell.util.cli2;

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests options.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class RequiredOptionsTest
    extends CliProcessorTestSupport
{
    private static class Simple
    {
        @Option(name = "h", required=true)
        boolean help;

        @Option(name = "v")
        boolean verbose;
    }

    private Simple bean;

    @Override
    protected Object createBean() {
        return bean = new Simple();
    }

    @Test
    public void testPresent() throws Exception {
        clp.process("-h");
        assertTrue(bean.help);
    }

    @Test
    public void testMissing() throws Exception {
        try {
            clp.process("-v");
            fail();
        }
        catch (Exception e) {
             // expected
        }
    }
}