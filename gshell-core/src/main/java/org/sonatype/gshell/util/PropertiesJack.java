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

package org.sonatype.gshell.util;

import org.slf4j.Logger;
import org.sonatype.gossip.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;
import java.util.Set;

/**
 * Hijacks the systems properties on a per-thread basis.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.4
 */
public class PropertiesJack
{
    private static final Logger log = Log.getLogger(PropertiesJack.class);

    /**
     * Contains a {@link PropertiesRegistration} for the current thread if its registered, else null.
     */
    private static final InheritableThreadLocal<PropertiesRegistration> registrations = new InheritableThreadLocal<PropertiesRegistration>();

    private static final Properties SYSTEM = System.getProperties();

    /**
     * The previously installed System properties, initialized when installing.
     */
    private static Properties previous;

    /**
     * Flag to indicate if the hijacker is installed or not.
     */
    private static boolean installed;

    /**
     * Check if the hijacker has been installed.
     */
    public static synchronized boolean isInstalled() {
        return installed;
    }

    private static synchronized void ensureInstalled() {
        if (!isInstalled()) {
            throw new IllegalStateException("Not installed");
        }
    }

    /**
     * Install the hijacker.
     */
    public static synchronized void install() {
        if (installed) {
            throw new IllegalStateException("Already installed");
        }

        // Capture the current properties
        previous = System.getProperties();

        System.setProperties(new DelegateProperties());
        installed = true;

        log.debug("Installed");
    }

    public static synchronized void install(final Properties props) {
        install();
        register(props);
    }

    public static synchronized void maybeInstall() {
        if (!isInstalled()) {
            install();
        }
    }

    public static synchronized void maybeInstall(final Properties props) {
        if (!isInstalled()) {
            install(props);
        }
        else {
            register(props);
        }
    }

    /**
     * Uninstall the hijacker.
     */
    public static synchronized void uninstall() {
        ensureInstalled();

        System.setProperties(previous);

        previous = null;
        installed = false;

        log.debug("Uninstalled");
    }

    /**
     * Get the current properties registration.
     */
    private static synchronized PropertiesRegistration registration(final boolean required) {
        if (required) {
            ensureRegistered();
        }

        return registrations.get();
    }

    /**
     * Check if there are properties registered for the current thread.
     */
    public static synchronized boolean isRegistered() {
        return registration(false) != null;
    }

    private static synchronized void ensureRegistered() {
        ensureInstalled();

        if (!isRegistered()) {
            throw new IllegalStateException(MessageFormat.format("Not registered: {0}", Thread.currentThread()));
        }
    }

    /**
     * Register properties for the current thread.
     */
    public static synchronized void register(final Properties props) {
        ensureInstalled();

        log.trace("Registering: {} -> {}, {}", Thread.currentThread(), props);

        PropertiesRegistration prev = registration(false);
        PropertiesRegistration next = new PropertiesRegistration(props, prev);

        registrations.set(next);
    }

    /**
     * De-register properties for the current thread, and restore the previous if any.
     */
    public static synchronized void deregister() {
        PropertiesRegistration cur = registration(true);

        registrations.set(cur.previous);

        log.trace("De-registered: {}, using properties: {}", Thread.currentThread(), cur.previous == null ? "null" : cur.previous.props);
    }

    /**
     * Property registration information.
     */
    private static class PropertiesRegistration
    {
        public final Properties props;

        public final PropertiesRegistration previous;

        public PropertiesRegistration(final Properties props, final PropertiesRegistration previous) {
            assert props != null;
            this.props = props;
            this.previous = previous;
        }
    }

    /**
     * Returns the currently registered properties.
     */
    public static synchronized Properties current() {
        PropertiesRegistration reg = registration(false);
        if (reg == null) {
            return previous;
        }
        return reg.props;
    }

    //
    // System properties Restoration.
    //

    /**
     * Restores the System properties and resets the hijacker state to un-installed.
     */
    public static synchronized void restore(final Properties props) {
        assert props != null;

        System.setProperties(props);

        previous = null;
        installed = false;
    }

    /**
     * Restores the original System properties and resets the hijacker state to un-installed.
     */
    public static synchronized void restore() {
        restore(SYSTEM);
    }

    //
    // DelegateProperties
    //

    private static class DelegateProperties
        extends Properties
    {
        private Properties get() {
            return registrations.get().props;
        }

        @Override
        public Object setProperty(String key, String value) {
            return get().setProperty(key, value);
        }

        @Override
        public void load(Reader reader) throws IOException {
            get().load(reader);
        }

        @Override
        public void load(InputStream is) throws IOException {
            get().load(is);
        }

        @Override
        public void save(OutputStream out, String comments) {
            get().save(out, comments);
        }

        @Override
        public void store(Writer writer, String comments) throws IOException {
            get().store(writer, comments);
        }

        @Override
        public void store(OutputStream out, String comments) throws IOException {
            get().store(out, comments);
        }

        @Override
        public void loadFromXML(InputStream in) throws IOException {
            get().loadFromXML(in);
        }

        @Override
        public void storeToXML(OutputStream os, String comment) throws IOException {
            get().storeToXML(os, comment);
        }

        @Override
        public void storeToXML(OutputStream os, String comment, String encoding) throws IOException {
            get().storeToXML(os, comment, encoding);
        }

        @Override
        public String getProperty(String key) {
            return get().getProperty(key);
        }

        @Override
        public String getProperty(String key, String defaultValue) {
            return get().getProperty(key, defaultValue);
        }

        @Override
        public Enumeration<?> propertyNames() {
            return get().propertyNames();
        }

        @Override
        public Set<String> stringPropertyNames() {
            return get().stringPropertyNames();
        }

        @Override
        public void list(PrintStream out) {
            get().list(out);
        }

        @Override
        public void list(PrintWriter out) {
            get().list(out);
        }
    }
}