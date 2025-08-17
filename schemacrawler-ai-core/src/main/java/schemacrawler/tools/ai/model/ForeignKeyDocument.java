/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.model;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.node.ObjectNode;
import static java.util.Objects.requireNonNull;
import schemacrawler.schema.ColumnReference;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.Table;

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"name", "referenced-table"})
public final class ForeignKeyDocument implements Serializable {

  private static final long serialVersionUID = 1873929712139211255L;

  private final String foreignKeyName;
  private final String referencedTableName;

  public ForeignKeyDocument(final ForeignKey foreignKey) {
    requireNonNull(foreignKey, "No foreign key provided");

    foreignKeyName = foreignKey.getName();

    Table parentTable = null;
    for (final ColumnReference columnReference : foreignKey) {
      parentTable = columnReference.getPrimaryKeyColumn().getParent();
      break;
    }
    referencedTableName = parentTable.getName();
  }

  @JsonProperty("name")
  public String getForeignKeyName() {
    return foreignKeyName;
  }

  @JsonProperty("referenced-table")
  public String getReferencedTableName() {
    return referencedTableName;
  }

  public ObjectNode toObjectNode() {
    return new ObjectMapper().valueToTree(this);
  }

  @Override
  public String toString() {
    return toObjectNode().toString();
  }
}
