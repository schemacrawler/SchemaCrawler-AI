/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.mcpserver.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

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
import schemacrawler.schema.Catalog;
import schemacrawler.tools.ai.mcpserver.SseMcpServer;
import schemacrawler.tools.ai.mcpserver.server.ConfigurationManager;
import schemacrawler.tools.command.mcpserver.McpServerCommandOptions;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = {SseMcpServer.class})
public class SchemaCrawlerMCPServerTest {

  @BeforeAll
  public static void setDryRun() {
    ConfigurationManager.instantiate(mock(McpServerCommandOptions.class), mock(Catalog.class));
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

    final Map<String, String> body = response.getBody();
    System.err.println(body);
    assertThat(response.getStatusCode(), is(HttpStatus.OK));
    assertThat(body.get("status"), is("UP"));
    assertThat(body.get("service"), startsWith("SchemaCrawler AI MCP Server"));
  }
}
