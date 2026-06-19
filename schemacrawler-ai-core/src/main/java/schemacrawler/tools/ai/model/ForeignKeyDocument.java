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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.io.Serial;
import schemacrawler.ermodel.model.RelationshipCardinality;
import schemacrawler.schema.ColumnReference;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.Table;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;
import tools.jackson.databind.node.ObjectNode;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"name", "referenced_table", "cardinality"})
public final class ForeignKeyDocument implements Document {

  @Serial private static final long serialVersionUID = 1873929712139211255L;

  private final String foreignKeyName;
  private final String referencedTable;
  private final RelationshipCardinality cardinality;

  ForeignKeyDocument(final ForeignKey foreignKey, final RelationshipCardinality cardinality) {
    requireNonNull(foreignKey, "No foreign key provided");

    foreignKeyName = foreignKey.getName();

    Table parentTable = null;
    for (final ColumnReference columnReference : foreignKey) {
      parentTable = columnReference.getPrimaryKeyColumn().getParent();
      break;
    }
    referencedTable = parentTable.getFullName();

    this.cardinality = cardinality;
  }

  public RelationshipCardinality getCardinality() {
    return cardinality;
  }

  @Override
  public String getName() {
    return foreignKeyName;
  }

  public String getReferencedTable() {
    return referencedTable;
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
