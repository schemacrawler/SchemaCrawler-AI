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
@JsonPropertyOrder({"schema", "name", "cardinality", "between", "remarks", "hint"})
public final class RelationshipDocument implements Document {

  @Serial private static final long serialVersionUID = -6765691827862270251L;

  private final String schemaName;
  private final String relationshipName;
  private final RelationshipCardinality cardinality;
  private final String[] between;
  private final String remarks;
  private final String hint;

  public RelationshipDocument(final Relationship relationship) {
    requireNonNull(relationship, "No relationship provided");

    final String schema;
    boolean usesBridgeTable;
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
    if (usesBridgeTable) {
      hint = "Relationship name is the same as the bridge table or associative entity";
    } else {
      hint = "Relationship name is the same as the foreign key";
    }

    between =
        new String[] {
          relationship.getLeftEntity().getName(), relationship.getRightEntity().getName()
        };

    relationshipName = relationship.getName();
    cardinality = relationship.getType();

    final String remarks = relationship.getRemarks();
    if (!isBlank(remarks)) {
      this.remarks = remarks;
    } else {
      this.remarks = null;
    }
  }

  @JsonProperty("between")
  public String[] getBetween() {
    return between;
  }

  public RelationshipCardinality getCardinality() {
    return cardinality;
  }

  public String getHint() {
    return hint;
  }

  @Override
  public String getName() {
    return relationshipName;
  }

  public String getRemarks() {
    return remarks;
  }

  @JsonProperty("schema")
  public String getSchemaName() {
    return schemaName;
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
