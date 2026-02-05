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
import schemacrawler.ermodel.model.ManyToManyRelationship;
import schemacrawler.ermodel.model.Relationship;
import schemacrawler.ermodel.model.RelationshipCardinality;
import schemacrawler.ermodel.model.TableReferenceRelationship;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;
import tools.jackson.databind.node.ObjectNode;

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"schema", "name", "cardinality", "between", "uses-bridge-table"})
public final class RelationshipDocument implements Document {

  @Serial private static final long serialVersionUID = -6765691827862270251L;

  private final String schemaName;
  private final String relationshipName;
  private final RelationshipCardinality cardinality;
  private final String[] between;
  private final boolean usesBridgeTable;

  public RelationshipDocument(final Relationship relationship) {
    requireNonNull(relationship, "No relationship provided");

    final String schema;
    if (relationship instanceof final TableReferenceRelationship refRelationship) {
      usesBridgeTable = false;
      schema = refRelationship.getTableReference().getSchema().getFullName();
    } else if (relationship instanceof final ManyToManyRelationship mnRelationship) {
      usesBridgeTable = true;
      schema = mnRelationship.getBridgeTable().getSchema().getFullName();
    } else {
      usesBridgeTable = false;
      schema = null;
    }
    if (!isBlank(schema)) {
      schemaName = schema;
    } else {
      schemaName = null;
    }

    between =
        new String[] {
          relationship.getLeftEntity().getName(), relationship.getRightEntity().getName()
        };

    relationshipName = relationship.getName();
    cardinality = relationship.getType();
  }

  @JsonProperty("between")
  public String[] getBetween() {
    return between;
  }

  public RelationshipCardinality getCardinality() {
    return cardinality;
  }

  @Override
  public String getName() {
    return relationshipName;
  }

  @JsonProperty("schema")
  public String getSchemaName() {
    return schemaName;
  }

  @JsonProperty("uses-bridge-table")
  public boolean isUsesBridgeTable() {
    return usesBridgeTable;
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
