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
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;
import io.modelcontextprotocol.spec.McpSchema.CallToolRequest;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.TextContent;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import schemacrawler.schema.Catalog;
import schemacrawler.test.utility.crawl.LightCatalogUtility;
import schemacrawler.tools.ai.mcpserver.McpServerInitializer;
import schemacrawler.tools.ai.mcpserver.McpServerMain.McpServer;
import schemacrawler.tools.ai.mcpserver.McpServerTransportType;
import schemacrawler.tools.ai.utility.JsonUtility;
import schemacrawler.vocabulary.Vocabulary;
import schemacrawler.vocabulary.VocabularyTerm;
import tools.jackson.databind.JsonNode;
import us.fatehi.test.utility.TestObjectUtility;
import us.fatehi.utility.datasource.DatabaseConnectionSources;

/**
 * End-to-end test of the schema vocabulary terms in the server instructions and in <code>
 * about_database</code>, and that an empty <code>list</code> search has no terms.
 */
public class McpServerVocabularyProtocolTest {

  private static final String TERMS_HEADER = "# Terms Used in This Database";

  private static class FixedVocabularyInitializer extends McpServerInitializer {
    private final Vocabulary vocabulary;

    FixedVocabularyInitializer(final Vocabulary vocabulary) {
      super(
          LightCatalogUtility.lightCatalog(),
          DatabaseConnectionSources.fromConnection(TestObjectUtility.mockConnection()),
          McpServerTransportType.http,
          Collections.emptyList());
      this.vocabulary = vocabulary;
    }

    @Override
    protected Vocabulary buildVocabulary(final Catalog catalog) {
      return vocabulary;
    }
  }

  private static Vocabulary vocabulary(final boolean trusted) {
    return new Vocabulary(
        List.of(
            new VocabularyTerm("customers", "customer", 7),
            new VocabularyTerm("orders", "order", 5)),
        trusted);
  }

  @Test
  public void trustedVocabularyIsShownInInstructionsAndAboutDatabase() throws Exception {
    final String[] seen = run(true);
    final String instructions = seen[0];
    final JsonNode aboutDatabase = JsonUtility.mapper.readTree(seen[1]);
    final JsonNode emptyList = JsonUtility.mapper.readTree(seen[2]);

    assertThat(instructions, containsString(TERMS_HEADER));
    assertThat(instructions, containsString("customers (7), orders (5)"));
    assertThat(aboutDatabase.get("terms").size(), is(2));
    assertThat(emptyList.get("list").size(), is(0));
    assertThat(emptyList.has("terms"), is(false));
  }

  @Test
  public void untrustedVocabularyIsNotShown() throws Exception {
    final String[] seen = run(false);
    final JsonNode aboutDatabase = JsonUtility.mapper.readTree(seen[1]);
    final JsonNode emptyList = JsonUtility.mapper.readTree(seen[2]);

    assertThat(seen[0], not(containsString(TERMS_HEADER)));
    assertThat(aboutDatabase.has("terms"), is(false));
    assertThat(emptyList.has("terms"), is(false));
  }

  /** Returns the instructions, the about_database result and an empty list result. */
  private String[] run(final boolean trusted) {
    try (final ConfigurableApplicationContext app =
        new SpringApplicationBuilder(McpServer.class)
            .initializers(new FixedVocabularyInitializer(vocabulary(trusted)))
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
        final String instructions = client.initialize().instructions();
        final String aboutDatabase = callTool(client, "about_database", Map.of());
        final String emptyList =
            callTool(
                client,
                "list",
                Map.of("database_object_type", "TABLES", "database_object_name", "NO_SUCH_TABLE"));
        return new String[] {instructions, aboutDatabase, emptyList};
      } finally {
        client.closeGracefully();
      }
    }
  }

  private String callTool(
      final McpSyncClient client, final String name, final Map<String, Object> arguments) {
    final CallToolResult result =
        client.callTool(CallToolRequest.builder(name).arguments(arguments).build());
    assertThat(result.isError(), is(false));
    return ((TextContent) result.content().getFirst()).text();
  }
}
