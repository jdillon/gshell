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

import javax.annotation.Nullable;

import com.google.common.base.Splitter;
import com.planet57.gossip.Log;
import org.jline.utils.AttributedStyle;
import org.slf4j.Logger;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jline.utils.AttributedStyle.BLACK;
import static org.jline.utils.AttributedStyle.BLUE;
import static org.jline.utils.AttributedStyle.BRIGHT;
import static org.jline.utils.AttributedStyle.CYAN;
import static org.jline.utils.AttributedStyle.DEFAULT;
import static org.jline.utils.AttributedStyle.GREEN;
import static org.jline.utils.AttributedStyle.MAGENTA;
import static org.jline.utils.AttributedStyle.RED;
import static org.jline.utils.AttributedStyle.WHITE;
import static org.jline.utils.AttributedStyle.YELLOW;

// TODO: document style specification

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

  public StyleSource getSource() {
    return source;
  }

  public String getGroup() {
    return group;
  }

  // TODO: could consider a small cache to reduce style calculations?

  /**
   * Resolve the given style specification.
   *
   * If for some reason the specification is invalid, then {@link AttributedStyle#DEFAULT} will be used.
   */
  public AttributedStyle resolve(final String spec) {
    checkNotNull(spec);

    log.trace("Resolve: {}", spec);

    int i = spec.indexOf(":-");
    if (i != -1) {
      String[] parts = spec.split(":-");
      return resolve(parts[0].trim(), parts[1].trim());
    }

    return apply(DEFAULT, spec);
  }

  /**
   * Resolve the given style specification.
   *
   * If this resolves to {@link AttributedStyle#DEFAULT} then given default specification is used if non-null.
   */
  public AttributedStyle resolve(final String spec, @Nullable final String defaultSpec) {
    checkNotNull(spec);

    log.trace("Resolve: {}; default: {}", spec, defaultSpec);

    AttributedStyle style = apply(DEFAULT, spec);
    if (style == DEFAULT && defaultSpec != null) {
      style = apply(style, defaultSpec);
    }
    return style;
  }

  /**
   * Apply style specification.
   */
  private AttributedStyle apply(AttributedStyle style, final String spec) {
    log.trace("Apply: {}", spec);

    for (String item : Splitter.on(',').omitEmptyStrings().trimResults().split(spec)) {
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

  /**
   * Apply source-referenced named style.
   */
  private AttributedStyle applyReference(final AttributedStyle style, final String spec) {
    log.trace("Apply-reference: {}", spec);

    if (spec.length() == 1) {
      log.warn("Invalid style-reference; missing discriminator: {}", spec);
    }
    else {
      String name = spec.substring(1, spec.length());
      String resolvedSpec = source.get(group, name);
      if (resolvedSpec != null) {
        return apply(style, resolvedSpec);
      }
      // null is normal if source has not be configured with named style
    }

    return style;
  }

  /**
   * Apply default named styles.
   */
  private AttributedStyle applyNamed(final AttributedStyle style, final String name) {
    log.trace("Apply-named: {}", name);

    // TODO: consider short aliases for named styles

    switch (name.toLowerCase(Locale.US)) {
      case "default":
        return DEFAULT;

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
      case "inverseneg":
        return style.inverseNeg();

      case "conceal":
        return style.conceal();

      case "crossed-out":
      case "crossedout":
        return style.crossedOut();

      case "hidden":
        return style.hidden();

      default:
        log.warn("Unknown style: {}", name);
        return style;
    }
  }

  /**
   * Apply color styles specification.
   *
   * @param spec Color specification: {@code <color-mode>:<color-name>}
   */
  private AttributedStyle applyColor(final AttributedStyle style, final String spec) {
    log.trace("Apply-color: {}", spec);

    // extract color-mode:color-name
    String[] parts = spec.split(":", 2);
    String colorMode = parts[0].trim();
    String colorName = parts[1].trim();

    // resolve the color-name
    Integer color = color(colorName);
    if (color == null) {
      log.warn("Invalid color-name: {}", colorName);
    }
    else {
      // resolve and apply color-mode
      switch (colorMode.toLowerCase(Locale.US)) {
        case "foreground":
        case "fg":
        case "f":
          return style.foreground(color);

        case "background":
        case "bg":
        case "b":
          return style.background(color);

        default:
          log.warn("Invalid color-mode: {}", colorMode);
      }
    }
    return style;
  }

  // TODO: consider simplify and always using StyleColor, for now for compat with other bits leaving syntax complexity

  /**
   * Returns the color identifier for the given name.
   *
   * Bright color can be specified with: {@code !<color>} or {@code bright-<color>}.
   *
   * Full xterm256 color can be specified with: {@code ~<color>}.
   *
   * @return color code, or {@code null} if unable to determine.
   */
  @Nullable
  private static Integer color(String name) {
    int flags = 0;
    name = name.toLowerCase(Locale.US);

    // extract bright flag from color name
    if (name.charAt(0) == '!') {
      name = name.substring(1, name.length());
      flags = BRIGHT;
    }
    else if (name.startsWith("bright-")) {
      name = name.substring(7, name.length());
      flags = BRIGHT;
    }
    else if (name.charAt(0) == '~') {
      try {
        name = name.substring(1, name.length());
        StyleColor color = StyleColor.valueOf(name);
        return color.code;
      }
      catch (IllegalArgumentException e) {
        log.warn("Invalid style-color name: {}", name);
        return null;
      }
    }

    switch (name) {
      case "black":
      case "k":
        return flags + BLACK;

      case "red":
      case "r":
        return flags + RED;

      case "green":
      case "g":
        return flags + GREEN;

      case "yellow":
      case "y":
        return flags + YELLOW;

      case "blue":
      case "b":
        return flags + BLUE;

      case "magenta":
      case "m":
        return flags + MAGENTA;

      case "cyan":
      case "c":
        return flags + CYAN;

      case "white":
      case "w":
        return flags + WHITE;
    }

    return null;
  }
}
