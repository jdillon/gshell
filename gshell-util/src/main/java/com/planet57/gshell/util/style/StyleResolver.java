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
package com.planet57.gshell.util.style;

import java.util.Locale;

import com.google.common.base.Splitter;
import com.planet57.gossip.Log;
import org.jline.utils.AttributedStyle;
import org.slf4j.Logger;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Resolves named (or source-referenced) {@link AttributedStyle}.
 *
 * @since 3.0
 */
public class StyleResolver
{
  private static final Logger log = Log.getLogger(StyleResolver.class);

  private final StyleSource source;

  private final String group;

  public StyleResolver(final StyleSource source, final String group) {
    this.source = checkNotNull(source);
    this.group = checkNotNull(group);
  }

  public AttributedStyle resolve(final String spec) {
    checkNotNull(spec);
    return apply(AttributedStyle.DEFAULT, spec);
  }

  private AttributedStyle apply(AttributedStyle style, final String spec) {
    for (String item : Splitter.on(',').omitEmptyStrings().split(spec)) {
      if (item.startsWith(".")) {
        style = applyReference(style, item);
      }
      else if (item.contains(":")) {
        style = applyColor(style, item);
      }
      else {
        style = applyNamed(style, item);
      }
    }

    return style;
  }

  private AttributedStyle applyReference(final AttributedStyle style, final String name) {
    String spec = source.get(group, name);
    if (spec != null) {
      // FIXME: this could presently be an @{...} expression, which isn't valid here
      return apply(style, spec);
    }

    log.warn("Invalid style-reference: {}", name);
    return style;
  }

  private AttributedStyle applyNamed(final AttributedStyle style, final String name) {
    switch (name.toLowerCase(Locale.US)) {
      case "default":
        return AttributedStyle.DEFAULT;

      case "bold":
        return style.bold();

      case "faint":
        return style.faint();

      case "italic":
        return style.italic();

      case "underline":
        return style.underline();

      case "blink":
        return style.blink();

      case "inverse":
        return style.inverse();

      case "inverse-neg":
      case "inverseNeg":
        return style.inverseNeg();

      case "conceal":
        return style.conceal();

      case "crossed-out":
      case "crossedOut":
        return style.crossedOut();

      case "hidden":
        return style.hidden();

      default:
        log.warn("Unknown style: {}", name);
        return style;
    }
  }

  private AttributedStyle applyColor(final AttributedStyle style, final String spec) {
    // extract color-mode:color-name
    String[] parts = spec.split(":", 2);
    String colorMode = parts[0];
    String colorName = parts[1];

    // resolve the color-name
    Integer color = color(colorName);
    if (color == null) {
      log.warn("Invalid color-name: {}", colorName);
    }
    else {
      // resolve and apply color-mode
      switch (colorMode.toLowerCase(Locale.US)) {
        case "fg":
          return style.foreground(color);

        case "bg":
          return style.background(color);

        default:
          log.warn("Invalid color-mode: {}", colorMode);
      }
    }
    return style;
  }

  private static Integer color(final String name) {
    switch (name.toLowerCase(Locale.US)) {
      case "black":
        return AttributedStyle.BLACK;

      case "red":
        return AttributedStyle.RED;

      case "green":
        return AttributedStyle.GREEN;

      case "yellow":
        return AttributedStyle.YELLOW;

      case "blue":
        return AttributedStyle.BLUE;

      case "magenta":
        return AttributedStyle.MAGENTA;

      case "cyan":
        return AttributedStyle.CYAN;

      case "white":
        return AttributedStyle.WHITE;

      case "bright":
        return AttributedStyle.BRIGHT;
    }

    return null;
  }
}
