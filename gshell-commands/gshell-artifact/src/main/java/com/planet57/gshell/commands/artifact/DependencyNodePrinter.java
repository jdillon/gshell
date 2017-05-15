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
package com.planet57.gshell.commands.artifact;

import com.planet57.gshell.util.io.IO;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyNode;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Helper to print {@link DependencyNode}.
 *
 * @since 3.0
 */
public class DependencyNodePrinter
{
  private final IO io;

  public DependencyNodePrinter(final IO io) {
    this.io = checkNotNull(io);
  }

  public void print(final DependencyNode node) {
    checkNotNull(node);
    print(node, "");
  }

  public void print(final DependencyNode node, final String indent) {
    checkNotNull(node);
    checkNotNull(indent);

    AttributedStringBuilder buff = new AttributedStringBuilder();
    buff.style(AttributedStyle.DEFAULT);

    Dependency dependency = node.getDependency();
    Artifact artifact = node.getArtifact();

    buff.append(artifact.getGroupId());
    faint(buff, ":");
    buff.append(artifact.getArtifactId());
    faint(buff, ":");
    buff.append(artifact.getExtension());
    faint(buff, ":");

    String classifier = artifact.getClassifier();
    if (classifier.length() > 0) {
      buff.append(classifier);
      faint(buff, ":");
    }

    bold(buff, artifact.getVersion());

    if (dependency != null) {
      faint(buff, " (" + dependency.getScope() + ")");
    }

    io.out.format("%s%s%n", indent, buff.toAnsi(io.terminal));

    // recurse to children with indent
    node.getChildren().forEach(child -> print(child, indent + "  "));
  }

  private static void faint(final AttributedStringBuilder buff, final String text) {
    buff.style(AttributedStyle.DEFAULT.faint());
    buff.append(text);
    buff.style(AttributedStyle.DEFAULT.faintOff());
  }

  private static void bold(final AttributedStringBuilder buff, final String text) {
    // FIXME: for some reason this is not BOLD as it should be
    buff.style(AttributedStyle.DEFAULT.bold());
    buff.append(text);
    buff.style(AttributedStyle.DEFAULT.boldOff());
  }
}
