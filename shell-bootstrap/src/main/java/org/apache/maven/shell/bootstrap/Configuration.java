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
 * @version $Rev$ $Date$
 */
public interface Configuration
{
    String GSHELL_HOME_DETECTED = "gshell.home.detected";

    String GSHELL_HOME = "gshell.home";

    String GSHELL_ETC = "gshell.etc";

    String GSHELL_LIB = "gshell.lib";

    String GSHELL_PROGRAM = "gshell.program";

    String GSHELL_VERSION = "gshell.version";

    String GSHELL_PROPERTIES = "gshell.properties";

    void configure() throws Exception;

    List<URL> getClassPath() throws Exception;

    String getMainClass();
    
    int getSuccessExitCode();

    int getFailureExitCode();
}