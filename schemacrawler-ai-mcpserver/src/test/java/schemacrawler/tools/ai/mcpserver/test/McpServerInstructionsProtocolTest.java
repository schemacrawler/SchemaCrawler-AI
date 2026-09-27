/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.mcpserver.test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;
import io.modelcontextprotocol.spec.McpSchema.InitializeResult;
import java.time.Duration;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import schemacrawler.test.utility.crawl.LightCatalogUtility;
import schemacrawler.tools.ai.mcpserver.McpServerInitializer;
import schemacrawler.tools.ai.mcpserver.McpServerMain.McpServer;
import schemacrawler.tools.ai.mcpserver.McpServerTransportType;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

public class McpServerInstructionsProtocolTest {

  @Test
  public void clientReceivesInstructions() {
    final McpServerInitializer initializer =
        new McpServerInitializer(
            LightCatalogUtility.lightCatalog(),
            mock(DatabaseConnectionSource.class),
            McpServerTransportType.http,
            Collections.emptyList(),
            "crm-prod",
            "CRM system of record");

    try (final ConfigurableApplicationContext app =
        new SpringApplicationBuilder(McpServer.class)
            .initializers(initializer)
            .profiles(McpServerTransportType.http.name())
            .properties("server.port=0")
            .run()) {

      final String port = app.getEnvironment().getProperty("local.server.port");
      final McpSyncClient client =
          McpClient.sync(
                  HttpClientStreamableHttpTransport.builder("http://localhost:" + port).build())
              .requestTimeout(Duration.ofSeconds(30))
              .build();
      try {
        final InitializeResult result = client.initialize();

        assertThat(result.serverInfo().name(), is("schemacrawler-mcpserver"));
        assertThat(result.instructions(), startsWith("CRM system of record\n\n"));
        assertThat(result.instructions(), containsString("Start here"));
        assertThat(result.instructions(), containsString("`list_members_of_tables`"));
      } finally {
        client.closeGracefully();
      }
    }
  }
}
