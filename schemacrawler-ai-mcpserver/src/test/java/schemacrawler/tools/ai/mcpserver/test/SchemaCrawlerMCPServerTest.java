/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.mcpserver.test;

import static org.mockito.Mockito.mock;
import static schemacrawler.tools.command.mcpserver.McpServerTransportType.unknown;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.ai.mcpserver.SseMcpServer;
import schemacrawler.tools.ai.mcpserver.server.ConfigurationManager;
import schemacrawler.tools.ai.mcpserver.server.ConnectionService;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = {SseMcpServer.class})
public class SchemaCrawlerMCPServerTest {

  @BeforeAll
  public static void setup() {
    ConnectionService.instantiate(mock(DatabaseConnectionSource.class));
    ConfigurationManager.instantiate(unknown, mock(Catalog.class));
  }

  @LocalServerPort private int port;

  @Autowired private TestRestTemplate restTemplate;

  @Test
  @DisplayName("Application context loads successfully")
  public void contextLoads() {
    // This test will fail if the application context cannot start
  }
}
