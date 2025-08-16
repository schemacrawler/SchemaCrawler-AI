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
import schemacrawler.schema.Column;
import schemacrawler.schema.Table;

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"column", "remarks", "data-type", "referenced-column"})
public final class ColumnDocument implements Serializable {

  private static final long serialVersionUID = 5110252842937512910L;

  private final String schemaName;
  private final String tableName;
  private final String columnName;
  private final String dataType;
  private final String remarks;
  private final ColumnDocument referencedColumn;

  public ColumnDocument(final Column column, final Column pkColumn, final boolean includeTable) {
    requireNonNull(column, "No column provided");

    columnName = column.getName();

    dataType = column.getColumnDataType().getName();

    final String remarks = column.getRemarks();
    if (!isBlank(remarks)) {
      this.remarks = remarks;
    } else {
      this.remarks = null;
    }

    if (pkColumn == null) {
      referencedColumn = null;
    } else {
      referencedColumn = new ColumnDocument(pkColumn, null, false);
    }

    if (includeTable) {
      final Table table = column.getParent();

      final String schema = table.getSchema().getFullName();
      if (!isBlank(schema)) {
        schemaName = schema;
      } else {
        schemaName = null;
      }
      tableName = table.getName();
    } else {
      schemaName = null;
      tableName = null;
    }
  }

  @JsonProperty("column")
  public String getColumnName() {
    return columnName;
  }

  @JsonProperty("data-type")
  public String getDataType() {
    return dataType;
  }

  @JsonProperty("referenced-column")
  public ColumnDocument getReferencedColumn() {
    return referencedColumn;
  }

  @JsonProperty("remarks")
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
}
