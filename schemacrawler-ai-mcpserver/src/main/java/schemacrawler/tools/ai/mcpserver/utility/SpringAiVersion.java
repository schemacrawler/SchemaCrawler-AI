/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.mcpserver.utility;

import java.io.Serial;
import us.fatehi.utility.property.BaseProductVersion;

public class SpringAiVersion extends BaseProductVersion {

  @Serial private static final long serialVersionUID = -7653937680189652866L;

  private static String getSpringAiVersion() {
    final String unknownVersion = "<unknown>";
    try {
      // Read from Spring AI package manifest
      final Package springAiPackage =
          org.springframework.ai.chat.model.ChatModel.class.getPackage();
      final String version = springAiPackage.getImplementationVersion();
      return version != null ? version : unknownVersion;
    } catch (final Exception e) {
      return unknownVersion;
    }
  }

  public SpringAiVersion() {
    super("Spring AI", getSpringAiVersion());
  }
}
