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
public record TablePathFunctionParameters(
    @JsonPropertyDescription(
            """
            Name of source database table or view for the dependency path.
            May be specified as a regular expression, matching the fully qualified
            table name (including the schema).
            Must match exactly one table or view.
            """)
        @JsonProperty(required = true)
        String sourceTableName,
    @JsonPropertyDescription(
            """
            Name of target database table or view for the dependency path.
            May be specified as a regular expression, matching the fully qualified
            table name (including the schema).
            Must match exactly one table or view.
            """)
        @JsonProperty(required = true)
        String targetTableName,
    @JsonPropertyDescription(
            """
            Maximum number of relationship hops in the path. Defaults to 5.
            Use -1 for an unlimited path depth.
            """)
        @JsonProperty(defaultValue = "5", required = false)
        Integer maxPathDepth)
    implements FunctionParameters {

  public TablePathFunctionParameters() {
    this("", "", 5);
  }

  public TablePathFunctionParameters(final String sourceTableName, final String targetTableName) {
    this(sourceTableName, targetTableName, 5);
  }

  public TablePathFunctionParameters {
    if (maxPathDepth == null) {
      maxPathDepth = 5;
    }
  }

  @Override
  public String toString() {
    return ParameterUtility.parametersToString(this);
  }
}
