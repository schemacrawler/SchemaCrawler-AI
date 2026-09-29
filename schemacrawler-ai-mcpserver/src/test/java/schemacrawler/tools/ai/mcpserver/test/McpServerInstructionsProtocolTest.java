/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.mcpserver.test;

import static org.assertj.core.api.Assertions.not;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.nullValue;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;
import io.modelcontextprotocol.spec.McpSchema.CallToolRequest;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.InitializeResult;
import io.modelcontextprotocol.spec.McpSchema.TextContent;
import java.time.Duration;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import schemacrawler.test.utility.crawl.LightCatalogUtility;
import schemacrawler.tools.ai.mcpserver.McpServerInitializer;
import schemacrawler.tools.ai.mcpserver.McpServerMain.McpServer;
import schemacrawler.tools.ai.mcpserver.McpServerTransportType;
import schemacrawler.tools.ai.utility.JsonUtility;
import tools.jackson.databind.JsonNode;
import us.fatehi.test.utility.TestObjectUtility;
import us.fatehi.utility.datasource.DatabaseConnectionSources;

public class McpServerInstructionsProtocolTest {

  @Test
  public void clientReceivesInstructions() {
    final McpServerInitializer initializer =
        new McpServerInitializer(
            LightCatalogUtility.lightCatalog(),
            DatabaseConnectionSources.fromConnection(TestObjectUtility.mockConnection()),
            McpServerTransportType.http,
            Collections.singletonList("about_database"));

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
        assertThat(result.instructions(), startsWith("# Database Server Description"));
        assertThat(result.instructions(), containsString("Start here"));
        assertThat(result.instructions(), containsString("`list_members_of_tables`"));
        assertThat(
            client.listTools().tools().stream()
                .filter(tool -> "about_database".equals(tool.name()))
                .count(),
            is(1L));

        final CallToolResult aboutDatabase =
            client.callTool(
                CallToolRequest.builder("about_database")
                    .arguments(Collections.emptyMap())
                    .build());
        assertThat(aboutDatabase.isError(), is(false));
        final JsonNode response =
            JsonUtility.mapper.readTree(((TextContent) aboutDatabase.content().getFirst()).text());
        final JsonNode databaseServer = response.get("database_server");
        assertThat(databaseServer.get("alias"), is(not(emptyString())));
        assertThat(databaseServer.get("description"), is(nullValue()));
        assertThat(response.has("database-server"), is(false));
        assertThat(databaseServer.has("database_product"), is(true));
        assertThat(response.has("server_info"), is(true));
      } finally {
        client.closeGracefully();
      }
    }
  }
}
