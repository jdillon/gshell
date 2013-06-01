/*
 * Copyright (c) 2009-2013 the original author or authors.
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
package org.sonatype.gshell.command.support;

import org.sonatype.gshell.command.IO;
import org.sonatype.gshell.util.io.StreamSet;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Test {@link IO}.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class TestIO
    extends IO
{
    private ByteArrayOutputStream output;

    private ByteArrayOutputStream error;

    public TestIO() {
        this(new ByteArrayOutputStream(), new ByteArrayOutputStream());
    }

    private TestIO(ByteArrayOutputStream output, ByteArrayOutputStream error) {
        super(new StreamSet(System.in, new PrintStream(output), new PrintStream(error)), true);

        this.output = output;
        this.error = error;
    }

    public ByteArrayOutputStream getOutput() {
        return output;
    }

    public String getOutputString() {
        return new String(getOutput().toByteArray());
    }

    public ByteArrayOutputStream getError() {
        return error;
    }

    public String getErrorString() {
        return new String(getError().toByteArray());
    }
}