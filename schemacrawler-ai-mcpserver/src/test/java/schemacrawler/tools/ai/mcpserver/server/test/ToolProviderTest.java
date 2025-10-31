/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.mcpserver.server.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.mockito.Mockito.mock;

import io.modelcontextprotocol.server.McpServerFeatures.SyncToolSpecification;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.ai.mcpserver.McpServerTransportType;
import schemacrawler.tools.ai.mcpserver.server.ServerHealth;
import schemacrawler.tools.ai.mcpserver.server.ToolHelper;
import schemacrawler.tools.ai.mcpserver.server.ToolProvider;
import schemacrawler.tools.ai.tools.FunctionDefinitionRegistry;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@TestInstance(Lifecycle.PER_CLASS)
@SpringJUnitConfig(classes = {ToolProvider.class, ToolProviderTest.MockConfig.class})
public class ToolProviderTest {

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
    Collection<String> excludeTools() {
      return Collections.singleton("server-information");
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

    @Bean
    ToolHelper toolHelper() {
      final ToolHelper toolHelper = new ToolHelper();
      return toolHelper;
    }
  }

  @Autowired private ToolProvider toolProvider;

  @Test
  @DisplayName("ToolProvider should return the right tools")
  public void testToolProvider() throws Exception {
    final List<SyncToolSpecification> schemaCrawlerTools = toolProvider.schemaCrawlerTools();
    assertThat(schemaCrawlerTools.size(), is(6));

    final List<String> actualToolNames =
        schemaCrawlerTools.stream().map(function -> function.tool().name()).toList();
    final String[] expectedToolNames = {
      "table-sample", "lint", "describe-tables", "describe-routines", "list-across-tables", "list"
    };
    assertThat(actualToolNames, contains(expectedToolNames));
  }
}
