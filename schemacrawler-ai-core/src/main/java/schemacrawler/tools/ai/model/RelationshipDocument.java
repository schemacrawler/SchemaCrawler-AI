/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.model;

import static us.fatehi.utility.Utility.isBlank;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.io.Serial;
import schemacrawler.ermodel.model.ManyToManyRelationship;
import schemacrawler.ermodel.model.Relationship;
import schemacrawler.ermodel.model.TableReferenceRelationship;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"full_name", "name", "cardinality", "between", "remarks", "hint"})
public final class RelationshipDocument extends BaseObjectDocument {

  @Serial private static final long serialVersionUID = -6765691827862270251L;

  private final String[] between;
  private final String remarks;
  private final String hint;

  public RelationshipDocument(final Relationship relationship) {
    super(relationship);

    boolean usesBridgeTable;
    if (relationship instanceof final TableReferenceRelationship refRelationship) {
      usesBridgeTable = false;
    } else if (relationship instanceof final ManyToManyRelationship mnRelationship) {
      usesBridgeTable = true;
    } else {
      usesBridgeTable = false;
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

    final String remarks = relationship.getRemarks();
    if (!isBlank(remarks)) {
      this.remarks = remarks;
    } else {
      this.remarks = null;
    }
  }

  public String[] getBetween() {
    return between;
  }

  public String getHint() {
    return hint;
  }

  public String getRemarks() {
    return remarks;
  }

  @Override
  @JsonProperty("cardinality")
  public String getType() {
    return super.getType();
  }
}
