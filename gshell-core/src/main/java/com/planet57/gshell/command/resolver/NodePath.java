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
package com.planet57.gshell.command.resolver;

import org.sonatype.goodies.common.ComponentSupport;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.planet57.gshell.command.resolver.Node.CURRENT;
import static com.planet57.gshell.command.resolver.Node.ROOT;
import static com.planet57.gshell.command.resolver.Node.SEPARATOR;

/**
 * Representation of a {@link Node}'s path.
 *
 * @since 2.5
 */
public class NodePath
  extends ComponentSupport
{
  private static final char SEPARATOR_CHAR = SEPARATOR.charAt(0);

  private static final char CURRENT_CHAR = CURRENT.charAt(0);

  private final StringBuilder path;

  public NodePath(final String path) {
    checkNotNull(path);
    this.path = new StringBuilder(path);
  }

  public boolean isAbsolute() {
    return path.toString().startsWith(ROOT);
  }

  public String first() {
    return split()[0];
  }

  public String last() {
    String[] elements = split();
    return elements[elements.length - 1];
  }

  // FIXME: Should be like base() or something
  @Nullable
  public NodePath parent() {
    int i = path.lastIndexOf(SEPARATOR);
    if (i == 0) {
      return this;
    }
    else if (i == -1) {
      return null;
    }
    else {
      return new NodePath(path.substring(0, i));
    }
  }

  public NodePath child(final String name) {
    String tmp = toString();
    if (!tmp.endsWith(SEPARATOR)) {
      tmp += SEPARATOR;
    }
    return new NodePath(tmp + name);
  }

  public NodePath normalize() {
    // Determine the start of the first element
    int first = 0;

    if (path.charAt(0) == SEPARATOR_CHAR) {
      if (path.length() == 1) {
        return this;
      }
      first = 1;
    }

    // Iterate over each element
    int start = first;
    int max = path.length();
    int items = 0;

    while (start < max) {
      // Find the end of the element
      int end = start;
      for (; end < max && path.charAt(end) != SEPARATOR_CHAR; end++) {
        // empty
      }
      int len = end - start;

      if (items > 0 && len == 0) {
        // An empty element - axe it
        path.delete(end, end + 1);
        max = path.length();
        continue;
      }

      if (items > 0 && len == 1 && path.charAt(start) == CURRENT_CHAR) {
        // A '.' element - axe it
        path.delete(start, end + 1);
        max = path.length();
        continue;
      }

      if (len == 2 && path.charAt(start) == CURRENT_CHAR && path.charAt(start + 1) == CURRENT_CHAR) {
        // A '..' element - remove the previous element if there is a regular element to remove
        if (items > 0 && start != first) {
          // Find start of previous element
          int pos = start - 2;
          for (; pos >= 0 && path.charAt(pos) != SEPARATOR_CHAR; pos--) {
            // empty
          }
          start = pos + 1;
          path.delete(start, end + 1);
          max = path.length();
        }
        else {
          // if there are no more regulars, then consume the .. and move on
          start = start + 3;
        }
        continue;
      }

      // A regular element
      start = end + 1;
      items++;
    }

    return this;
  }

  public String[] split() {
    String[] elements = toString().split(SEPARATOR);
    if (isAbsolute()) {
      if (elements.length == 0) {
        elements = new String[1];
      }
      elements[0] = ROOT;
    }
    return elements;
  }

  @Override
  public int hashCode() {
    return path.hashCode();
  }

  @Override
  public String toString() {
    return path.toString();
  }
}
