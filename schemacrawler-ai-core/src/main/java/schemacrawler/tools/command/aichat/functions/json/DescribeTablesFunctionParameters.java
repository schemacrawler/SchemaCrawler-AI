/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.tools.command.aichat.functions.json;

import java.util.ArrayList;
import java.util.Collection;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import schemacrawler.tools.command.aichat.tools.FunctionParameters;
import schemacrawler.tools.command.serialize.model.AdditionalTableDetails;

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
public record DescribeTablesFunctionParameters(
    @JsonPropertyDescription(
            """
    Name of database table or view to describe.
    Can be a regular expression, matching the fully qualified
    table name (including the schema).
    Use an empty string if all tables are requested.
    If not specified, all tables will be returned, but the results
    could be large.
    """)
        @JsonProperty(defaultValue = "", required = false)
        String tableName,
    @JsonPropertyDescription(
            """
    Indicates what details of the database table or view to return -
    columns, primary key, foreign keys, indexes, triggers, attributes,
    and table definition.
    Columns, foreign key references to other tables, and remarks or comments
    are always returned. The other details can be requested.
    The results could be large.
    """)
        @JsonProperty(required = false)
        Collection<TableDescriptionScope> descriptionScope)
    implements FunctionParameters {

  public enum TableDescriptionScope {
    DEFAULT(null),
    PRIMARY_KEY(AdditionalTableDetails.PRIMARY_KEY),
    CHILD_TABLES(AdditionalTableDetails.CHILD_TABLES),
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
    try {
      return new ObjectMapper().writeValueAsString(this);
    } catch (final JsonProcessingException e) {
      return "";
    }
  }
}
