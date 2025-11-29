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
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.io.Serial;
import java.util.List;
import java.util.stream.Collectors;
import schemacrawler.schema.Index;
import schemacrawler.schema.IndexColumn;
import schemacrawler.schema.PrimaryKey;
import schemacrawler.schema.TableConstraintColumn;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;
import tools.jackson.databind.node.ObjectNode;

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"name", "columns", "is-unique"})
public final class IndexDocument implements Document {

  @Serial private static final long serialVersionUID = 1873929712139211255L;

  private final String indexName;
  private final List<String> columns;
  private final boolean isUnique;

  public IndexDocument(final Index index) {
    requireNonNull(index, "No index provided");

    indexName = index.getName();
    columns = index.getColumns().stream().map(IndexColumn::getName).collect(Collectors.toList());
    isUnique = index.isUnique();
  }

  public IndexDocument(final PrimaryKey primaryKey) {
    requireNonNull(primaryKey, "No index provided");

    indexName = primaryKey.getName();
    columns =
        primaryKey.getConstrainedColumns().stream()
            .map(TableConstraintColumn::getName)
            .collect(Collectors.toList());
    isUnique = true;
  }

  public List<String> getColumns() {
    return columns;
  }

  @Override
  public String getName() {
    return indexName;
  }

  public boolean isUnique() {
    return isUnique;
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
