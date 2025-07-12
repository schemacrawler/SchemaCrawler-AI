/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */


package schemacrawler.tools.command.aichat.functions.text;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import schemacrawler.tools.command.aichat.tools.FunctionParameters;
import schemacrawler.tools.command.aichat.utility.JsonUtility;

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
public record LintFunctionParameters(
    @JsonPropertyDescription(
            """
    Name of database table for which to find design issues.
    Can be a regular expression.
    Use an empty string if all tables are requested.
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
    return JsonUtility.parametersToString(this);
  }
}
