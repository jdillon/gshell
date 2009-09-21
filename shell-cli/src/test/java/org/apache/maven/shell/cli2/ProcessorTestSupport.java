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

package org.apache.maven.shell.cli2;

import org.junit.After;
import org.junit.Before;
import org.apache.maven.shell.cli.ProcessingException;

/**
 * Support for {@link Processor} tests.
 *
 * @version $Rev$ $Date$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public abstract class ProcessorTestSupport
{
    private Processor processor;

    @Before
    public void setUp() {
        processor = new Processor(createBean());
    }

    @After
    public void tearDown() {
        processor = null;
    }

    protected void process(final String... args) throws ProcessingException {
        processor.process(args);
    }

    protected abstract Object createBean();
}