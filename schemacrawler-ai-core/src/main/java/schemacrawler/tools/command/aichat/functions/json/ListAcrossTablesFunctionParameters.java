/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */


package schemacrawler.tools.command.aichat.functions.json;

import static schemacrawler.tools.command.aichat.functions.json.ListAcrossTablesFunctionParameters.DependantObjectType.NONE;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import schemacrawler.tools.command.aichat.tools.FunctionParameters;
import schemacrawler.tools.command.aichat.utility.JsonUtility;

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
public record ListAcrossTablesFunctionParameters(
    @JsonPropertyDescription(
            """
    Type of database table dependant objects, like columns, indexes,
    foreign keys or triggers.
    """)
        @JsonProperty(defaultValue = "NONE", required = true)
        DependantObjectType dependantObjectType,
    @JsonPropertyDescription(
            """
    Name of table dependant object.
    May be a regular expression, matching the fully qualified
    dependant object name (including the schema and table). May match
    more than one dependant object.
    Use an empty string if all dependant objects are requested.
    If not specified, all table dependant objects will be returned,
    but the results could be large.
    """)
        @JsonProperty(defaultValue = "", required = false)
        String dependantObjectName,
    @JsonPropertyDescription(
            """
    Name of database table for which dependant objects are described.
    May be a regular expression, matching the fully qualified
    table name (including the schema), in which case, multiple tables
    may be returned.
    Use an empty string if all tables are requested.
    If not specified, all tables will be returned, but the results
    could be large.
    """)
        @JsonProperty(defaultValue = "", required = false)
        String tableName)
    implements FunctionParameters {

  public ListAcrossTablesFunctionParameters {
    if (dependantObjectType == null) {
      dependantObjectType = NONE;
    }
  }

  public enum DependantObjectType {
    NONE(""),
    COLUMNS("column"),
    INDEXES("index"),
    FOREIGN_KEYS("foreign-key"),
    TRIGGERS("trigger");

    private final String nameAttribute;

    private DependantObjectType(String nameAttribute) {
      this.nameAttribute = nameAttribute;
    }

    public String nameAttribute() {
      return nameAttribute;
    }
  }

  @Override
  public String toString() {
    return JsonUtility.parametersToString(this);
  }
}
