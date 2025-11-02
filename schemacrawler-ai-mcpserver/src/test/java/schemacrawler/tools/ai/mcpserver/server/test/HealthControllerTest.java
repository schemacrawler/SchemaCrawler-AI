/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.mcpserver.server.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static schemacrawler.tools.ai.utility.JsonUtility.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.ai.mcpserver.ExcludeTools;
import schemacrawler.tools.ai.mcpserver.McpServerTransportType;
import schemacrawler.tools.ai.mcpserver.server.HealthController;
import schemacrawler.tools.ai.mcpserver.server.ServerHealth;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@WebMvcTest(HealthController.class)
@ContextConfiguration(classes = {HealthController.class, HealthControllerTest.MockConfig.class})
@TestInstance(Lifecycle.PER_CLASS)
public class HealthControllerTest {

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
    ExcludeTools excludeTools() {
      return new ExcludeTools(null);
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
      final ServerHealth serverHealth = mock(ServerHealth.class);
      when(serverHealth.currentState()).thenReturn(currentState());
      return serverHealth;
    }
  }

  static Map<String, String> currentState() {
    final Map<String, String> state = new HashMap<>();
    state.put("_server", "SchemaCrawler AI MCP Server Test");
    state.put("current-timestamp", "2025-01-01T00:00:00");
    state.put("in-error-state", "false");
    state.put("server-uptime", "PT0S");
    state.put("transport", "stdio");
    return state;
  }

  @Autowired private MockMvc mockMvc;

  @ParameterizedTest
  @ValueSource(strings = {"/", "/health"})
  @DisplayName("Health endpoint should return status UP")
  public void healthCheckEndpoint(final String endpoint) throws Exception {
    final MvcResult mvcResult =
        mockMvc
            .perform(get(endpoint).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andReturn();

    final String currentStatusJson = mvcResult.getResponse().getContentAsString();
    final JsonNode node = mapper.readTree(currentStatusJson);
    final Map<String, Object> currentStateMap = mapper.convertValue(node, HashMap.class);
    assertThat("Parsed JSON should not be null", node, notNullValue());
    assertThat("Current state should match", currentStateMap, is(currentState()));
  }
}
