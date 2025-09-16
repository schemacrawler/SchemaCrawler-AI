/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.mcpserver.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.ai.mcpserver.McpServerMain.McpServer;
import schemacrawler.tools.ai.mcpserver.server.ServerHealth;
import schemacrawler.tools.ai.mcpserver.test.SchemaCrawlerMCPServerTest.MockConfig;
import schemacrawler.tools.command.mcpserver.McpServerTransportType;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = {McpServer.class})
@org.springframework.test.context.junit.jupiter.SpringJUnitConfig(classes = {MockConfig.class})
public class SchemaCrawlerMCPServerTest {

  @TestConfiguration
  static class MockConfig {
    @Bean
    Catalog catalog() {
      return mock(Catalog.class);
    }

    @Bean
    DatabaseConnectionSource databaseConnectionSource() {
      return mock(DatabaseConnectionSource.class);
    }

    @Bean
    boolean isInErrorState() {
      return false;
    }

    @Bean
    McpServerTransportType mcpTransport() {
      return McpServerTransportType.sse;
    }

    @Bean
    ServerHealth serverHealth() {
      return mock(ServerHealth.class);
    }
  }

  @LocalServerPort private int port;

  @Autowired private TestRestTemplate restTemplate;

  @Autowired private ServerHealth serverHealth;

  @BeforeEach
  public void _stubServerHealth() {
    final Map<String, String> state = new HashMap<>();
    state.put("_server", "SchemaCrawler AI MCP Server Test");
    state.put("current-timestamp", "2025-01-01T00:00:00");
    state.put("in-error-state", "false");
    state.put("server-uptime", "PT0S");
    state.put("transport", "stdio");
    when(serverHealth.currentState()).thenReturn(state);
    when(serverHealth.currentStateString())
        .thenReturn(
            "SchemaCrawler AI MCP Server Test\n"
                + "in-error-state=false; server-uptime=PT0S; transport=stdio");
  }

  @Test
  @DisplayName("Application context loads successfully")
  public void contextLoads() {
    // This test will fail if the application context cannot start
  }

  @Test
  @DisplayName("Health endpoint returns status UP in integration test")
  public void healthEndpoint() {
    final ResponseEntity<Map> response =
        restTemplate.getForEntity("http://localhost:" + port + "/health", Map.class);

    assertThat(response.getStatusCode(), is(HttpStatus.OK));

    final Map<String, String> currentStatus = response.getBody();
    assertThat(currentStatus, is(not(nullValue())));
    assertThat(currentStatus.isEmpty(), is(false));
  }
}
