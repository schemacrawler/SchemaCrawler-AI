/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.model;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.isBlank;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.io.Serializable;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.Sequence;
import schemacrawler.schema.Synonym;
import schemacrawler.schema.TypedObject;

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"schema", "name", "type"})
public final class DatabaseObjectDocument implements Serializable {

  private static final long serialVersionUID = -2159895984317222363L;

  private final String schemaName;
  private final String databaseObjectName;
  private final String type;

  public DatabaseObjectDocument(final DatabaseObject databaseObject) {
    requireNonNull(databaseObject, "No database object provided");

    final String schema = databaseObject.getSchema().getFullName();
    if (!isBlank(schema)) {
      schemaName = schema;
    } else {
      schemaName = null;
    }
    databaseObjectName = databaseObject.getName();

    if (databaseObject instanceof final TypedObject typedObject) {
      type = typedObject.getType().toString();
    } else if (databaseObject instanceof Sequence) {
      type = "sequence";
    } else if (databaseObject instanceof Synonym) {
      type = "synonym";
    } else {
      type = null;
    }
  }

  @JsonProperty("name")
  public String getDatabaseObjectName() {
    return databaseObjectName;
  }

  @JsonProperty("schema")
  public String getSchemaName() {
    return schemaName;
  }

  public String getType() {
    return type;
  }
}
