/**
 * Copyright (c) 2009-2011 the original author or authors.
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
package org.sonatype.gshell.command.descriptor;

import org.junit.Test;
import org.sonatype.gshell.command.descriptor.io.xpp3.CommandsXpp3Writer;

import java.io.StringWriter;

/**
 * Unit tests for the {@link CommandsDescriptor} class.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class CommandsDescriptorTest
{
    @Test
    public void testWriteXml() throws Exception {
        CommandsDescriptor commands = new CommandsDescriptor();
        commands.setVersion("1");

        CommandSetDescriptor commandSet = new CommandSetDescriptor();
        commandSet.setId("foo");
        commandSet.setEnabled(true);
        commands.getCommandSets().add(commandSet);

        CommandDescriptor command = new CommandDescriptor();
        command.setEnabled(true);
        command.setAction("blah");
        command.setName("ick");
        commandSet.getCommands().add(command);

        CommandsXpp3Writer writer = new CommandsXpp3Writer();
        StringWriter buff = new StringWriter();
        writer.write(buff, commands);

        System.out.println("XML:\n" + buff);
    }
}