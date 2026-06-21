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
import static us.fatehi.utility.Utility.isBlank;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.io.Serial;
import schemacrawler.schema.Column;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;
import tools.jackson.databind.node.ObjectNode;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"full_name", "name", "remarks", "data_type", "is_nullable", "foreign_key_to"})
public final class ColumnDocument implements Document {

  @Serial private static final long serialVersionUID = 5110252842937512910L;

  private final String fullName;
  private final String columnName;
  private final String dataType;
  private final boolean isNullable;
  private final String remarks;
  private final String referencedColumn;

  ColumnDocument(final Column column, final Column pkColumn) {
    requireNonNull(column, "No column provided");

    fullName = column.getFullName();
    columnName = column.getName();

    dataType = column.getColumnDataType().getName();

    isNullable = column.isNullable();

    final String remarks = column.getRemarks();
    if (!isBlank(remarks)) {
      this.remarks = remarks;
    } else {
      this.remarks = null;
    }

    if (pkColumn == null) {
      referencedColumn = null;
    } else {
      referencedColumn = pkColumn.getFullName();
    }
  }

  public String getDataType() {
    return dataType;
  }

  public String getFullName() {
    return fullName;
  }

  @Override
  public String getName() {
    return columnName;
  }

  @JsonProperty("foreign_key_to")
  public String getReferencedColumn() {
    return referencedColumn;
  }

  public String getRemarks() {
    return remarks;
  }

  public boolean isNullable() {
    return isNullable;
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
