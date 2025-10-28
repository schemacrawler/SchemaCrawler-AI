/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.functions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import schemacrawler.tools.ai.tools.FunctionParameters;
import schemacrawler.tools.ai.tools.FunctionReturnType;
import schemacrawler.tools.ai.tools.base.ParameterUtility;

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
public record LintFunctionParameters(
    @JsonPropertyDescription(
            """
            May be specified as a regular expression, matching the fully qualified
            table name (including the schema).
            Use an empty string if all tables are requested.
            If not specified, all tables will be processed, but the results
            could be large, and execution time may be longer.
            """)
        @JsonProperty(defaultValue = "", required = false)
        String tableName)
    implements FunctionParameters {

  public LintFunctionParameters {
    if (tableName == null || tableName.isBlank()) {
      tableName = "";
    }
  }

  @Override
  public String toString() {
    return ParameterUtility.parametersToString(this);
  }

  @JsonIgnore
  @Override
  public FunctionReturnType getFunctionReturnType() {
    return FunctionReturnType.JSON;
  }
}
