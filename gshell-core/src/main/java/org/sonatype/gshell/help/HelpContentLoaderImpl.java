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
package org.sonatype.gshell.help;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.gshell.util.io.Closer;
import org.sonatype.gshell.util.PrintBuffer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;

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

    private String load(final String name, final Locale locale, final ClassLoader loader) throws
        MissingContentException,
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
        assert loader != null;

        URL resource = null;

        for (Locale candidate : getCandidateLocales(locale)) {
            String bundle = name;
            
            if (candidate != null) {
                bundle = toBundleName(name, candidate);
            }

            try {
                resource = loader.getResource(toResourceName(bundle, "help"));
            }
            catch (MissingResourceException e) {
                // ignore
            }
        }
        return resource;
    }

    private List<Locale> getCandidateLocales(final Locale locale) {
        assert locale != null;

        String language = locale.getLanguage();
        String country = locale.getCountry();
        String variant = locale.getVariant();

        List<Locale> locales = new ArrayList<Locale>(4);

        if (variant.length() > 0) {
            locales.add(locale);
        }
        if (country.length() > 0) {
            locales.add((locales.size() == 0) ? locale : new Locale(language, country, ""));
        }
        if (language.length() > 0) {
            locales.add((locales.size() == 0) ? locale : new Locale(language, "", ""));
        }
        locales.add(null);

        return locales;
    }

    private String toBundleName(final String baseName, final Locale locale) {
        assert baseName != null;
        assert locale != null;

        String language = locale.getLanguage();
        String country = locale.getCountry();
        String variant = locale.getVariant();

        if ("".equals(language) && "".equals(country) && "".equals(variant)) {
            return baseName;
        }

        StringBuilder buff = new StringBuilder(baseName);
        buff.append('_');
        if (!"".equals(variant)) {
            buff.append(language).append('_').append(country).append('_').append(variant);
        }
        else if (!"".equals(country)) {
            buff.append(language).append('_').append(country);
        }
        else {
            buff.append(language);
        }
        
        return buff.toString();

    }

    private String toResourceName(final String bundleName, final String suffix) {
        assert bundleName != null;
        assert suffix != null;

        StringBuilder buff = new StringBuilder(bundleName.length() + 1 + suffix.length());
        buff.append(bundleName.replace('.', '/')).append('.').append(suffix);
        return buff.toString();
    }
}