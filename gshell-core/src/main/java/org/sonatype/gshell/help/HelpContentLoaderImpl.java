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

package org.sonatype.gshell.help;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.gshell.io.Closer;
import org.sonatype.gshell.util.PrintBuffer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * {@link HelpContentLoader} component.
 * 
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
public class HelpContentLoaderImpl
    implements HelpContentLoader
{
    private static final Logger log = LoggerFactory.getLogger(HelpContentLoaderImpl.class);

    public String load(final String name, final ClassLoader loader) throws MissingContentException, IOException {
        return load(name, Locale.getDefault(), loader);
    }

    private String load(final String name, final Locale locale, final ClassLoader loader) throws MissingContentException,
        IOException
    {
        log.debug("Loading help content for {} ({})", name, locale);

        URL resource = findResource(name, locale, loader);
        if (resource == null) {
            throw new MissingContentException(name);
        }

        log.debug("Using resource: {}", resource);

        BufferedReader input = new BufferedReader(new InputStreamReader(resource.openStream()));
        PrintBuffer buff;
        try {
            buff = new PrintBuffer();
            String line;

            while ((line = input.readLine()) != null) {
                // Ignore lines starting with #, these are comments
                if (!line.startsWith("#")) {
                    buff.println(line);
                }
            }
        }
        finally {
            Closer.close(input);
        }

        return buff.toString();
    }

    private URL findResource(final String name, final Locale locale, final ClassLoader loader) {
        assert name != null;
        assert locale != null;
        assert loader != null;
        
        URL resource = null;

        ResourceBundle.Control control = ResourceBundle.Control.getControl(ResourceBundle.Control.FORMAT_DEFAULT);

        for (Locale candidate : control.getCandidateLocales(name, locale)) {
            String bundle = control.toBundleName(name, candidate);

            try {
                resource = loader.getResource(control.toResourceName(bundle, "help"));
            }
            catch (MissingResourceException e) {
                // ignore
            }
        }
        return resource;
    }
}