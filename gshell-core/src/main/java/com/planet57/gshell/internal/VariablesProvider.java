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
package com.planet57.gshell.internal;

import com.planet57.gshell.variables.Variables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import javax.inject.Provider;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

// HACK: need to adjust how variables and session work;  resolver, file-system-access and completers all depend on Variables
// CommandResolverImpl
// DirectionNameCompleter
// FileNameCompleter
// FileSystemAccessImpl
// VariableNameCompleter

/**
 * ???
 *
 * @since 3.0
 */
@Named
public class VariablesProvider
  implements Provider<Variables>
{
  private static final Logger log = LoggerFactory.getLogger(VariablesProvider.class);

  private static final ThreadLocal<Variables> holder = new InheritableThreadLocal<>();

  public static void set(final Variables variables) {
    checkNotNull(variables);
    log.trace("Variables installed: {}", variables);
    holder.set(variables);
  }

  @Override
  public Variables get() {
    // TODO: this doesn't work with completers, which do not yet have a current job
//    Job job = Job.Utils.current();
//    checkState(job != null);
//
//    CommandSessionImpl session = (CommandSessionImpl) job.session();
//    return new VariablesSupport(session.getVariables());

    Variables variables = holder.get();
    checkState(variables != null);
    return variables;
  }
}
