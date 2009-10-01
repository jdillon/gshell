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

package org.apache.gshell.bootstrap;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Bootstrap configuration.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 *
 * @since 2.0
 */
public class Configuration
{
    public static final String SHELL_HOME_DETECTED = "shell.home.detected";

    public static final String SHELL_HOME = "shell.home";

    public static final String SHELL_ETC = "shell.etc";

    public static final String SHELL_LIB = "shell.lib";

    public static final String SHELL_PROGRAM = "shell.program";

    public static final String SHELL_VERSION = "shell.version";

    public static final String SHELL_MAIN = "shell.main";

    public static final String DEFAULT_PROPERTIES = "default.properties";

    public static final String BOOTSTRAP_PROPERTIES = "bootstrap.properties";

    public static final String BOOTSTRAP_JAR = "bootstrap.jar";
    
    public static final int SUCCESS_EXIT_CODE = 0;

    public static final int FAILURE_EXIT_CODE = 100;

    private static final Pattern PATTERN = Pattern.compile("\\$\\{([^}]+)\\}");

    private Properties props;

    private Properties loadProperties() throws Exception {
        Properties props = new Properties();

        URL defaults = getClass().getResource(DEFAULT_PROPERTIES);
        if (defaults == null) {
            // Should never happen
            throw new Error("Missing resource: " + DEFAULT_PROPERTIES); // TODO: i18n
        }
        mergeProperties(props, defaults);

        URL bootstrap = getClass().getClassLoader().getResource(BOOTSTRAP_PROPERTIES);
        if (bootstrap == null) {
            // Happens when assembly forgets to install the bootstrap properties file
            throw new Error("Missing resource: " + BOOTSTRAP_PROPERTIES); // TODO: i18n
        }
        mergeProperties(props, bootstrap);

        return props;
    }

    private void mergeProperties(final Properties props, final URL url) throws IOException {
        Log.debug("Merging properties from: ", url);

        InputStream input = url.openStream();
        try {
            props.load(input);
        }
        finally {
            input.close();
        }
    }

    /**
     * Detect the home directory, which is expected to be <tt>../../</tt> from the location of the jar containing this class.
     */
    private File detectHomeDir() throws Exception {
        String path = getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
        path = URLDecoder.decode(path, "UTF-8");
        return new File(path).getParentFile().getParentFile().getCanonicalFile();
    }

    public void configure() throws Exception {
        Log.debug("Configuring");

        props = loadProperties();
        props.setProperty(SHELL_HOME_DETECTED, detectHomeDir().getPath());

        if (Log.DEBUG) {
            Log.debug("Properties:");
            for (Map.Entry entry : props.entrySet()) {
                Log.debug("    ",  entry.getKey(), "=", entry.getValue());
            }
        }

        requireProperty(SHELL_MAIN);
        requireProperty(SHELL_PROGRAM);
        requireProperty(SHELL_VERSION);

        // Export some configuration
        setSystemProperty(SHELL_HOME, getPropertyAsFile(SHELL_HOME).getPath());
        setSystemProperty(SHELL_PROGRAM, getProperty(SHELL_PROGRAM));
        setSystemProperty(SHELL_VERSION, getProperty(SHELL_VERSION));
    }

    private void setSystemProperty(final String name, final String value) {
        assert name != null;
        assert value != null;
        Log.debug(name, ": ", value);
        System.setProperty(name, value);
    }

    private void ensureConfigured() {
        if (props == null) {
            throw new IllegalStateException("Not configured");
        }
    }

    /**
     * Get the value of a property, checking system properties, then configuration properties and evaluating the result.
     */
    private String getProperty(final String name) {
        assert name != null;
        ensureConfigured();
        return evaluate(System.getProperty(name, props.getProperty(name)));
    }

    private String evaluate(String input) {
        if (input != null) {
            Matcher matcher = PATTERN.matcher(input);

            while (matcher.find()) {
                Object rep = props.get(matcher.group(1));
                if (rep != null) {
                    input = input.replace(matcher.group(0), rep.toString());
                    matcher.reset(input);
                }
            }
        }

        return input;
    }

    private File getPropertyAsFile(final String name) {
        String path = getProperty(name);
        if (path != null) {
            return new File(path).getAbsoluteFile();
        }
        return null;
    }

    private void requireProperty(final String name) {
        String value = getProperty(name);
        if (value == null) {
            throw new Error("Missing required property: " + name); // TODO: i18n
        }
    }

    public List<URL> getClassPath() throws Exception {
        ensureConfigured();
        List<URL> classPath = new ArrayList<URL>();

        classPath.add(getPropertyAsFile(SHELL_ETC).toURI().toURL());

        File dir = getPropertyAsFile(SHELL_LIB);

        Log.debug("Finding jars under: ", dir);

        File[] files = dir.listFiles(new FileFilter() {
            public boolean accept(final File file) {
                assert file != null;
                String name = file.getName().toLowerCase();
                return !name.equals(BOOTSTRAP_JAR) && file.isFile() && name.endsWith(".jar");

            }
        });

        if (files == null) {
            throw new Error("No jars found under: " + dir); // TODO: i18n
        }

        for (File file : files) {
            classPath.add(file.toURI().toURL());
        }

        return classPath;
    }

    public String getMainClass() {
        return getProperty(SHELL_MAIN);
    }

    public int getSuccessExitCode() {
        return SUCCESS_EXIT_CODE;
    }

    public int getFailureExitCode() {
        return FAILURE_EXIT_CODE;
    }
}