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

package org.apache.gshell.cli2;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.gshell.cli2.setter.Setter;
import org.apache.gshell.cli2.setter.SetterFactory;
import org.apache.gshell.i18n.MessageSource;
import org.apache.xbean.propertyeditor.PropertyEditors;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

/**
 * Processes an object for command-line configuration annotations.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class Processor
{
    private List<OptionDescriptor> optionDescriptors = new LinkedList<OptionDescriptor>();

    private MessageSource messages;

    public Processor() {}

    public Processor(final Object bean) {
        addBean(bean);
    }

    public void setMessages(final MessageSource messages) {
        this.messages = messages;
    }

    public MessageSource getMessages() {
        return messages;
    }

    public void addBean(final Object bean) {
        discoverDescriptors(bean);
    }

    //
    // Discovery
    //

    private void discoverDescriptors(final Object bean) {
        assert bean != null;

        // Recursively process all the methods/fields (@Inherited won't work here)
        for (Class type=bean.getClass(); type!=null; type=type.getSuperclass()) {
            for (Method method : type.getDeclaredMethods()) {
                discoverDescriptor(bean, method);
            }
            for (Field field : type.getDeclaredFields()) {
                discoverDescriptor(bean, field);
            }
        }
    }

    private void discoverDescriptor(final Object bean, final AnnotatedElement element) {
        assert bean != null;
        assert element != null;

        Option option = element.getAnnotation(Option.class);
        Argument argument = element.getAnnotation(Argument.class);

        if (option != null && argument != null) {
            throw new IllegalAnnotationError("Element can only be Option or Argument, not both: " + element); // TODO: i18n
        }
        else if (option != null) {
            addOption(option, SetterFactory.create(element, bean));
        }
        else if (argument != null) {
            addArgument(argument, SetterFactory.create(element, bean));
        }
    }

    private void addArgument(final Argument argument, final Setter setter) {
        assert argument != null;
        assert setter != null;

        // FIXME: Ignore for now
    }

    private void addOption(final Option option, final Setter setter) {
        OptionDescriptor desc = new OptionDescriptor(option, setter);
        optionDescriptors.add(desc);
    }

    //
    // Processing
    //

    public void process(final String... args) throws ProcessingException {
        assert args != null;

        Options options = new Options();

        for (OptionDescriptor desc : optionDescriptors) {
            options.addOption(new OptionDescriptorOption(desc));
        }

        CommandLineParser parser = new PosixParser();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
        }
        catch (ParseException e) {
            throw new ProcessingException(e);
        }

        for (org.apache.commons.cli.Option opt : cmd.getOptions()) {
            dumpOption(opt);

            if (opt instanceof OptionDescriptorOption) {
                OptionDescriptor desc = ((OptionDescriptorOption)opt).getDescriptor();
                Setter setter = desc.getSetter();

                // TODO: Add multivalue
                
                setter.set(PropertyEditors.getValue(desc.getType(), opt.getValue()));
            }
        }

        // TODO: Process arguments

        System.out.println("left over: " + cmd.getArgList());
    }

    private void dumpOption(final org.apache.commons.cli.Option option) {
        System.out.println("option" + option + " (" + option.getClass().getName() + ")");
        System.out.println("    ID: " + option.getId());
        System.out.println("    Opt: " + option.getOpt());
        System.out.println("    LongOpt: " + option.getLongOpt());
        System.out.println("    Description: " + option.getDescription());
        System.out.println("    Required: " + option.isRequired());
        System.out.println("    Type: " + option.getType());
        System.out.println("    Has arg: " + option.hasArg());
        System.out.println("    Has arg name: " + option.getArgName());
        System.out.println("    Optional Arg: " + option.hasOptionalArg());
        System.out.println("    Arg name: " + option.getArgName());
        System.out.println("    Args: " + option.getArgs());
        System.out.println("    Value Sep: " + option.getValueSeparator());
        System.out.println("    Value: " + option.getValue());
        System.out.println("    Values: " + option.getValuesList());
    }
}