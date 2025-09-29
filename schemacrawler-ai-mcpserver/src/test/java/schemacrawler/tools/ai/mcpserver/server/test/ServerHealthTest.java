/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.mcpserver.server.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

import java.time.Duration;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import schemacrawler.tools.ai.mcpserver.McpServerTransportType;
import schemacrawler.tools.ai.mcpserver.server.ServerHealth;

@TestInstance(Lifecycle.PER_CLASS)
@SpringJUnitConfig(classes = {ServerHealth.class, ServerHealthTest.MockConfig.class})
@TestPropertySource(
    properties = {
      "server.name=SchemaCrawler AI MCP Server",
      "server.version=TEST",
      "server.heartbeat=false"
    })
public class ServerHealthTest {

  @TestConfiguration
  static class MockConfig {
    @Bean
    boolean isInErrorState() {
      return false;
    }

    @Bean
    McpServerTransportType mcpTransport() {
      return McpServerTransportType.http;
    }
  }

  @Autowired private ServerHealth serverHealth;

  @Test
  @DisplayName("ServerHealth.currentState should include expected keys and values")
  public void currentState_hasExpectedEntries() {
    final Map<String, String> state = serverHealth.currentState();

    assertThat(state, is(notNullValue()));
    assertThat(
        state.keySet(),
        hasItems("_server", "current-timestamp", "in-error-state", "server-uptime", "transport"));

    assertThat(state.get("_server"), is("SchemaCrawler AI MCP Server TEST"));
    assertThat(state.get("in-error-state"), is("false"));
    assertThat(state.get("transport"), is("http"));

    // server-uptime should be a valid ISO-8601 duration (e.g., PT123S)
    final String uptimeStr = state.get("server-uptime");
    assertThat(uptimeStr, not(emptyOrNullString()));
    // Will throw if invalid
    Duration.parse(uptimeStr);

    final String timestamp = state.get("current-timestamp");
    assertThat(timestamp, not(emptyOrNullString()));
  }

  @Test
  @DisplayName("ServerHealth.currentStateString should include key info")
  public void currentStateString_includesInfo() {
    final String summary = serverHealth.currentStateString();

    assertThat(summary, containsString("SchemaCrawler AI MCP Server TEST"));
    assertThat(summary, containsString("in-error-state=false"));
    assertThat(summary, containsString("transport=http"));
    assertThat(summary, containsString("server-uptime="));
  }
}
