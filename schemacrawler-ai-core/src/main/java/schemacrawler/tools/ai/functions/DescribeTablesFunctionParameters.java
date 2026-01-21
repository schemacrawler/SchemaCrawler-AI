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
            Try not to match all tables, but instead use a regular expression
            to match a subset or match a single table, since otherwise results may
            be large.
            """)
        @JsonProperty(required = false)
        String tableName,
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

  @Override
  public String toString() {
    return ParameterUtility.parametersToString(this);
  }
}
