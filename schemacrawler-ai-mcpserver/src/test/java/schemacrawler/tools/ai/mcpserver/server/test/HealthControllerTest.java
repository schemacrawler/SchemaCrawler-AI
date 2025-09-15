/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.mcpserver.server.test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
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
import schemacrawler.tools.ai.mcpserver.server.HealthController;
import schemacrawler.tools.ai.mcpserver.server.ServerHealth;

@WebMvcTest(HealthController.class)
@ContextConfiguration(classes = {HealthController.class, HealthControllerTest.MockConfig.class})
@TestInstance(Lifecycle.PER_CLASS)
public class HealthControllerTest {

  @TestConfiguration
  static class MockConfig {
    @Bean
    ServerHealth serverHealth() {
      return mock(ServerHealth.class);
    }
  }

  @Autowired private MockMvc mockMvc;

  @Autowired private ServerHealth serverHealth;

  @BeforeEach
  public void _stubServerHealth() {
    final Map<String, String> state = new HashMap<>();
    state.put("_service", "SchemaCrawler AI MCP Server Test");
    state.put("current-timestamp", LocalDateTime.now().toString());
    when(serverHealth.currentState()).thenReturn(state);
  }

  @ParameterizedTest
  @ValueSource(strings = {"/", "/health"})
  @DisplayName("Health endpoint should return status UP")
  public void healthCheckEndpoint(final String endpoint) throws Exception {
    mockMvc
        .perform(get(endpoint).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$._service", is(startsWith("SchemaCrawler AI MCP Server"))))
        .andExpect(jsonPath("$.current-timestamp").exists());
  }
}
