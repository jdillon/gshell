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

package org.sonatype.gshell.util.cli;

import jline.TerminalFactory;
import org.sonatype.gshell.util.cli.handler.Handler;
import org.sonatype.gshell.util.i18n.AggregateMessageSource;
import org.sonatype.gshell.util.i18n.MessageSource;
import org.sonatype.gshell.util.i18n.ResourceBundleMessageSource;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Helper to print formatted help and usage text.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class CommandLinePrinter
{
    private final CommandLineProcessor processor;

    private AggregateMessageSource messages = new AggregateMessageSource(new ResourceBundleMessageSource(getClass()));

    private int terminalWidth = TerminalFactory.get().getWidth();

    private String prefix = "  ";

    private String separator = "    ";

    public CommandLinePrinter(final CommandLineProcessor processor) {
        assert processor != null;
        this.processor = processor;

        // Add messages from the processor
        MessageSource messages = processor.getMessages();
        if (messages != null) {
            addMessages(messages);
        }
    }

    public void addMessages(final MessageSource messages) {
        this.messages.getSources().add(messages);
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

    private String getNameAndToken(final Handler handler) {
        assert handler != null;

        String str = handler.isArgument() ? "" : handler.getDescriptor().toString();
        String token = handler.getToken(messages);

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

        if (handler.getHelpText(messages) == null) {
            return 0;
        }

        return getNameAndToken(handler).length();
    }

    public void printUsage(final PrintWriter out, final String name) {
        assert out != null;

        List<Handler> argumentHandlers = new ArrayList<Handler>();
        argumentHandlers.addAll(processor.getArgumentHandlers());

        List<Handler> optionHandlers = new ArrayList<Handler>();
        optionHandlers.addAll(processor.getOptionHandlers());

        // For display purposes, we like the argument handlers in argument order,
        // but the option handlers in alphabetical order
        Collections.sort(optionHandlers, new Comparator<Handler>()
        {
            public int compare(final Handler a, final Handler b) {
                return a.getDescriptor().toString().compareTo(b.getDescriptor().toString());
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

    public void printUsage(final PrintWriter writer) {
        printUsage(writer, null);
    }

    private void printHandler(final PrintWriter out, final Handler handler, final int len) {
        assert out != null;
        assert handler != null;

        int prefixSeperatorWidth = prefix.length() + separator.length();
        int descriptionWidth = terminalWidth - len - prefixSeperatorWidth;

        // Only render if there is help-text, else its hidden
        String desc = handler.getHelpText(messages);
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

        for (; i > 0; i--) {
            out.print(' ');
        }
    }
}
