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
package com.planet57.gshell.maven;

import java.io.File;

import com.planet57.gshell.branding.BrandingSupport;
import com.planet57.gshell.branding.License;
import com.planet57.gshell.branding.LicenseSupport;
import com.planet57.gshell.util.PrintBuffer;
import org.apache.maven.project.MavenProject;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Branding for Maven GShell plugin.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
@Named
@Singleton
public class BrandingImpl
    extends BrandingSupport
{
  @Inject
  MavenProject project;

  @Override
  public String getWelcomeMessage() {
    PrintBuffer buff = new PrintBuffer();
    buff.println("\nType '@|bold help|@' for more information.");
    buff.print(line());
    buff.flush();

    return buff.toString();
  }

  @Override
  public String getDisplayName() {
    return getMessages().format("displayName");
  }

  @Override
  public String getGoodbyeMessage() {
    return getMessages().format("goodbye");
  }

  @Override
  public File getShellContextDir() {
    return project.getBasedir();
  }

  @Override
  public File getUserContextDir() {
    return resolveFile(new File(getUserHomeDir(), ".m2/gshell/" + getProgramName()));
  }

  @Override
  public License getLicense() {
    return new LicenseSupport("Apache License, Version 2.0", "http://www.apache.org/licenses/LICENSE-2.0.txt");
  }
}
