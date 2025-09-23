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
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasKey;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Map;
import org.junit.jupiter.api.Test;
import schemacrawler.tools.ai.tools.FunctionParameters;
import schemacrawler.tools.ai.tools.FunctionReturnType;
import schemacrawler.tools.ai.tools.ToolUtility;

public class ToolUtilityTest {

  // Sample parameter class for testing
  public static class TestParameters implements FunctionParameters {
    private String requiredParam;
    private String optionalParam;

    @Override
    public final FunctionReturnType getFunctionReturnType() {
      return FunctionReturnType.JSON;
    }

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
    final Map<String, JsonNode> schema =
        ToolUtility.extractParametersSchemaMap(TestParameters.class);

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
}
