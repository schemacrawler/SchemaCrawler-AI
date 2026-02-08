/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.mcpserver.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;
import schemacrawler.ermodel.model.ERModel;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.ai.mcpserver.ExcludeTools;
import schemacrawler.tools.ai.mcpserver.McpServerMain.McpServer;
import schemacrawler.tools.ai.mcpserver.McpServerTransportType;
import schemacrawler.tools.ai.mcpserver.server.ServerHealth;
import schemacrawler.tools.ai.mcpserver.test.SchemaCrawlerMCPServerTest.MockConfig;
import schemacrawler.tools.ai.mcpserver.utility.EmptyFactory;
import schemacrawler.tools.ai.tools.FunctionDefinitionRegistry;
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
      return EmptyFactory.createEmptyCatalog(new NullPointerException());
    }

    @Bean
    DatabaseConnectionSource databaseConnectionSource() {
      return mock(DatabaseConnectionSource.class);
    }

    @Bean
    ERModel erModel() {
      return EmptyFactory.createEmptyERModel();
    }

    @Bean
    ExcludeTools excludeTools() {
      return new ExcludeTools();
    }

    @Bean
    FunctionDefinitionRegistry functionDefinitionRegistry() {
      return FunctionDefinitionRegistry.getFunctionDefinitionRegistry();
    }

    @Bean
    boolean isInErrorState() {
      return false;
    }

    @Bean
    McpServerTransportType mcpTransport() {
      return McpServerTransportType.http;
    }

    @Bean
    ServerHealth serverHealth() {
      return mock(ServerHealth.class);
    }
  }

  @LocalServerPort private int port;

  @Autowired private RestClient.Builder restClientBuilder;

  @Autowired private ServerHealth serverHealth;

  @BeforeEach
  public void _stubServerHealth() {
    final Map<String, Object> state = new HashMap<>();
    state.put("_server", "SchemaCrawler AI MCP Server Test");
    state.put("current-timestamp", "2026-01-01T00:00:00");
    state.put("in-error-state", false);
    state.put("server-uptime", "PT0S");
    state.put("transport", "stdio");
    state.put("exclude-tools", Collections.emptySet());
    when(serverHealth.currentState()).thenReturn(state);
  }

  @Test
  @DisplayName("Application context loads successfully")
  public void contextLoads() {
    // This test will fail if the application context cannot start
  }

  @Test
  @DisplayName("Health endpoint returns status UP")
  public void healthEndpoint() {
    final RestClient restClient = restClientBuilder.baseUrl("http://localhost:" + port).build();
    final ResponseEntity<Map> response =
        restClient.get().uri("/health").retrieve().toEntity(Map.class);

    assertThat(response.getStatusCode(), is(HttpStatus.OK));

    final Map<String, String> currentStatus = response.getBody();
    assertThat(currentStatus, is(not(nullValue())));
    assertThat(currentStatus.isEmpty(), is(false));
  }
}
