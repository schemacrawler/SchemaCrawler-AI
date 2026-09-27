/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.mcpserver.server;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static schemacrawler.tools.ai.utility.JsonUtility.mapper;

import io.modelcontextprotocol.spec.McpSchema.CallToolRequest;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.TextContent;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import schemacrawler.tools.ai.tools.DatabaseIdentity;
import schemacrawler.tools.ai.tools.FunctionCallback;
import schemacrawler.tools.ai.tools.FunctionDefinition;
import schemacrawler.tools.ai.tools.FunctionExecutor;
import schemacrawler.tools.ai.tools.FunctionReturn;
import schemacrawler.tools.ai.tools.JsonFunctionReturn;
import schemacrawler.tools.ai.tools.NoParameters;
import schemacrawler.tools.ai.tools.NoResultsFunctionReturn;
import schemacrawler.tools.ai.tools.TextFunctionReturn;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.jdbc.serverfingerprint.DatabaseServerFingerprint;
import us.fatehi.utility.jdbc.serverfingerprint.FingerprintConfidence;
import us.fatehi.utility.jdbc.serverfingerprint.HostClassification;
import us.fatehi.utility.property.PropertyName;

@TestInstance(Lifecycle.PER_CLASS)
@SpringJUnitConfig(
    classes = {CallToolHandlerTest.MockConfig.class, DatabaseConnectionService.class})
public class CallToolHandlerTest {

  @TestConfiguration
  static class MockConfig {
    @Bean
    DatabaseConnectionSource databaseConnectionSource() {
      return mock(DatabaseConnectionSource.class);
    }
  }

  private static final DatabaseIdentity IDENTITY =
      new DatabaseIdentity(
          "crm-prod",
          "CRM system of record",
          "PostgreSQL",
          new DatabaseServerFingerprint(
              "postgresql", HostClassification.INTERNAL, "a1b2c3", FingerprintConfidence.MEDIUM));

  @Test
  public void exceptionResultWithIdentity() throws Exception {
    final FunctionExecutor<NoParameters> executor = mock(FunctionExecutor.class);
    when(executor.call()).thenThrow(new RuntimeException("Tool failed"));

    final CallToolResult result = callTool(executor, IDENTITY);

    assertThat(result.isError(), is(true));
    assertThat(outputNode(result).has("database"), is(false));
    assertIdentityBlock(metadataNode(result).get("database"));
  }

  @Test
  public void jsonResultWithEmptyIdentity() throws Exception {
    final JsonFunctionReturn functionReturn = new JsonFunctionReturn("list", listNode());

    final CallToolResult result =
        callTool(executorReturning(functionReturn), DatabaseIdentity.empty());

    assertThat(outputText(result), is(functionReturn.get()));
    assertThat(metadataNode(result).has("database"), is(false));
  }

  @Test
  public void jsonResultWithIdentity() throws Exception {
    final JsonFunctionReturn functionReturn = new JsonFunctionReturn("list", listNode());

    final CallToolResult result = callTool(executorReturning(functionReturn), IDENTITY);

    final JsonNode outputNode = outputNode(result);
    assertThat(outputNode.properties().iterator().next().getKey(), is("database"));
    assertIdentityBlock(outputNode.get("database"));
    assertThat(outputNode.get("list"), is(functionReturn.getResult().get("list")));
    assertThat(outputNode.size(), is(2));
    assertThat(metadataNode(result).has("database"), is(false));
  }

  @Test
  public void noResultsWithIdentity() throws Exception {
    final CallToolResult result =
        callTool(executorReturning(new NoResultsFunctionReturn()), IDENTITY);

    assertThat(outputText(result), is(new NoResultsFunctionReturn().get()));
    assertIdentityBlock(metadataNode(result).get("database"));
  }

  @Test
  public void textResultWithEmptyIdentity() throws Exception {
    final CallToolResult result =
        callTool(executorReturning(new TextFunctionReturn("ok")), DatabaseIdentity.empty());

    assertThat(outputText(result), is("ok"));
    assertThat(metadataNode(result).has("database"), is(false));
    assertThat(metadataNode(result).get("mime-type").asString(), is("text/plain"));
  }

  @Test
  public void textResultWithIdentity() throws Exception {
    final CallToolResult result =
        callTool(executorReturning(new TextFunctionReturn("ok")), IDENTITY);

    assertThat(outputText(result), is("ok"));
    assertIdentityBlock(metadataNode(result).get("database"));
    assertThat(metadataNode(result).get("mime-type").asString(), is("text/plain"));
  }

  private void assertIdentityBlock(final JsonNode databaseNode) {
    assertThat(databaseNode.get("alias").asString(), is("crm-prod"));
    assertThat(databaseNode.get("database-product-name").asString(), is("PostgreSQL"));
    assertThat(databaseNode.get("fingerprint").asString(), is("a1b2c3"));
    assertThat(databaseNode.has("description"), is(false));
    assertThat(databaseNode.has("confidence"), is(false));
    assertThat(databaseNode.has("host-classification"), is(false));
  }

  private CallToolResult callTool(
      final FunctionExecutor<NoParameters> executor, final DatabaseIdentity identity) {
    final FunctionDefinition<NoParameters> definition = mock(FunctionDefinition.class);
    when(definition.getFunctionName()).thenReturn(new PropertyName("test-tool"));
    when(definition.getParametersClass()).thenReturn(NoParameters.class);
    when(definition.newParameters()).thenReturn(new NoParameters());
    when(definition.newExecutor()).thenReturn(executor);

    final FunctionCallback<NoParameters> functionCallback =
        new FunctionCallback<>(definition, null, null, null, identity);
    final CallToolHandler handler = new CallToolHandler(functionCallback, identity);

    final CallToolResult result =
        handler.apply(
            null, CallToolRequest.builder("test-tool").arguments(Collections.emptyMap()).build());
    assertThat(result.content(), hasSize(2));
    return result;
  }

  private FunctionExecutor<NoParameters> executorReturning(final FunctionReturn functionReturn)
      throws Exception {
    final FunctionExecutor<NoParameters> executor = mock(FunctionExecutor.class);
    when(executor.call()).thenReturn(functionReturn);
    return executor;
  }

  private ArrayNode listNode() {
    final ArrayNode list = mapper.createArrayNode();
    final ObjectNode item = list.addObject();
    item.put("name", "CUSTOMERS");
    return list;
  }

  private JsonNode metadataNode(final CallToolResult result) {
    return mapper.readTree(((TextContent) result.content().getLast()).text());
  }

  private JsonNode outputNode(final CallToolResult result) {
    return mapper.readTree(outputText(result));
  }

  private String outputText(final CallToolResult result) {
    return ((TextContent) result.content().getFirst()).text();
  }
}
