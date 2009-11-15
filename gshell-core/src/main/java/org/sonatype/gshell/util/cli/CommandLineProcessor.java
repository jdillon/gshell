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

import org.sonatype.gshell.util.IllegalAnnotationError;
import org.sonatype.gshell.util.NameValue;
import org.sonatype.gshell.util.cli.handler.Handler;
import org.sonatype.gshell.util.cli.handler.Handlers;
import org.sonatype.gshell.util.i18n.MessageSource;
import org.sonatype.gshell.util.i18n.ResourceBundleMessageSource;
import org.sonatype.gshell.util.setter.Setter;
import org.sonatype.gshell.util.setter.SetterFactory;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Processes an object for command-line configuration annotations.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class CommandLineProcessor
{
    public static final String DASH_DASH = "--";

    public static final String DASH = "-";

    private final List<Handler> optionHandlers = new ArrayList<Handler>();

    private final List<Handler> argumentHandlers = new ArrayList<Handler>();

    private boolean stopAtNonOption = false;

    private MessageSource messages;

    public CommandLineProcessor() {
    }

    public CommandLineProcessor(final Object bean) {
        addBean(bean);
    }

    public void setMessages(final MessageSource messages) {
        this.messages = messages;
    }

    public MessageSource getMessages() {
        return messages;
    }

    public List<Handler> getOptionHandlers() {
        return Collections.unmodifiableList(optionHandlers);
    }

    public List<Handler> getArgumentHandlers() {
        return Collections.unmodifiableList(argumentHandlers);
    }

    public boolean getStopAtNonOption() {
        return stopAtNonOption;
    }

    public void setStopAtNonOption(final boolean flag) {
        stopAtNonOption = flag;
    }

    public void addBean(final Object bean) {
        discoverDescriptors(bean);

        if (bean instanceof CommandLineProcessorAware) {
            ((CommandLineProcessorAware) bean).setProcessor(this);
        }
    }

    //
    // Discovery
    //

    private void discoverDescriptors(final Object bean) {
        assert bean != null;

        // Recursively process all the methods/fields (@Inherited won't work here)
        for (Class type = bean.getClass(); type != null; type = type.getSuperclass()) {
            for (Method method : type.getDeclaredMethods()) {
                discoverDescriptor(bean, method);
            }
            for (Field field : type.getDeclaredFields()) {
                discoverDescriptor(bean, field);
            }
        }

        // Sanity check the argument indexes
        for (int i = 0; i < argumentHandlers.size(); i++) {
            if (argumentHandlers.get(i) == null) {
                throw new IllegalAnnotationError("No argument annotation for index: " + i);
            }
        }
    }

    private void discoverDescriptor(final Object bean, final AnnotatedElement element) {
        assert bean != null;
        assert element != null;

        Option option = element.getAnnotation(Option.class);
        Argument argument = element.getAnnotation(Argument.class);

        if (option != null && argument != null) {
            throw new IllegalAnnotationError("Element can only be Option or Argument, not both: " + element);
        }
        else if (option != null) {
            addOption(option, SetterFactory.create(element, bean));
        }
        else if (argument != null) {
            addArgument(argument, SetterFactory.create(element, bean));
        }
    }

    private void addArgument(final Argument argument, final Setter setter) {
        Handler handler = Handlers.create(new ArgumentDescriptor(setter.getName(), argument, setter.isMultiValued()), setter);
        int index = argument.index();

        // Make sure the argument will fit in the list
        while (index >= argumentHandlers.size()) {
            argumentHandlers.add(null);
        }

        if (argumentHandlers.get(index) != null) {
            throw new IllegalAnnotationError("Duplicate argument index: " + index);
        }

        argumentHandlers.set(index, handler);
    }

    private void addOption(final Option option, final Setter setter) {
        Handler handler = Handlers.create(new OptionDescriptor(setter.getName(), option, setter.isMultiValued()), setter);
        ensureUniqueOptionName(option.name());

        for (String alias : option.aliases()) {
            ensureUniqueOptionName(alias);
        }

        optionHandlers.add(handler);
    }

    private void ensureUniqueOptionName(final String name) {
        if (findOptionByName(name) != null) {
            throw new IllegalAnnotationError("Duplicate option name: " + name);
        }
    }

    //
    // Processing
    //

    private class ParametersImpl
        implements Parameters
    {
        private final String[] args;

        private int pos = 0;

        Handler handler;

        public ParametersImpl(final String[] args) {
            assert args != null;
            this.args = args;
        }

        private boolean hasMore() {
            return pos < args.length;
        }

        private String current() {
            return args[pos];
        }

        private void skip(final int n) {
            pos += n;
        }

        public String get(final int idx) throws Exception {
            if (pos + idx >= args.length) {
                throw new ProcessingException(Messages.MISSING_OPERAND.format(handler.getDescriptor(), handler.getToken(messages)));
            }

            String arg = args[pos + idx];

            if (handler.isKeyValuePair()) {
                int i = arg.indexOf(NameValue.SEPARATOR);
                arg = arg.substring(i, arg.length());
                arg = NameValue.parse(arg).value;
            }

            return arg;
        }
    }

    public void process(final String... args) throws Exception {
        ParametersImpl params = new ParametersImpl(args);
        Set<Handler> present = new HashSet<Handler>();
        int argIndex = 0;
        boolean processOptions = true;
        boolean requireOverride = false;

        //
        // TODO: Need to rewrite some of this to allow more posix-style argument processing,
        //       like --foo=bar and --foo bar, and -vvvv
        //

        while (params.hasMore()) {
            String arg = params.current();
            Handler handler;

            if (processOptions && arg.startsWith(DASH)) {
                boolean nv = arg.contains(NameValue.SEPARATOR);

                // parse this as an option.
                handler = nv ? findOptionHandler(arg) : findOptionByName(arg);

                if (handler == null) {
                    if (stopAtNonOption) {
                        // Slurp up the remaining bits as arguments (including the option we just looked at)
                        processOptions = false;
                        continue;
                    }
                    else {
                        // Unknown option, complain
                        throw new ProcessingException(Messages.UNDEFINED_OPTION.format(arg));
                    }
                }
                else if (nv) {
                    // known option, but further processing is required in the handler.
                    handler.setKeyValuePair(nv);
                }
                else {
                    // known option; skip its name
                    params.skip(1);
                }
            }
            else {
                // Complain if we have more arguments than we have handlers configured
                if (argIndex >= argumentHandlers.size()) {
                    Messages msg = argumentHandlers.size() == 0 ? Messages.NO_ARGUMENT_ALLOWED : Messages.TOO_MANY_ARGUMENTS;
                    throw new ProcessingException(msg.format(arg));
                }

                // known argument
                handler = argumentHandlers.get(argIndex);
                if (!handler.getDescriptor().isMultiValued()) {
                    argIndex++;
                }
            }

            try {
                // Hook up the current handler to the params for error message rendering
                params.handler = handler;

                // If this is an option which overrides requirements track it
                if (!requireOverride && handler.isOption()) {
                    requireOverride = ((OptionDescriptor) handler.getDescriptor()).isRequireOverride();
                }

                // Invoker the handler and then skip arguments which it has eaten up
                int consumed = handler.handle(params);
                params.skip(consumed);
            }
            catch (StopProcessingOptionsNotification n) {
                processOptions = false;
            }

            // Keep a list of the handlers which have been processed (for required validation below)
            present.add(handler);
        }

        // Ensure that all required option handlers are present, unless a processed option has overridden requirements
        if (!requireOverride) {
            for (Handler handler : optionHandlers) {
                if (handler.getDescriptor().isRequired() && !present.contains(handler)) {
                    throw new ProcessingException(Messages.REQUIRED_OPTION_MISSING.format(handler.getToken(messages)));
                }
            }

            // Ensure that all required argument handlers are present
            for (Handler handler : argumentHandlers) {
                if (handler.getDescriptor().isRequired() && !present.contains(handler)) {
                    throw new ProcessingException(Messages.REQUIRED_ARGUMENT_MISSING.format(handler.getToken(messages)));
                }
            }
        }
    }

    public void process(final List<String> args) throws Exception {
        if (args == null) {
            //noinspection RedundantArrayCreation
            process(new String[0]);
        }
        else {
            process(args.toArray(new String[args.size()]));
        }
    }

    //
    // Option Handler lookup
    //

    private Handler findOptionHandler(final String name) {
        Handler handler = findOptionByName(name);

        if (handler == null) {
            // Have not found by its name, maybe its a property?
            // Search for parts of the name (=prefix) - most specific first 
            for (int i = name.length(); i > 1; i--) {
                String prefix = name.substring(0, i);
                Map<String, Handler> possibleHandlers = filter(optionHandlers, prefix);
                handler = possibleHandlers.get(prefix);

                if (handler != null) {
                    return handler;
                }
            }
        }

        return handler;
    }

    private Map<String, Handler> filter(final List<Handler> handlers, final String keyFilter) {
        Map<String, Handler> map = new TreeMap<String, Handler>();

        for (Handler handler : handlers) {
            OptionDescriptor descriptor = (OptionDescriptor) handler.getDescriptor();

            if (keyFilter.contains(DASH_DASH)) {
                for (String alias : descriptor.getAliases()) {
                    if (alias.startsWith(keyFilter)) {
                        map.put(alias, handler);
                    }
                }
            }
            else {
                if (descriptor.getName().startsWith(keyFilter)) {
                    map.put(descriptor.getName(), handler);
                }
            }
        }

        return map;
    }

    private Handler findOptionByName(final String name) {
        for (Handler handler : optionHandlers) {
            OptionDescriptor descriptor = (OptionDescriptor) handler.getDescriptor();

            if (name.equals(descriptor.getName())) {
                return handler;
            }

            for (String alias : descriptor.getAliases()) {
                if (name.equals(alias)) {
                    return handler;
                }
            }
        }

        return null;
    }

    private static enum Messages
    {
        MISSING_OPERAND,
        UNDEFINED_OPTION,
        NO_ARGUMENT_ALLOWED,
        REQUIRED_OPTION_MISSING,
        TOO_MANY_ARGUMENTS,
        REQUIRED_ARGUMENT_MISSING;

        private final MessageSource messages = new ResourceBundleMessageSource(CommandLineProcessor.class);

        String format(final Object... args) {
            return messages.format(name(), args);
        }
    }
}
