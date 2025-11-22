/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.model;

import static java.util.Objects.requireNonNull;
import static schemacrawler.tools.ai.utility.JsonUtility.mapper;
import static schemacrawler.utility.MetaDataUtility.getTypeName;
import static us.fatehi.utility.Utility.isBlank;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.io.Serial;
import schemacrawler.schema.DatabaseObject;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;
import tools.jackson.databind.node.ObjectNode;

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"schema", "name", "type"})
public final class DatabaseObjectDocument implements Document {

  @Serial private static final long serialVersionUID = -2159895984317222363L;

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
    type = getTypeName(databaseObject);
  }

  @Override
  public String getName() {
    return databaseObjectName;
  }

  @JsonProperty("schema")
  public String getSchemaName() {
    return schemaName;
  }

  public String getType() {
    return type;
  }

  @Override
  public ObjectNode toObjectNode() {
    return mapper.valueToTree(this);
  }

  @Override
  public String toString() {
    return toObjectNode().toString();
  }
}
