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
package com.planet57.gshell.util.style

import org.sonatype.goodies.testsupport.TestSupport

import com.planet57.gshell.util.style.StyleBundle.DefaultStyle
import com.planet57.gshell.util.style.StyleBundle.Prefix
import org.jline.utils.AttributedString
import org.jline.utils.AttributedStringBuilder
import org.junit.Test

/**
 * Various style trials.
 */
class StyleTrial
  extends TestSupport
{
  @Prefix('test')
  private interface Styles
      extends StyleBundle
  {
    @DefaultStyle('@{bold,yellow %3d}')
    AttributedString history_index(int index) // maps to '.history_index'
  }

  @Test
  void 'styler style-bundle'() {
    int index = 1
    def styles = Styler.bundle(Styles.class)
    AttributedStringBuilder buff = new AttributedStringBuilder()

    buff.append(styles.history_index(index))
  }

  @Test
  void 'styler style-factory'() {
    int index = 1
    def styles = Styler.factory('test')
    AttributedStringBuilder buff = new AttributedStringBuilder()

    buff.append(styles.style('@{bold,yellow %3d}', index))
    buff.append(styles.style('@{.history_index %3d}', index))
    // ^^^ .history_index=bold,yellow (from resource or other transparent configuration)
  }
}
