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
package com.planet57.gshell.help;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.eventbus.Subscribe;
import com.planet57.gshell.event.EventAware;
import com.planet57.gshell.util.jline.StringsCompleter2;
import org.jline.reader.Completer;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link Completer} for meta help page names.
 * Keeps up to date automatically by handling meta-page-related events.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
@Named("meta-help-page-name")
@Singleton
public class MetaHelpPageNameCompleter
  extends StringsCompleter2
  implements EventAware
{
  private final HelpPageManager helpPages;

  @Inject
  public MetaHelpPageNameCompleter(final HelpPageManager helpPages) {
    this.helpPages = checkNotNull(helpPages);
  }

  @Override
  protected void init() {
    for (MetaHelpPage page : helpPages.getMetaPages()) {
      add(page.getName());
    }
  }

  @Subscribe
  void on(final MetaHelpPageAddedEvent event) {
    add(event.getPage().getName());
  }
}
