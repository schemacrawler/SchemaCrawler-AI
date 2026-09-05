/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.functions;

import static schemacrawler.tools.ai.functions.ListMembersOfTablesFunctionParameters.TableMemberType.COLUMNS;
import static us.fatehi.utility.Utility.isBlank;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import schemacrawler.tools.ai.tools.FunctionParameters;
import schemacrawler.tools.ai.tools.base.ParameterUtility;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ListMembersOfTablesFunctionParameters(
    @JsonPropertyDescription(
            """
            Type of table member to list, such as columns, indexes, foreign keys,
            or triggers.
            """)
        @JsonProperty(defaultValue = "COLUMNS", required = true)
        TableMemberType memberType,
    @JsonPropertyDescription(
            """
            Name of table member (or members).
            May be a regular expression, matching the fully qualified
            member name (including the schema, table and member name).
            May match more than one member. Use an empty string if all
            members are requested.
            If not specified, all table members will be returned,
            but the results could be large.
            """)
        @JsonProperty(defaultValue = "", required = false)
        String memberName,
    @JsonPropertyDescription(
            """
            Name of a database table (or tables) whose members are listed.
            May be a regular expression, matching the fully qualified
            table name (including the schema), in which case, multiple tables
            may be selected.
            Use an empty string if all tables are requested.
            If not specified, all tables will be returned, but the results
            could be large.
            """)
        @JsonProperty(defaultValue = "", required = false)
        String tableName)
    implements FunctionParameters {

  public ListMembersOfTablesFunctionParameters() {
    this(null, null, null);
  }

  public ListMembersOfTablesFunctionParameters {
    if (memberType == null) {
      memberType = COLUMNS;
    }
    if (isBlank(memberName)) {
      memberName = "";
    }
    if (isBlank(tableName)) {
      tableName = "";
    }
  }

  public enum TableMemberType {
    COLUMNS("column"),
    INDEXES("index"),
    FOREIGN_KEYS("foreign-key"),
    TRIGGERS("trigger");

    private final String nameAttribute;

    TableMemberType(final String nameAttribute) {
      this.nameAttribute = nameAttribute;
    }

    public String nameAttribute() {
      return nameAttribute;
    }
  }

  @Override
  public String toString() {
    return ParameterUtility.parametersToString(this);
  }
}
