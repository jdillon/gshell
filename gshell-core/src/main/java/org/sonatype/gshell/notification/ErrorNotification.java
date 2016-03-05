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
package org.sonatype.gshell.notification;

/**
 * Thrown to indicate an error notification state.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public final class ErrorNotification
    extends Notification
{
    ///CLOVER:OFF

    private static final long serialVersionUID = 1;

    public ErrorNotification(final String msg) {
        super(msg);
    }

    public ErrorNotification(final String msg, final Throwable cause) {
        super(msg, cause);
    }

    public ErrorNotification(final Throwable cause) {
        super(cause);
    }

    public ErrorNotification() {
        super();
    }
}
