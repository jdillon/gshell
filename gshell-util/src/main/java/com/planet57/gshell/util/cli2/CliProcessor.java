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

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.planet57.gossip.Log;
import com.planet57.gshell.util.IllegalAnnotationError;
import com.planet57.gshell.util.cli2.handler.Handler;
import com.planet57.gshell.util.cli2.handler.Handlers;
import com.planet57.gshell.util.i18n.MessageSource;
import com.planet57.gshell.util.setter.SetterFactory;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.slf4j.Logger;
import com.planet57.gshell.util.i18n.I18N;
import com.planet57.gshell.util.i18n.MessageBundle;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Processes an object for cli annotations.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.3
 */
public class CliProcessor
{
  private interface Messages
    extends MessageBundle
  {
    @DefaultMessage("Option '%s' takes an operand: %s")
    String MISSING_OPERAND(String option, String operand);

    @DefaultMessage("'%s' is not a valid option")
    String UNDEFINED_OPTION(String option);

    @DefaultMessage("No argument is allowed: %s")
    String NO_ARGUMENT_ALLOWED(String value);

    @DefaultMessage("Option '%s' is required")
    String REQUIRED_OPTION_MISSING(List<?> option);

    @DefaultMessage("Argument '%s' is required")
    String REQUIRED_ARGUMENT_MISSING(String argument);

    @DefaultMessage("Too many arguments: %s")
    String TOO_MANY_ARGUMENTS(String argument);
  }

  private static final Messages messages = I18N.create(Messages.class);

  private static final Logger log = Log.getLogger(CliProcessor.class);

  private final List<OptionDescriptor> optionDescriptors = new ArrayList<>();

  private final List<ArgumentDescriptor> argumentDescriptors = new ArrayList<>();

  private boolean stopAtNonOption;

  private MessageSource userMessages;

  // TODO: change to Flavor.DEFAULT; but have to sort out control/handling of required-options which are delayed for Option.override handling
  private CliParser.Flavor flavor = CliParser.Flavor.POSIX;

  public CliProcessor() {
    // empty
  }

  public boolean isStopAtNonOption() {
    return stopAtNonOption;
  }

  public void setStopAtNonOption(boolean flag) {
    this.stopAtNonOption = flag;
  }

  public MessageSource getMessages() {
    return userMessages;
  }

  public void setMessages(final MessageSource messages) {
    this.userMessages = messages;
  }

  public CliParser.Flavor getFlavor() {
    return flavor;
  }

  public void setFlavor(final CliParser.Flavor flavor) {
    this.flavor = checkNotNull(flavor);
  }

  public List<OptionDescriptor> getOptionDescriptors() {
    return optionDescriptors;
  }

  public List<ArgumentDescriptor> getArgumentDescriptors() {
    return argumentDescriptors;
  }

  public void addBean(final Object bean) {
    discoverDescriptors(bean);

    if (bean instanceof CliProcessorAware) {
      ((CliProcessorAware) bean).setProcessor(this);
    }
  }

  //
  // Discovery
  //

  private void discoverDescriptors(final Object bean) {
    assert bean != null;

    // Recursively process all the methods/fields (@Inherited won't work here)
    for (Class<?> type = bean.getClass(); type != null; type = type.getSuperclass()) {
      for (Method method : type.getDeclaredMethods()) {
        discoverDescriptor(bean, method);
      }
      for (Field field : type.getDeclaredFields()) {
        discoverDescriptor(bean, field);
      }
    }

    // Sanity check the argument indexes
    for (int i = 0; i < argumentDescriptors.size(); i++) {
      if (argumentDescriptors.get(i) == null) {
        throw new IllegalAnnotationError(String.format("No @Argument for index: %d", i));
      }
    }
  }

  private void discoverDescriptor(final Object bean, final AnnotatedElement element) {
    assert bean != null;
    assert element != null;

    Option opt = element.getAnnotation(Option.class);
    Argument arg = element.getAnnotation(Argument.class);

    if (opt != null && arg != null) {
      throw new IllegalAnnotationError(
          String.format("Element can only implement @Option or @Argument, not both: %s", element));
    }

    if (opt != null) {
      log.trace("Discovered @Option for: {} -> {}", element, opt);

      OptionDescriptor desc = new OptionDescriptor(opt, SetterFactory.create(element, bean));

      // Make sure we have unique names
      for (OptionDescriptor tmp : optionDescriptors) {
        if (desc.getName() != null && desc.getName().equals(tmp.getName())) {
          throw new IllegalAnnotationError(
              String.format("Duplicate @Option name: %s, on: %s", desc.getName(), element));
        }
        if (desc.getLongName() != null && desc.getLongName().equals(tmp.getLongName())) {
          throw new IllegalAnnotationError(
              String.format("Duplicate @Option longName: %s, on: %s", desc.getLongName(), element));
        }
      }

      optionDescriptors.add(desc);
    }
    else if (arg != null) {
      log.trace("Discovered @Argument for: {} -> {}", element, arg);

      ArgumentDescriptor desc = new ArgumentDescriptor(arg, SetterFactory.create(element, bean));
      int index = arg.index();

      // Make sure the argument will fit in the list
      while (index >= argumentDescriptors.size()) {
        argumentDescriptors.add(null);
      }

      if (argumentDescriptors.get(index) != null) {
        throw new IllegalAnnotationError(String.format("Duplicate @Argument index: %s, on: %s", index, element));
      }

      argumentDescriptors.set(index, desc);
    }
  }

  //
  // Processing
  //

  public void process(final List<?> args) throws Exception {
    checkNotNull(args);
    process(toStringArray(args));
  }

  private static String[] toStringArray(final List<?> args) {
    String[] strings = new String[args.size()];
    for (int i = 0; i < args.size(); i++) {
      strings[i] = String.valueOf(args.get(i));
    }
    return strings;
  }

  public void process(final String... args) throws Exception {
    checkNotNull(args);
    if (log.isTraceEnabled()) {
      log.trace("Processing: {}", Arrays.asList(args));
    }

    CliParser parser = flavor.create();
    log.trace("Parser: {}", parser);

    CommandLine cl;
    try {
      cl = parser.parse(createOptions(), args, stopAtNonOption);
    }
    catch (UnrecognizedOptionException e) {
      throw new ProcessingException(messages.UNDEFINED_OPTION(e.getOption()));
    }
    catch (MissingArgumentException e) {
      OptionDescriptor desc = ((Opt) e.getOption()).getDescriptor();
      throw new ProcessingException(messages.MISSING_OPERAND(desc.getSyntax(), desc.getToken()));
    }
    catch (ParseException e) {
      throw new ProcessingException(e);
    }

    Set<CliDescriptor> present = new HashSet<>();
    boolean override = false;

    if (log.isTraceEnabled()) {
      log.trace("Parsed options: {}", Arrays.asList(cl.getOptions()));
    }

    for (Object tmp : cl.getOptions()) {
      Opt opt = (Opt) tmp;
      log.trace("Processing option: {}", opt);

      OptionDescriptor desc = opt.getDescriptor();
      present.add(desc);

      // Track the override, this is used to handle when --help present, but a required arg/opt is missing
      if (!override) {
        override = desc.getOverride();
      }

      Handler handler = Handlers.create(desc);
      String[] values = opt.getValues();

      if (values == null || values.length == 0) {
        // Set the value
        handler.handle(opt.getValue());
      }
      else {
        // Set the values
        for (String value : values) {
          handler.handle(value);
        }
      }
    }

    log.trace("Remaining arguments: {}", cl.getArgList());

    int i = 0;
    for (final String arg : cl.getArgs()) {
      log.trace("Processing argument: {}", arg);

      // Check if we allow an argument or we have overflowed
      if (i >= argumentDescriptors.size()) {
        throw new ProcessingException(argumentDescriptors.size() == 0 ?
            messages.NO_ARGUMENT_ALLOWED(arg) : messages.TOO_MANY_ARGUMENTS(arg));
      }

      ArgumentDescriptor desc = argumentDescriptors.get(i);
      present.add(desc);

      // For single-valued args, increment the argument index, else let the multivalued handler consume it
      if (!desc.isMultiValued()) {
        i++;
      }

      // Set the value
      Handler handler = Handlers.create(desc);
      handler.handle(arg);
    }

    // Check for any required arguments which were not present
    if (!override) {
      try {
        parser.ensureRequiredOptionsPresent();
      }
      catch (MissingOptionException e) {
        throw new ProcessingException(messages.REQUIRED_OPTION_MISSING(e.getMissingOptions()));
      }

      for (ArgumentDescriptor arg : argumentDescriptors) {
        if (arg.isRequired() && !present.contains(arg)) {
          throw new ProcessingException(messages.REQUIRED_ARGUMENT_MISSING(arg.getToken()));
        }
      }
    }

    // TODO: Handle setting defaults
  }

  private Options createOptions() {
    Options opts = new Options();

    for (OptionDescriptor opt : optionDescriptors) {
      opts.addOption(new Opt(opt));
    }

    return opts;
  }

  private static class Opt
      extends org.apache.commons.cli.Option
  {
    private final OptionDescriptor desc;

    private Opt(final OptionDescriptor opt) throws IllegalArgumentException {
      super(opt.getName(), opt.getDescription());
      this.desc = opt;

      setLongOpt(opt.getLongName());
      setArgName(opt.getToken());
      setRequired(opt.isRequired());
      setValueSeparator(opt.getSeparator());
      setArgs(opt.getArgs());
      setOptionalArg(opt.isArgumentOptional());
    }

    public OptionDescriptor getDescriptor() {
      return desc;
    }
  }
}
