/*
 * Copyright (C) 2009 the original author(s).
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

package org.sonatype.gshell.util.converter.basic;

import org.sonatype.gshell.util.converter.ConverterSupport;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * ???
 *
 * @since 2.0
 */
public class UrlConverter
    extends ConverterSupport
{
    public UrlConverter() {
        super(URL.class);
    }

    protected Object toObjectImpl(final String text) throws Exception {
        try {
            // try to create directly from the text property.
            URL url = new URL(text);
            // this parsed correctly, but if this is a file object,
            // we need to make sure this gets converted into the proper
            // absolute directory form.
            if (url.getProtocol().equals("file")) {
                // ok, this is a file URL, so get the file string portion,
                // convert that to a file object, then go through the URI()/URL()
                // conversion sequence to get a fully valid URL().
                return new File(url.getFile()).toURI().toURL();
            }

            return url;
        }
        catch (MalformedURLException e) {
            // this is a format error, but it could have been specified as a local
            // file name. so try to create a file object and make a URL from that.
        }

        // The file class has direct support for returning as a URL, but the Javadoc
        // for File.toURL() recommends converting the File object to a URI first
        // so that untranslatable characters get handled correctly.
        return new File(text).toURI().toURL();
    }
}
