/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.model;

import static java.util.Objects.requireNonNull;
import static schemacrawler.tools.ai.utility.JsonUtility.mapper;
import static schemacrawler.utility.MetaDataUtility.getTypeName;
import static us.fatehi.utility.Utility.trimToEmpty;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.io.Serial;
import schemacrawler.schema.DatabaseObject;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;
import tools.jackson.databind.node.ObjectNode;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"full_name", "schema", "name", "type"})
public class DatabaseObjectDocument implements Document {

  @Serial private static final long serialVersionUID = -6765691827862270251L;

  private final String fullName;
  private final String schemaName;
  private final String databaseObjectName;
  private final String type;

  public DatabaseObjectDocument(final DatabaseObject databaseObject) {
    requireNonNull(databaseObject, "No database object provided");

    fullName = databaseObject.getFullName();
    final String schemaName = databaseObject.getSchema().getFullName();
    this.schemaName = trimToEmpty(schemaName);
    databaseObjectName = databaseObject.getName();
    type = getTypeName(databaseObject).toLowerCase();
  }

  public String getFullName() {
    return fullName;
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
