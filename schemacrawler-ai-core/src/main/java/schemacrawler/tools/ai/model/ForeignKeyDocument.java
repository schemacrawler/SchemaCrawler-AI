/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.model;

import static java.util.Objects.requireNonNull;
import static schemacrawler.tools.ai.utility.JsonUtility.mapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.io.Serial;
import schemacrawler.schema.ColumnReference;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.Table;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;
import tools.jackson.databind.node.ObjectNode;

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"name", "referenced-table"})
public final class ForeignKeyDocument implements Document {

  @Serial private static final long serialVersionUID = 1873929712139211255L;

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

  @Override
  public String getName() {
    return foreignKeyName;
  }

  @JsonProperty("referenced-table")
  public String getReferencedTableName() {
    return referencedTableName;
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
