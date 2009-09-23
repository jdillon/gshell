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

package org.apache.maven.shell.bootstrap;

import java.net.URL;
import java.util.List;

/**
 * Bootstrap configuration.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 *
 * @since 1.0
 */
public interface Configuration
{
    String SHELL_HOME_DETECTED = "shell.home.detected";

    String SHELL_HOME = "shell.home";

    String SHELL_ETC = "shell.etc";

    String SHELL_LIB = "shell.lib";

    String SHELL_PROGRAM = "shell.program";

    String SHELL_VERSION = "shell.version";

    String[] VARIABLES = {
        SHELL_HOME_DETECTED,
        SHELL_HOME,
        SHELL_ETC,
        SHELL_LIB,
        SHELL_PROGRAM,
        SHELL_VERSION
    };

    void configure() throws Exception;

    List<URL> getClassPath() throws Exception;

    String getMainClass();
    
    int getSuccessExitCode();

    int getFailureExitCode();
}