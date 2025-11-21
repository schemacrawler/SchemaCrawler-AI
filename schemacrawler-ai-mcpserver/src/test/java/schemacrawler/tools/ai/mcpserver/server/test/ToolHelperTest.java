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
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import io.modelcontextprotocol.server.McpServerFeatures.SyncToolSpecification;
import io.modelcontextprotocol.spec.McpSchema.CallToolRequest;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.TextContent;
import java.sql.Connection;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.ai.mcpserver.server.ConnectionService;
import schemacrawler.tools.ai.mcpserver.server.ToolHelper;
import schemacrawler.tools.ai.mcpserver.utility.EmptyFactory;
import schemacrawler.tools.ai.tools.FunctionDefinition;
import schemacrawler.tools.ai.tools.FunctionExecutor;
import schemacrawler.tools.ai.tools.FunctionReturn;
import schemacrawler.tools.ai.tools.NoParameters;
import schemacrawler.tools.ai.tools.TextFunctionReturn;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.property.PropertyName;

@TestInstance(Lifecycle.PER_CLASS)
@SpringJUnitConfig(
    classes = {ToolHelperTest.MockConfig.class, ToolHelper.class, ConnectionService.class})
public class ToolHelperTest {

  /** Trivial function definition whose executor returns a fixed text. */
  public static class TrivialFunctionDefinition implements FunctionDefinition<NoParameters> {

    @Override
    public String getDescription() {
      return "A trivial function for testing ToolHelper";
    }

    @Override
    public String getName() {
      return "trivial";
    }

    @Override
    public Class<NoParameters> getParametersClass() {
      return NoParameters.class;
    }

    @Override
    public String getTitle() {
      return "Trivial function";
    }

    @Override
    public FunctionExecutor<NoParameters> newExecutor() {
      return new FunctionExecutor<>() {
        private Connection connection;
        private Catalog catalog;

        @Override
        public FunctionReturn call() {
          return new TextFunctionReturn("ok");
        }

        @Override
        public void configure(final NoParameters parameters) {}

        public Catalog getCatalog() {
          return catalog;
        }

        @Override
        public PropertyName getCommandName() {
          return null;
        }

        public Connection getConnection() {
          return connection;
        }

        @Override
        public void initialize() {}

        public void setCatalog(final Catalog catalog) {
          this.catalog = catalog;
        }

        public void setConnection(final Connection connection) {
          this.connection = connection;
        }

        @Override
        public boolean usesConnection() {
          return false;
        }
      };
    }
  }

  @TestConfiguration
  static class MockConfig {
    @Bean
    Catalog catalog() {
      return EmptyFactory.createEmptyCatalog(null);
    }

    @Bean
    DatabaseConnectionSource databaseConnectionSource() {
      return EmptyFactory.createEmptyDatabaseConnectionSource();
    }
  }

  @Autowired private ToolHelper toolHelper;

  @Test
  public void testToolCallHandler() throws Exception {
    final TrivialFunctionDefinition functionDefinition = new TrivialFunctionDefinition();
    final SyncToolSpecification syncToolSpecification =
        toolHelper.toSyncToolSpecification(functionDefinition);
    final CallToolRequest callToolRequest = new CallToolRequest("", Collections.emptyMap());
    final CallToolResult callToolResult =
        syncToolSpecification.callHandler().apply(null, callToolRequest);
    assertThat(callToolResult, is(not(nullValue())));
    final TextContent textContent = (TextContent) callToolResult.content().getFirst();
    assertThat(textContent.text(), is("ok"));
  }
}
