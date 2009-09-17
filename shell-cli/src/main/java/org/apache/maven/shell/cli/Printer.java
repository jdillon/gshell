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

package org.apache.maven.shell.cli;

import org.apache.maven.shell.cli.handler.Handler;
import org.apache.maven.shell.i18n.MessageSource;
import org.apache.maven.shell.i18n.ResourceBundleMessageSource;
import org.apache.maven.shell.i18n.ResourceNotFoundException;
import org.apache.maven.shell.i18n.AggregateMessageSource;

import org.codehaus.plexus.util.StringUtils;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Helper to print formatted help and usage text.
 *
 * @version $Rev$ $Date$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class Printer
{
    private final Processor processor;

    private MessageSource messages = new ResourceBundleMessageSource(Printer.class);

    private int terminalWidth = jline.Terminal.getTerminal().getTerminalWidth();

    private String prefix = "  ";

    private String separator = "    ";

    public Printer(final Processor processor) {
        assert processor != null;
        this.processor = processor;
    }

    public void setMessageSource(final MessageSource messages) {
        assert messages != null;
        this.messages = new AggregateMessageSource(this.messages, messages);
    }

    public int getTerminalWidth() {
        return terminalWidth;
    }

    public void setTerminalWidth(final int terminalWidth) {
        assert terminalWidth > 0;
        this.terminalWidth = terminalWidth;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(final String prefix) {
        assert prefix != null;
        this.prefix = prefix;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(final String separator) {
        assert separator != null;
        this.separator = separator;
    }

    /**
     * Get the help text for the given descriptor, using any configured messages for i18n support.
     */
    private String getHelpText(final Descriptor descriptor) {
        assert descriptor != null;

        String message = descriptor.getDescription();

        // If we have i18n messages for the command, then try to resolve the message further using the message as the code
        if (messages != null) {
            String code = message;

            // If there is no code, then generate one
            if (code == null) {
                if (descriptor instanceof ArgumentDescriptor) {
                    code = String.format("argument.%s", descriptor.getId());
                }
                else {
                    code = String.format("option.%s", descriptor.getId());
                }
            }

            // Resolve the text in the message source
            try {
                message = messages.getMessage(code);
            }
            catch (ResourceNotFoundException e) {
                // Just use the code as the message
            }
        }

        return message;
    }

    private String getToken(final Handler handler) {
        assert handler != null;

        Descriptor descriptor = handler.descriptor;
        String token = descriptor.getToken();

        // If we have i18n messages for the command, then try to resolve the token further
        if (messages != null) {
            String code = token;

            // If there is no coded, then generate one
            if (code == null) {
                if (descriptor instanceof ArgumentDescriptor) {
                    code = String.format("argument.%s.token", descriptor.getId());
                }
                else {
                    code = String.format("option.%s.token", descriptor.getId());
                }
            }

            // Resolve the text in the message source
            try {
                token = messages.getMessage(code);
            }
            catch (ResourceNotFoundException e) {
                // Just use the code as the message
            }
        }

        if (token == null) {
            token = handler.getDefaultToken();
        }

        return token;
    }

    private String getNameAndToken(final Handler handler) {
        assert handler != null;

        String str = (handler.descriptor instanceof ArgumentDescriptor) ? "" : handler.descriptor.toString();
        String token = getToken(handler);

        if (token != null) {
            if (str.length() > 0) {
                str += " ";
            }
            str += token;
        }
        
        return str;
    }

    private int getPrefixLen(final Handler handler) {
        assert handler != null;

        String helpText = getHelpText(handler.descriptor);
        if (helpText == null) {
            return 0;
        }

        return getNameAndToken(handler).length();
    }

    public void printUsage(final Writer writer, final String name) {
        assert writer != null;

        PrintWriter out = new PrintWriter(writer);

        List<Handler> argumentHandlers = new ArrayList<Handler>();
        argumentHandlers.addAll(processor.getArgumentHandlers());

        List<Handler> optionHandlers = new ArrayList<Handler>();
        optionHandlers.addAll(processor.getOptionHandlers());

        // For display purposes, we like the argument handlers in argument order,
        // but the option handlers in alphabetical order
        Collections.sort(optionHandlers, new Comparator<Handler>() {
            public int compare(Handler a, Handler b) {
                return a.descriptor.toString().compareTo(b.descriptor.toString());
            }
        });

        if (name != null) {
            String syntax = messages.format("syntax", name);
            if (!optionHandlers.isEmpty()) {
                syntax = messages.format("syntax.hasOptions", syntax);
            }
            if (!argumentHandlers.isEmpty()) {
                syntax = messages.format("syntax.hasArguments", syntax);
            }
            out.println(syntax);
            out.println();
        }

        // Compute the maximum length of the syntax column
        int len = 0;

        for (Handler handler : optionHandlers) {
            int curLen = getPrefixLen(handler);
            len = Math.max(len, curLen);
        }

        for (Handler handler : argumentHandlers) {
            int curLen = getPrefixLen(handler);
            len = Math.max(len, curLen);
        }

        // And then render the handler usage
        if (!argumentHandlers.isEmpty()) {
            out.println(messages.getMessage("arguments.header"));

            for (Handler handler : argumentHandlers) {
                printHandler(out, handler, len);
            }

            out.println();
        }

        if (!optionHandlers.isEmpty()) {
            out.println(messages.getMessage("options.header"));
            
            for (Handler handler : optionHandlers) {
                printHandler(out, handler, len);
            }

            out.println();
        }
        
        out.flush();
    }

    public void printUsage(final Writer writer) {
        printUsage(writer, null);
    }

    private void printHandler(final PrintWriter out, final Handler handler, final int len) {
        assert out != null;
        assert handler != null;

        int prefixSeperatorWidth = prefix.length() + separator.length();
        int descriptionWidth = terminalWidth - len - prefixSeperatorWidth;

        // Only render if there is help-text, else its hidden
        String desc = getHelpText(handler.descriptor);
        if (desc == null) {
            return;
        }

        // Render the prefix and syntax
        String nameAndToken = getNameAndToken(handler);
        out.print(prefix);
        out.print(nameAndToken);

        // Render the separator
        for (int i = nameAndToken.length(); i < len; ++i) {
            out.print(' ');
        }
        out.print(separator);

        String[] words = desc.split("\\b");
        StringBuilder buff = new StringBuilder();

        for (String word : words) {
            if (word.length() + buff.length() > descriptionWidth) {
                // spit out the current buffer and indent
                out.println(buff);
                indent(out, len + prefixSeperatorWidth);
                buff.setLength(0);
            }
            buff.append(word);
        }

        out.println(buff);
    }

    private void indent(final PrintWriter out, int i) {
        assert out != null;

        for (; i>0; i--) {
            out.print(' ');
        }
    }
}
