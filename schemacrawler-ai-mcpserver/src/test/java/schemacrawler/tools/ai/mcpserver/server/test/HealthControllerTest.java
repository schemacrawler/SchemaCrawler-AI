/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.mcpserver.server.test;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import schemacrawler.tools.ai.mcpserver.server.HealthController;

@WebMvcTest(HealthController.class)
@ContextConfiguration(classes = {HealthController.class})
public class HealthControllerTest {

  @Autowired private MockMvc mockMvc;

  @Test
  @DisplayName("Health endpoint should return status UP")
  public void healthCheckEndpoint() throws Exception {
    mockMvc
        .perform(get("/health").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status", is("UP")))
        .andExpect(jsonPath("$.service", is("SchemaCrawler MCP Server")))
        .andExpect(jsonPath("$.timestamp").exists());
  }

  @Test
  @DisplayName("Root endpoint should return welcome message")
  public void rootEndpoint() throws Exception {
    mockMvc
        .perform(get("/").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.message", containsString("running")))
        .andExpect(jsonPath("$.health_endpoint", is("/health")))
        .andExpect(jsonPath("$.timestamp").exists());
  }
}
