/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */


package schemacrawler.tools.command.aichat.functions.text;

import static schemacrawler.tools.command.aichat.options.DatabaseObjectType.ALL;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import schemacrawler.tools.command.aichat.options.DatabaseObjectType;
import schemacrawler.tools.command.aichat.tools.FunctionParameters;
import schemacrawler.tools.command.aichat.utility.JsonUtility;

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
public record DatabaseObjectListFunctionParameters(
    @JsonPropertyDescription(
            """
    Type of database object to list, like tables (including views),
    routines (that is, functions and stored procedures),
    schemas (that is, catalogs), sequences, or synonyms.
    """)
        @JsonProperty(defaultValue = "ALL", required = true)
        DatabaseObjectType databaseObjectType)
    implements FunctionParameters {

  public DatabaseObjectListFunctionParameters {
    if (databaseObjectType == null) {
      databaseObjectType = ALL;
    }
  }

  @Override
  public String toString() {
    return JsonUtility.parametersToString(this);
  }
}
