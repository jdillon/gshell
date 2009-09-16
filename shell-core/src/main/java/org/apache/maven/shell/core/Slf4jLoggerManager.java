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

package org.apache.maven.shell.core;

import org.codehaus.plexus.logging.AbstractLogger;
import org.codehaus.plexus.logging.BaseLoggerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adapts the Plexus logging system to SLF4J.
 *
 * @version $Rev$ $Date$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class Slf4jLoggerManager
    extends BaseLoggerManager
{
    protected org.codehaus.plexus.logging.Logger createLogger(final String key) {
        return new LoggerImpl(getThreshold(), LoggerFactory.getLogger(key));
    }

    public org.codehaus.plexus.logging.Logger getLoggerForComponent(final String role, final String roleHint) {
        return createLogger(toMapKey(role, roleHint));
    }

    public void returnComponentLogger(final String role, final String roleHint) {
        // Ignore
    }

    /**
     * Adapts the Plexus {@link org.codehaus.plexus.logging.Logger} interface to SLF4J
     */
    public static class LoggerImpl
        extends AbstractLogger
    {
        private final Logger log;

        public LoggerImpl(final int threshold, final Logger logger) {
            super(threshold, logger.getName());

            this.log = logger;
        }

        public void debug(final String message, final Throwable throwable) {
            log.debug( message, throwable );
        }

        public void error(final String message, final Throwable throwable) {
            log.error(message, throwable);
        }

        public void fatalError(final String message, final Throwable throwable) {
            log.error(message, throwable);
        }

        public org.codehaus.plexus.logging.Logger getChildLogger(final String name) {
            String childName = log.getName() + "." + name;

            return new LoggerImpl(getThreshold(), LoggerFactory.getLogger(childName));
        }

        public void info(String message, final Throwable throwable) {
            log.info(message, throwable);
        }

        public void warn(String message, final Throwable throwable) {
            log.warn(message, throwable);
        }
    }
}