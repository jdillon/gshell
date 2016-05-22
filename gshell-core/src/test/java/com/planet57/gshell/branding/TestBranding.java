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
package com.planet57.gshell.branding;

import java.io.File;

/**
 * Test {@link Branding}.
 * 
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class TestBranding
    extends BrandingSupport
{
    private File baseDir;

    public TestBranding(final File baseDir) {
        assert baseDir != null;
        this.baseDir = baseDir;
    }

    @Override
    public String getProgramName() {
        return "testsh";
    }

    @Override
    public String getScriptExtension() {
        return "tsh";
    }

    @Override
    public String getVersion() {
        return "1.0-TEST";
    }

    @Override
    public File getShellHomeDir() {
        return baseDir;
    }

    @Override
    public File getUserHomeDir() {
        return baseDir;
    }

    @Override
    public File getUserContextDir() {
        return resolveFile(new File(getUserHomeDir(), getProgramName()));
    }
}