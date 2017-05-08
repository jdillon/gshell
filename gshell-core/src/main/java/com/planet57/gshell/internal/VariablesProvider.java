package com.planet57.gshell.internal;

import com.planet57.gshell.variables.Variables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.goodies.common.ComponentSupport;

import javax.inject.Named;
import javax.inject.Provider;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

// HACK: need to adjust how variables and session work;  resolver, file-system-access and completers all depend on Variables

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
    log.debug("Variables installed: {}", variables);
    holder.set(variables);
  }

  @Override
  public Variables get() {
    Variables variables = holder.get();
    checkState(variables != null);
    return variables;
  }
}
