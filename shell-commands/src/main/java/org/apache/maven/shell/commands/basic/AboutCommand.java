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

package org.apache.maven.shell.commands.basic;

import org.apache.maven.cli.MavenCli;
import org.apache.maven.shell.command.Command;
import org.apache.maven.shell.command.CommandContext;
import org.apache.maven.shell.command.CommandSupport;
import org.apache.maven.shell.io.Closer;
import org.apache.maven.shell.io.IO;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.util.Os;
import org.codehaus.plexus.util.StringUtils;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

/**
 * Display information about the current shell.
 *
 * @version $Rev$ $Date$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
@Component(role=Command.class, hint="about", instantiationStrategy="per-lookup")
public class AboutCommand
    extends CommandSupport
{
    public Object execute(final CommandContext context) throws Exception {
        assert context != null;
        IO io = context.getIo();

        io.out.println("@|bold,red Apache Maven| @|bold Shell|");
        io.out.println(StringUtils.repeat("-", io.getTerminal().getTerminalWidth() - 1));

        Properties properties = getBuildProperties();
        String timestamp = reduce(properties.getProperty("timestamp"));
        String version = reduce(properties.getProperty("version"));
        String rev = reduce(properties.getProperty("buildNumber"));

        String msg = "Apache Maven ";
        msg += (version != null ? version : "<version unknown>");
        if (rev != null || timestamp != null) {
            msg += " (";
            msg += (rev != null ? "r" + rev : "");
            if (timestamp != null) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
                String ts = format.format(new Date(Long.valueOf(timestamp)));
                msg += (rev != null ? "; " : "") + ts;
            }
            msg += ")";
        }

        io.info(msg);
        io.info("Java version: {}", System.getProperty("java.version", "<unknown java version>"));
        io.info("Java home: {}", System.getProperty("java.home", "<unknown java home>"));
        io.info("Default locale: {}, platform encoding: {}", Locale.getDefault(), System.getProperty("file.encoding", "<unknown encoding>"));
        io.info("OS name: '{}' version: '{}' arch: '{}' Family: '{}'", Os.OS_NAME, Os.OS_VERSION, Os.OS_ARCH, Os.OS_FAMILY);

        return Result.SUCCESS;
    }

    private static String reduce(final String s) {
        return (s != null ? (s.startsWith("${") && s.endsWith("}") ? null : s) : null);
    }

    private static Properties getBuildProperties() throws Exception {
        Properties properties = new Properties();
        InputStream input = null;

        try {
            input = MavenCli.class.getClassLoader().getResourceAsStream("org/apache/maven/messages/build.properties");
            if (input != null) {
                properties.load(input);
            }
        }
        finally {
            Closer.close(input);
        }

        return properties;
    }
}