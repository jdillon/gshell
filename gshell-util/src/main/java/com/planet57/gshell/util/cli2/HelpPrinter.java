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
package com.planet57.gshell.util.cli2;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.planet57.gshell.util.i18n.AggregateMessageSource;
import com.planet57.gshell.util.i18n.MessageSource;
import com.planet57.gshell.util.i18n.ResourceBundleMessageSource;
import org.jline.terminal.Terminal;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Helper to print formatted help and usage text.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.3
 */
public class HelpPrinter
{
  private final CliProcessor processor;

  private AggregateMessageSource messages = new AggregateMessageSource(new ResourceBundleMessageSource(getClass()));

  private int terminalWidth;

  private String prefix = "  ";

  private String separator = "    ";

  public HelpPrinter(final CliProcessor processor, @Nullable final Terminal terminal) {
    this.processor = checkNotNull(processor);

    // Add messages from the processor
    MessageSource messages = processor.getMessages();
    if (messages != null) {
      addMessages(messages);
    }

    terminalWidth = terminal != null ? terminal.getWidth() : 80;
  }

  public void addMessages(final MessageSource messages) {
    this.messages.getSources().add(messages);
  }

  public int getTerminalWidth() {
    return terminalWidth;
  }

  public void setTerminalWidth(final int terminalWidth) {
    checkArgument(terminalWidth > 0);
    this.terminalWidth = terminalWidth;
  }

  public String getPrefix() {
    return prefix;
  }

  public void setPrefix(final String prefix) {
    this.prefix = checkNotNull(prefix);
  }

  public String getSeparator() {
    return separator;
  }

  public void setSeparator(final String separator) {
    this.separator = checkNotNull(separator);
  }

  public void printUsage(final PrintWriter out, @Nullable final String name) {
    checkNotNull(out);

    List<ArgumentDescriptor> arguments = new ArrayList<>();
    arguments.addAll(processor.getArgumentDescriptors());

    List<OptionDescriptor> options = new ArrayList<>();
    options.addAll(processor.getOptionDescriptors());

    if (name != null) {
      String syntax = messages.format("syntax", name);
      if (!options.isEmpty()) {
        syntax = messages.format("syntax.hasOptions", syntax);
      }
      if (!arguments.isEmpty()) {
        syntax = messages.format("syntax.hasArguments", syntax);
      }
      out.println(syntax);
      out.println();
    }

    // Compute the maximum length of the syntax column
    int len = 0;

    for (ArgumentDescriptor arg : arguments) {
      len = Math.max(len, arg.renderSyntax(messages).length());
    }

    for (OptionDescriptor opt : options) {
      len = Math.max(len, opt.renderSyntax(messages).length());
    }

    // And then render the handler usage
    if (!arguments.isEmpty()) {
      out.println(messages.getMessage("arguments.header"));

      printArguments(out, arguments, len);

      out.println();
    }

    if (!options.isEmpty()) {
      out.println(messages.getMessage("options.header"));

      printOptions(out, options, len);

      out.println();
    }

    out.flush();
  }

  /**
   * @since 2.4
   */
  private void printArguments(final PrintWriter out, final List<ArgumentDescriptor> arguments, final int len) {
    for (ArgumentDescriptor arg : arguments) {
      printDescriptor(out, arg, len);
    }
  }

  /**
   * @since 2.4
   */
  public void printArguments(final PrintWriter out, final List<ArgumentDescriptor> arguments) {
    int len = 0;
    for (ArgumentDescriptor arg : arguments) {
      len = Math.max(len, arg.renderSyntax(messages).length());
    }

    printArguments(out, arguments, len);
  }

  /**
   * @since 2.4
   */
  private void printOptions(final PrintWriter out, final List<OptionDescriptor> options, final int len) {
    for (OptionDescriptor opt : options) {
      printDescriptor(out, opt, len);
    }
  }

  /**
   * @since 2.4
   */
  public void printOptions(final PrintWriter out, final List<OptionDescriptor> options) {
    int len = 0;
    for (OptionDescriptor opt : options) {
      len = Math.max(len, opt.renderSyntax(messages).length());
    }

    printOptions(out, options, len);
  }

  public void printUsage(final PrintWriter writer) {
    printUsage(writer, null);
  }

  private void printDescriptor(final PrintWriter out, final CliDescriptor desc, final int len) {
    assert out != null;
    assert desc != null;

    int prefixSeparatorWidth = prefix.length() + separator.length();
    int descriptionWidth = terminalWidth - len - prefixSeparatorWidth;

    String description = desc.renderHelpText(messages);

    // Render the prefix and syntax
    String syntax = desc.renderSyntax(messages);
    out.print(prefix);
    out.print(syntax);

    // Render the separator
    for (int i = syntax.length(); i < len; ++i) {
      out.print(' ');
    }
    out.print(separator);

    StringBuilder buff = new StringBuilder();

    if (description != null) {
      String[] words = description.split("\\b");

      for (String word : words) {
        if (word.length() + buff.length() > descriptionWidth) {
          // spit out the current buffer and indent
          out.println(buff);
          indent(out, len + prefixSeparatorWidth);
          buff.setLength(0);
        }
        buff.append(word);
      }
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
