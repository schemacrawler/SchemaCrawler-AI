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
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import io.modelcontextprotocol.server.McpServerFeatures.SyncToolSpecification;
import io.modelcontextprotocol.spec.McpSchema.CallToolRequest;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.TextContent;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import schemacrawler.ermodel.model.ERModel;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.ai.mcpserver.server.DatabaseConnectionService;
import schemacrawler.tools.ai.mcpserver.server.ToolHelper;
import schemacrawler.tools.ai.mcpserver.utility.InErrorFactory;
import schemacrawler.tools.ai.tools.FunctionDefinition;
import schemacrawler.tools.ai.tools.FunctionExecutor;
import schemacrawler.tools.ai.tools.FunctionReturn;
import schemacrawler.tools.ai.tools.NoParameters;
import schemacrawler.tools.ai.tools.TextFunctionReturn;
import schemacrawler.tools.ai.utility.JsonUtility;
import schemacrawler.tools.state.AbstractExecutionState;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ObjectNode;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.property.PropertyName;

@TestInstance(Lifecycle.PER_CLASS)
@SpringJUnitConfig(
    classes = {ToolHelperTest.MockConfig.class, ToolHelper.class, DatabaseConnectionService.class})
public class ToolHelperTest {

  /** Trivial function definition whose executor returns a fixed text. */
  public static class TrivialFunctionDefinition implements FunctionDefinition<NoParameters> {

    private static final class TrivialFunctionExecutor extends AbstractExecutionState
        implements FunctionExecutor<NoParameters> {

      @Override
      public FunctionReturn call() {
        return new TextFunctionReturn("ok");
      }

      @Override
      public void configure(final NoParameters parameters) {}

      @Override
      public PropertyName getCommandName() {
        return null;
      }

      @Override
      public NoParameters getCommandOptions() {
        return new NoParameters();
      }

      @Override
      public void initialize() {}

      @Override
      public boolean usesConnection() {
        return false;
      }
    }

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
      return new TrivialFunctionExecutor();
    }

    @Override
    public JsonNode toJson() {
      final ObjectNode definitionNode = JsonUtility.mapper.createObjectNode();
      definitionNode.set("inputSchema", JsonUtility.mapper.createObjectNode());
      return definitionNode;
    }

    @Override
    public boolean usesConnection() {
      return false;
    }
  }

  @TestConfiguration
  static class MockConfig {
    @Bean
    Catalog catalog() {
      return InErrorFactory.createErroredCatalog();
    }

    @Bean
    DatabaseConnectionSource databaseConnectionSource() {
      return InErrorFactory.createErroredConnectionSource();
    }

    @Bean
    ERModel erModel() {
      return InErrorFactory.createErroredERModel();
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
