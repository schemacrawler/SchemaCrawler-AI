/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.functions;

import static schemacrawler.tools.ai.model.DatabaseObjectType.ALL;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import schemacrawler.tools.ai.model.DatabaseObjectType;
import schemacrawler.tools.ai.tools.FunctionParameters;
import schemacrawler.tools.ai.tools.base.ParameterUtility;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
public record ListFunctionParameters(
    @JsonPropertyDescription(
            """
            Type of database object to list, like tables (including views),
            routines (that is, stored procedures and functions),
            schemas (that is, catalogs), sequences, or synonyms.
            If the parameter is not provided, all database objects are listed.
            """)
        @JsonProperty(defaultValue = "ALL", required = false)
        DatabaseObjectType databaseObjectType,
    @JsonPropertyDescription(
            """
            Name of database object to list.
            Is a regular expression, matching the fully qualified
            database object name (including the schema). May match
            more than one database object.
            Use an empty string if all database objects are requested.
            """)
        @JsonProperty(defaultValue = "", required = false)
        String databaseObjectName)
    implements FunctionParameters {

  public ListFunctionParameters {
    if (databaseObjectType == null) {
      databaseObjectType = ALL;
    }
  }

  @Override
  public String toString() {
    return ParameterUtility.parametersToString(this);
  }
}
