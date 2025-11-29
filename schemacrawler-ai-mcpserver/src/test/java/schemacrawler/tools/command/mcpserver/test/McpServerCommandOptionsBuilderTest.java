/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.command.mcpserver.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import schemacrawler.tools.ai.mcpserver.McpServerTransportType;
import schemacrawler.tools.command.mcpserver.McpServerCommandOptions;
import schemacrawler.tools.command.mcpserver.McpServerCommandOptionsBuilder;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.ConfigUtility;

public class McpServerCommandOptionsBuilderTest {

  @Test
  public void fromConfig() {
    final Config config = ConfigUtility.newConfig();
    config.put("transport", "http");
    final McpServerCommandOptions options =
        McpServerCommandOptionsBuilder.builder().fromConfig(config).toOptions();
    assertThat(options.mcpTransport().name(), is("http"));
  }

  @Test
  public void fromNullConfig() {
    final McpServerCommandOptionsBuilder optionsBuilder =
        McpServerCommandOptionsBuilder.builder().fromConfig(null);
    assertThrows(IllegalArgumentException.class, () -> optionsBuilder.toOptions());
  }

  @Test
  public void fromNullOptions() {
    final McpServerCommandOptionsBuilder optionsBuilder =
        McpServerCommandOptionsBuilder.builder().fromOptions(null);
    assertThrows(IllegalArgumentException.class, () -> optionsBuilder.toOptions());
  }

  @Test
  public void fromOptions() {
    final McpServerCommandOptions options =
        McpServerCommandOptionsBuilder.builder()
            .withMcpTransport(McpServerTransportType.http)
            .toOptions();
    final McpServerCommandOptionsBuilder optionsBuilder =
        McpServerCommandOptionsBuilder.builder().fromOptions(options);
    assertThat(optionsBuilder.toOptions().mcpTransport().name(), is("http"));
  }

  @Test
  public void mcpServerCommandOptionsBuilderTimeout() {

    final McpServerCommandOptionsBuilder optionsBuilder = McpServerCommandOptionsBuilder.builder();

    assertThrows(IllegalArgumentException.class, () -> optionsBuilder.toOptions());

    optionsBuilder.withMcpTransport(McpServerTransportType.http);
    assertThat(optionsBuilder.toOptions().mcpTransport().name(), is("http"));

    optionsBuilder.withMcpTransport(McpServerTransportType.stdio);
    assertThat(optionsBuilder.toOptions().mcpTransport().name(), is("stdio"));
  }

  @Test
  public void toConfig() {
    final McpServerCommandOptionsBuilder builder = McpServerCommandOptionsBuilder.builder();
    assertThrows(UnsupportedOperationException.class, () -> builder.toConfig());
  }
}
