/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.model;

import static schemacrawler.tools.ai.utility.JsonUtility.mapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"db", "tables", "routines"})
public final class CatalogDocument implements Document {

  private static final long serialVersionUID = -1937966351313941597L;

  private final String databaseProductName;
  private final List<TableDocument> tables;
  private final List<RoutineDocument> routines;

  public CatalogDocument(final String databaseProductName) {
    tables = new ArrayList<>();
    routines = new ArrayList<>();
    this.databaseProductName = databaseProductName;
  }

  public void addRoutine(final RoutineDocument routine) {
    if (routine != null) {
      routines.add(routine);
    }
  }

  public void addTable(final TableDocument table) {
    if (table != null) {
      tables.add(table);
    }
  }

  @JsonProperty("db")
  public String getDatabaseProductName() {
    return databaseProductName;
  }

  @Override
  public String getName() {
    return null;
  }

  @JsonProperty("routines")
  public List<RoutineDocument> getRoutines() {
    return routines;
  }

  @JsonProperty("tables")
  public List<TableDocument> getTables() {
    return tables;
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
