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
public record DetectClustersFunctionParameters(
    @JsonPropertyDescription(
            """
            Name of a database table or view used to select communities. A community
            is returned when any member matches.
            May be specified as a regular expression, matching the fully qualified
            table name (including the schema).
            Try not to match all tables, but instead use a regular expression
            to match a subset or match a single table, since otherwise results may
            be large.
            """)
        @JsonProperty(required = false)
        String tableName,
    @JsonPropertyDescription(
            """
            Maximum number of communities to return. Defaults to 5.
            -1 returns all matching communities without limiting.
            """)
        @JsonProperty(defaultValue = "5", required = false)
        Integer maxCommunities,
    @JsonPropertyDescription(
            """
            Maximum number of member tables and views to include per community.
            Defaults to 5.
            -1 returns all members without limiting.
            """)
        @JsonProperty(defaultValue = "5", required = false)
        Integer maxCommunitySize)
    implements FunctionParameters {

  public DetectClustersFunctionParameters() {
    this("", 5, 5);
  }

  public DetectClustersFunctionParameters {
    if (tableName == null) {
      tableName = "";
    }
    if (maxCommunities == null) {
      maxCommunities = 5;
    }
    if (maxCommunitySize == null) {
      maxCommunitySize = 5;
    }
  }

  @Override
  public String toString() {
    return ParameterUtility.parametersToString(this);
  }
}
