/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.functions;

import java.util.ArrayList;
import java.util.Collection;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import schemacrawler.tools.ai.model.AdditionalTableDetails;
import schemacrawler.tools.ai.tools.FunctionParameters;
import schemacrawler.tools.ai.utility.JsonUtility;

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
public record DescribeTablesFunctionParameters(
    @JsonPropertyDescription(
            """
    Name of database table or view to describe.
    May be specified as a regular expression, matching the fully qualified
    table name (including the schema).
    Use an empty string if all tables are requested.
    If not specified, all tables will be returned, but the results
    could be large.
    """)
        @JsonProperty(required = false)
        String tableName,
    @JsonPropertyDescription(
            """
    Indicates what details of the database table or view to return -
    columns, primary key, foreign keys, indexes, triggers, attributes,
    and table definition.
    Columns, foreign key references to other tables, and remarks or comments
    are always returned by default. The other details can be requested.
    The results could be large.
    """)
        @JsonProperty(required = false)
        Collection<TableDescriptionScope> descriptionScope)
    implements FunctionParameters {

  public enum TableDescriptionScope {
    DEFAULT(null),
    PRIMARY_KEY(AdditionalTableDetails.PRIMARY_KEY),
    REFERENCED_TABLES(AdditionalTableDetails.REFERENCED_TABLES),
    INDEXES(AdditionalTableDetails.INDEXES),
    TRIGGERS(AdditionalTableDetails.TRIGGERS),
    ATTRIBUTES(AdditionalTableDetails.ATTRIBUTES),
    DEFINIITION(AdditionalTableDetails.DEFINIITION);

    private final AdditionalTableDetails additionalTableDetails;

    TableDescriptionScope(final AdditionalTableDetails additionalTableDetails) {
      this.additionalTableDetails = additionalTableDetails;
    }

    public AdditionalTableDetails toAdditionalTableDetails() {
      return additionalTableDetails;
    }
  }

  public DescribeTablesFunctionParameters {
    if (tableName == null || tableName.isBlank()) {
      tableName = "";
    }
    if (descriptionScope == null) {
      descriptionScope = new ArrayList<>();
    }
  }

  @Override
  public String toString() {
    return JsonUtility.parametersToString(this);
  }
}
