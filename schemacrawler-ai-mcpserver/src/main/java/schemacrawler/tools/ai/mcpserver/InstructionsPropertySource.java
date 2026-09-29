/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.mcpserver;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.joining;
import static us.fatehi.utility.Utility.isBlank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;
import org.springframework.core.env.MapPropertySource;
import schemacrawler.tools.ai.mcpserver.server.DatabaseIdentity;
import us.fatehi.utility.ioresource.ClasspathInputResource;

public class InstructionsPropertySource extends MapPropertySource {

  private static final String INSTRUCTIONS_PROPERTY = "spring.ai.mcp.server.instructions";

  private static String toolUsageGuide() {
    try (final BufferedReader reader =
        new ClasspathInputResource("tool-usage-guide.md").openNewInputReader(UTF_8)) {
      final String text = new BufferedReader(reader).lines().collect(joining("\n"));
      return text;
    } catch (final IOException e) {
      throw new UncheckedIOException("Could not read tool usage guide", e);
    }
  }

  private static String instructions(final DatabaseIdentity databaseIdentity) {
    final String description = databaseIdentity.description();
    final String toolUsageGuide = toolUsageGuide();
    if (isBlank(description)) {
      return toolUsageGuide;
    }
    return description + "\n\n" + toolUsageGuide;
  }

  public InstructionsPropertySource(final DatabaseIdentity databaseIdentity) {
    super("mcpServerInstructions", Map.of(INSTRUCTIONS_PROPERTY, instructions(databaseIdentity)));
  }
}
