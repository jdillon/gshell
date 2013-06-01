/*
 * Copyright (c) 2009-2011 the original author or authors.
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
package org.sonatype.gshell.util.cli2;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.slf4j.Logger;
import org.sonatype.gossip.Log;
import org.sonatype.gshell.util.IllegalAnnotationError;
import org.sonatype.gshell.util.cli2.handler.Handler;
import org.sonatype.gshell.util.cli2.handler.Handlers;
import org.sonatype.gshell.util.i18n.MessageSource;
import org.sonatype.gshell.util.i18n.ResourceBundleMessageSource;
import org.sonatype.gshell.util.setter.SetterFactory;
import org.sonatype.gshell.util.yarn.Yarn;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Processes an object for cli annotations.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.3
 */
public class CliProcessor
{
    private static final Logger log = Log.getLogger(CliProcessor.class);

    private final List<OptionDescriptor> optionDescriptors = new ArrayList<OptionDescriptor>();

    private final List<ArgumentDescriptor> argumentDescriptors = new ArrayList<ArgumentDescriptor>();

    private boolean stopAtNonOption;

    private MessageSource messages;

    public static enum Flavor
    {
        POSIX,
        GNU
    }

    private Flavor flavor = Flavor.POSIX;

    public CliProcessor() {
    }

    public boolean isStopAtNonOption() {
        return stopAtNonOption;
    }

    public void setStopAtNonOption(boolean flag) {
        this.stopAtNonOption = flag;
    }

    public MessageSource getMessages() {
        return messages;
    }

    public void setMessages(final MessageSource messages) {
        this.messages = messages;
    }

    public Flavor getFlavor() {
        return flavor;
    }

    public void setFlavor(final Flavor flavor) {
        assert flavor != null;
        this.flavor = flavor;
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
            throw new IllegalAnnotationError(String.format("Element can only implement @Option or @Argument, not both: %s", element));
        }

        if (opt != null) {
            log.trace("Discovered @Option for: {}", element);

            OptionDescriptor desc = new OptionDescriptor(opt, SetterFactory.create(element, bean));

            // If the type is boolean, and its marked as optional or requires args, complain to use Boolean instead
            if (desc.getSetter().getType() == boolean.class) {
                if (desc.isArgumentOptional() || desc.getArgs() != 0) {
                    throw new IllegalAnnotationError(String.format("Using Boolean for advanced processing of boolean types, on: %s", element));
                }
            }

            // Make sure we have unique names
            for (OptionDescriptor tmp : optionDescriptors) {
                if (desc.getName() != null && desc.getName().equals(tmp.getName())) {
                    throw new IllegalAnnotationError(String.format("Duplicate @Option name: %s, on: %s", desc.getName(), element));
                }
                if (desc.getLongName() != null && desc.getLongName().equals(tmp.getLongName())) {
                    throw new IllegalAnnotationError(String.format("Duplicate @Option longName: %s, on: %s", desc.getLongName(), element));
                }
            }

            optionDescriptors.add(desc);
        }
        else if (arg != null) {
            log.trace("Discovered @Argument for: {}", element);

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

    private static interface CliParser
        extends CommandLineParser
    {
        void ensureRequiredOptionsPresent() throws MissingOptionException;
    }

    private static class GnuParser
        extends org.apache.commons.cli.GnuParser
        implements CliParser
    {
        @Override
        protected void checkRequiredOptions() {
            // delay, need to check for required options after processing to support override
        }

        public void ensureRequiredOptionsPresent() throws MissingOptionException {
            super.checkRequiredOptions();
        }
    }

    private static class PosixParser
        extends org.apache.commons.cli.PosixParser
        implements CliParser
    {
        @Override
        protected void checkRequiredOptions() {
            // delay, need to check for required options after processing to support override
        }

        public void ensureRequiredOptionsPresent() throws MissingOptionException {
            super.checkRequiredOptions();
        }
    }

    public void process(final String... args) throws Exception {
        assert args != null;

        CliParser parser = null;

        switch (flavor) {
            case POSIX:
                parser = new PosixParser();
                break;
            case GNU:
                parser = new GnuParser();
                break;
        }

        assert parser != null;

        CommandLine cl;

        try {
            cl = parser.parse(createOptions(), args, stopAtNonOption);
        }
        catch (UnrecognizedOptionException e) {
            throw new ProcessingException(Messages.UNDEFINED_OPTION.format(e.getOption()));
        }
        catch (MissingArgumentException e) {
            OptionDescriptor desc = ((Opt)e.getOption()).getDescriptor();
            throw new ProcessingException(Messages.MISSING_OPERAND.format(desc.getSyntax(), desc.renderToken(messages)));
        }
        catch (ParseException e) {
            throw new ProcessingException(e);    
        }

        Set<CliDescriptor> present = new HashSet<CliDescriptor>();
        boolean override = false;

        for (Object tmp : cl.getOptions()) {
            Opt opt = (Opt)tmp;
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
                    Messages.NO_ARGUMENT_ALLOWED.format(arg) : Messages.TOO_MANY_ARGUMENTS.format(arg));
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
                throw new ProcessingException(Messages.REQUIRED_OPTION_MISSING.format(e.getMissingOptions()));
            }

            for (ArgumentDescriptor arg : argumentDescriptors) {
                if (arg.isRequired() && !present.contains(arg)) {
                    throw new ProcessingException(Messages.REQUIRED_ARGUMENT_MISSING.format(arg.renderToken(messages)));
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

        public String toString() {
            return Yarn.render(this);
        }
    }

    private static enum Messages
    {
        MISSING_OPERAND,
        UNDEFINED_OPTION,
        NO_ARGUMENT_ALLOWED,
        REQUIRED_OPTION_MISSING,
        TOO_MANY_ARGUMENTS,
        REQUIRED_ARGUMENT_MISSING;

        private final MessageSource messages = new ResourceBundleMessageSource(CliProcessor.class);

        String format(final Object... args) {
            return messages.format(name(), args);
        }
    }
}