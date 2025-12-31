/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.mcpserver.utility;

import java.io.Serial;
import org.springframework.core.SpringVersion;
import us.fatehi.utility.property.BaseProductVersion;

public class SpringFrameworkVersion extends BaseProductVersion {

  @Serial private static final long serialVersionUID = -7653937680189652866L;

  public SpringFrameworkVersion() {
    super("Spring Framework", SpringVersion.getVersion());
  }
}
