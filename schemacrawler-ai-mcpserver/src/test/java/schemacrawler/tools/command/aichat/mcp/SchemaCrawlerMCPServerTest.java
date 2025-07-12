/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */


package schemacrawler.tools.command.aichat.mcp;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import schemacrawler.tools.ai.mcpserver.SseMcpServer;
import schemacrawler.tools.ai.mcpserver.server.ConfigurationManager;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = {SseMcpServer.class})
public class SchemaCrawlerMCPServerTest {

  @BeforeAll
  public static void setDryRun() {
    ConfigurationManager.getInstance().setDryRun(true);
  }

  @AfterAll
  public static void unsetDryRun() {
    ConfigurationManager.getInstance().setDryRun(false);
  }

  @LocalServerPort private int port;

  @Autowired private TestRestTemplate restTemplate;

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

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).containsKey("status");
    assertThat(response.getBody().get("status")).isEqualTo("UP");
    assertThat(response.getBody()).containsKey("service");
    assertThat(response.getBody().get("service")).isEqualTo("SchemaCrawler MCP Server");
  }

  @Test
  @DisplayName("Root endpoint returns welcome message in integration test")
  public void rootEndpoint() {
    final ResponseEntity<Map> response =
        restTemplate.getForEntity("http://localhost:" + port + "/", Map.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).containsKey("message");
    assertThat(response.getBody().get("message").toString()).contains("running");
    assertThat(response.getBody()).containsKey("health_endpoint");
    assertThat(response.getBody().get("health_endpoint")).isEqualTo("/health");
  }
}
