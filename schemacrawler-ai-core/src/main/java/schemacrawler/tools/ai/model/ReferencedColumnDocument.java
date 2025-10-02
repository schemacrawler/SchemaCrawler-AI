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
import static us.fatehi.utility.Utility.trimToEmpty;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.node.ObjectNode;
import schemacrawler.schema.Column;
import schemacrawler.schema.Table;

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"schema", "table", "name"})
public final class ReferencedColumnDocument implements Document {

  private static final long serialVersionUID = -4296619897674250692L;

  private final String schemaName;
  private final String tableName;
  private final String columnName;
  private final String remarks;

  public ReferencedColumnDocument(final Column column) {
    requireNonNull(column, "No column provided");

    columnName = column.getName();

    final Table table = column.getParent();
    schemaName = trimToEmpty(table.getSchema().getFullName());
    tableName = table.getName();

    remarks = column.getRemarks();
  }

  @Override
  public String getName() {
    return columnName;
  }

  public String getRemarks() {
    return remarks;
  }

  @JsonProperty("schema")
  public String getSchemaName() {
    return schemaName;
  }

  @JsonProperty("table")
  public String getTableName() {
    return tableName;
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
