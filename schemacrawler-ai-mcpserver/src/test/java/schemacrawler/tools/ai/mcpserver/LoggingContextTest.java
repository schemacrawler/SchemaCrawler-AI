/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.mcpserver;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.Map;
import java.util.logging.Level;
import org.junit.jupiter.api.Test;
import us.fatehi.test.utility.extensions.WithSystemProperty;
import us.fatehi.utility.readconfig.EnvironmentVariableConfig;

public class LoggingContextTest {

  @Test
  @WithSystemProperty(key = "SCHCRWLR_LOG_LEVEL", value = "OFF")
  void systemPropertyDoesNotOverrideEnvironmentDefaults() {
    assumeTrue(System.getenv("SCHCRWLR_LOG_LEVEL") == null);
    assertThat(new LoggingContext().getLogLevel(), is(Level.INFO));
  }

  @Test
  void injectedEnvironmentAccessorRemainsSupported() {
    final LoggingContext context =
        new LoggingContext((EnvironmentVariableConfig) () -> Map.of("SCHCRWLR_LOG_LEVEL", "fine"));
    assertThat(context.getLogLevel(), is(Level.FINE));
  }
}
