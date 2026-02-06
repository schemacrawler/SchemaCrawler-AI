/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.mcpserver.server.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Mockito.mock;

import io.modelcontextprotocol.server.McpServerFeatures.SyncToolSpecification;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import schemacrawler.ermodel.model.ERModel;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.ai.mcpserver.ExcludeTools;
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
    ERModel erModel() {
      return mock(ERModel.class);
    }

    @Bean
    ExcludeTools excludeTools() {
      return new ExcludeTools(Set.of("server-information"));
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

  private static final int NUM_TOOLS = 8;

  @Autowired private ToolProvider toolProvider;

  @Test
  @DisplayName("ToolProvider should return the right tools")
  public void testToolProvider() throws Exception {
    final List<SyncToolSpecification> schemaCrawlerTools = toolProvider.schemaCrawlerTools();
    assertThat(schemaCrawlerTools.size(), is(NUM_TOOLS));

    final List<String> actualToolNames =
        schemaCrawlerTools.stream().map(function -> function.tool().name()).toList();
    assertThat(
        actualToolNames,
        containsInAnyOrder(
            "table-sample",
            "lint",
            "describe-entities",
            "describe-tables",
            "describe-routines",
            "list-across-tables",
            "list",
            "diagram"));
  }
}
