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

package org.sonatype.gshell.branding;

import org.codehaus.plexus.util.IOUtil;
import org.sonatype.gshell.util.io.Closer;
import org.sonatype.gshell.util.PrintBuffer;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Support for {@link License) implementations.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
public class LicenseSupport
    implements License
{
    private final String name;

    private final URL url;

    public LicenseSupport(final String name, final URL url) {
        this.name = name;
        this.url = url;
    }

    public LicenseSupport(final String name, final String url) {
        this.name = name;
        try {
            this.url = new URL(url);
        }
        catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getName() {
        return name;
    }

    public URL getUrl() {
        return url;
    }

    public String getContent() throws IOException {
        URL url = getUrl();
        
        if (url == null) {
            return null;
        }

        PrintBuffer buff = new PrintBuffer();
        InputStream input = url.openStream();
        try {
            IOUtil.copy(input, buff);
        }
        finally {
            Closer.close(input);
        }
        return buff.toString();
    }
}