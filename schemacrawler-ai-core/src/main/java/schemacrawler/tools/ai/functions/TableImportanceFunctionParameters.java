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

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record TableImportanceFunctionParameters(
    @JsonPropertyDescription(
            """
            Name of database table or view to report importance for.
            May be specified as a regular expression, matching the fully qualified
            table name (including the schema).
            Try not to match all tables, but instead use a regular expression
            to match a subset or match a single table, since otherwise results may
            be large.
            """)
        @JsonProperty(required = false)
        String tableName)
    implements FunctionParameters {

  public TableImportanceFunctionParameters() {
    this("");
  }

  public TableImportanceFunctionParameters {
    if (tableName == null) {
      tableName = "";
    }
  }

  @Override
  public String toString() {
    return ParameterUtility.parametersToString(this);
  }
}
