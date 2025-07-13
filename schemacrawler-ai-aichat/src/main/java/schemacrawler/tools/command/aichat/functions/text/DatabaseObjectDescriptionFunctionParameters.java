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
import schemacrawler.tools.ai.tools.FunctionParameters;
import schemacrawler.tools.ai.utility.JsonUtility;

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
public record DatabaseObjectDescriptionFunctionParameters(
    @JsonPropertyDescription(
            """
    Name of database object to describe.
    Can be a regular expression.
    Use an empty string if all database objects are requested.
    """)
        @JsonProperty(defaultValue = "", required = false)
        String databaseObjectName,
    @JsonPropertyDescription(
            """
        Indicates the type of database objects to show - like
        tables (all types, including views),
        routines (that is, functions and stored procedures),
        sequences, or synonyms.
        """)
        @JsonProperty(defaultValue = "NONE", required = true)
        DatabaseObjectsScope databaseObjectsScope)
    implements FunctionParameters {

  public enum DatabaseObjectsScope {
    NONE,
    TABLES,
    ROUTINES,
    SEQUENCES,
    SYNONYMS;
  }

  public DatabaseObjectDescriptionFunctionParameters {
    if (databaseObjectName == null || databaseObjectName.isBlank()) {
      databaseObjectName = "";
    }
    if (databaseObjectsScope == null) {
      databaseObjectsScope = DatabaseObjectsScope.NONE;
    }
  }

  @Override
  public String toString() {
    return JsonUtility.parametersToString(this);
  }
}
