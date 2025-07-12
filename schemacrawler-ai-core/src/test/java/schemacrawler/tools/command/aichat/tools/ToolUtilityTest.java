/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */



package schemacrawler.tools.command.aichat.tools;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasKey;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.Map;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.databind.JsonNode;
import schemacrawler.tools.ai.tools.FunctionDefinition;
import schemacrawler.tools.ai.tools.FunctionParameters;
import schemacrawler.tools.ai.tools.ToolSpecification;
import schemacrawler.tools.ai.tools.ToolUtility;

public class ToolUtilityTest {

  // Sample parameter class for testing
  public static class TestParameters implements FunctionParameters {
    private String requiredParam;
    private String optionalParam;

    public String getOptionalParam() {
      return optionalParam;
    }

    public String getRequiredParam() {
      return requiredParam;
    }

    public void setOptionalParam(final String optionalParam) {
      this.optionalParam = optionalParam;
    }

    public void setRequiredParam(final String requiredParam) {
      this.requiredParam = requiredParam;
    }
  }

  @Test
  public void testExtractParametersSchema() {
    final Map<String, JsonNode> schema = ToolUtility.extractParametersSchema(TestParameters.class);

    assertThat(schema, notNullValue());
    assertThat(schema.isEmpty(), is(false));
    assertThat(schema.size(), greaterThan(0));
    assertThat(schema, hasKey("requiredParam"));

    final JsonNode requiredParamSchema = schema.get("requiredParam");
    assertThat(requiredParamSchema, notNullValue());
    assertThat(requiredParamSchema.has("type"), is(true));

    if (schema.containsKey("optionalParam")) {
      final JsonNode optionalParamSchema = schema.get("optionalParam");
      assertThat(optionalParamSchema, notNullValue());
      assertThat(optionalParamSchema.has("type"), is(true));
    }
  }

  @Test
  public void testToToolSpecification() {
    // Create a mock FunctionDefinition
    @SuppressWarnings("unchecked")
    final FunctionDefinition<TestParameters> mockFunctionDefinition =
        mock(FunctionDefinition.class);
    when(mockFunctionDefinition.getName()).thenReturn("test-function");
    when(mockFunctionDefinition.getDescription()).thenReturn("Test function description");
    when(mockFunctionDefinition.getParametersClass()).thenReturn(TestParameters.class);

    final ToolSpecification toolSpecification =
        ToolUtility.toToolSpecification(mockFunctionDefinition);

    assertThat(toolSpecification, notNullValue());
    assertThat(toolSpecification.name(), is("test-function"));
    assertThat(toolSpecification.description(), is("Test function description"));
    assertThat(toolSpecification.parameters(), notNullValue());

    final String parametersString = toolSpecification.getParametersAsString();
    assertThat(parametersString, containsString("type"));
    assertThat(parametersString, containsString("object"));
    assertThat(parametersString, containsString("properties"));
    assertThat(parametersString, containsString("requiredParam"));
  }
}
