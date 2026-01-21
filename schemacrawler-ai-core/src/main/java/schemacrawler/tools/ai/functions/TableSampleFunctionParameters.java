/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.functions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import schemacrawler.tools.ai.tools.FunctionParameters;
import schemacrawler.tools.ai.tools.base.ParameterUtility;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
public record TableSampleFunctionParameters(
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

  public TableSampleFunctionParameters {
    if (tableName == null || tableName.isBlank()) {
      tableName = "";
    }
  }

  @Override
  public String toString() {
    return ParameterUtility.parametersToString(this);
  }
}
