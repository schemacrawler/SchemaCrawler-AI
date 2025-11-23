/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.functions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import java.util.ArrayList;
import java.util.Collection;
import schemacrawler.tools.ai.model.AdditionalTableDetails;
import schemacrawler.tools.ai.tools.FunctionParameters;
import schemacrawler.tools.ai.tools.base.ParameterUtility;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
public record DescribeTablesFunctionParameters(
    @JsonPropertyDescription(
            """
            Name of database table or view to describe.
            May be specified as a regular expression, matching the fully qualified
            table name (including the schema).
            Do not use a regular expression that will match all tables, but instead
            try to match a subset, since otherwise results may be large.
            """)
        @JsonProperty(required = false)
        String tableName,
    @JsonPropertyDescription(
            """
            Also include children (or dependent) tables for the selected tables.
            """)
        @JsonProperty(required = false, defaultValue = "false")
        boolean includeChildren,
    @JsonPropertyDescription(
            """
            Also include parent tables for (or tables that are referenced by) the selected tables.
            """)
        @JsonProperty(required = false, defaultValue = "false")
        boolean includeParents,
    @JsonPropertyDescription(
            """
            Indicates what details of the database table or view to return -
            columns, primary key, foreign keys, indexes, triggers, attributes,
            and table definition. Also returns which objects reference a given table
            as "used by objects".
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
    USED_BY_OBJECTS(AdditionalTableDetails.USED_BY_OBJECTS),
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

  public DescribeTablesFunctionParameters(
      String tableName, Collection<TableDescriptionScope> descriptionScope) {
    this(tableName, false, false, descriptionScope);
  }

  @Override
  public String toString() {
    return ParameterUtility.parametersToString(this);
  }
}
