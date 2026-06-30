/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.functions;

import static schemacrawler.tools.ai.utility.JsonUtility.mapper;
import static us.fatehi.utility.Utility.isBlank;

import java.util.ArrayList;
import java.util.Collection;
import schemacrawler.ermodel.model.ERModel;
import schemacrawler.inclusionrule.ExcludeAll;
import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.schema.Column;
import schemacrawler.schema.DependantObject;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.Index;
import schemacrawler.schema.Table;
import schemacrawler.schema.Trigger;
import schemacrawler.schemacrawler.GrepOptionsBuilder;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.tools.ai.functions.ListAcrossTablesFunctionParameters.DependantObjectType;
import schemacrawler.tools.ai.model.CompactCatalogBuilder;
import schemacrawler.tools.ai.model.Document;
import schemacrawler.tools.ai.tools.JsonFunctionReturn;
import schemacrawler.tools.ai.tools.base.AbstractJsonFunctionExecutor;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;
import us.fatehi.utility.property.PropertyName;

public final class ListAcrossTablesFunctionExecutor
    extends AbstractJsonFunctionExecutor<ListAcrossTablesFunctionParameters> {

  protected ListAcrossTablesFunctionExecutor(final PropertyName functionName) {
    super(functionName);
  }

  @Override
  public JsonFunctionReturn call() throws Exception {
    refilterCatalog();

    final Collection<DependantObject<Table>> dependantObjects = new ArrayList<>();
    final DependantObjectType dependantObjectType = commandOptions.dependantObjectType();

    for (final Table table : getCatalog().getTables()) {
      switch (dependantObjectType) {
        case COLUMNS:
          dependantObjects.addAll(table.getColumns());
          break;
        case INDEXES:
          dependantObjects.addAll(table.getIndexes());
          break;
        case FOREIGN_KEYS:
          dependantObjects.addAll(table.getForeignKeys());
          break;
        case TRIGGERS:
          dependantObjects.addAll(table.getTriggers());
          break;
        default:
          break;
      }
    }

    final String listName = dependantObjectType.name().replace('_', '-').toLowerCase();
    final ArrayNode list = createDependantObjectsArray(dependantObjects);

    return new JsonFunctionReturn(listName, list)
        .withSummary("Returned %d objects".formatted(dependantObjects.size()))
        .withNextSteps(listAcrossTablesNextSteps(dependantObjectType, dependantObjects.size()));
  }

  private String listAcrossTablesNextSteps(
      final DependantObjectType dependantObjectType, final int objectCount) {
    if (objectCount == 0) {
      return "Choose columns, indexes, foreign keys, or triggers next, because no dependent objects"
                 + " matched the current selection.";
    }

    return switch (dependantObjectType) {
      case NONE ->
          "Choose columns, indexes, foreign keys, or triggers next, because no dependent object"
              + " type was selected.";
      case COLUMNS ->
          "Inspect table indexes or referenced tables next, because column listing does not show"
              + " the table's wider relationships.";
      case INDEXES ->
          "Inspect table details or referenced tables next, because the index list does not include"
              + " full table context.";
      case FOREIGN_KEYS ->
          "Inspect referenced tables or objects that use these tables next, because foreign keys"
              + " only show one side of the relationship.";
      case TRIGGERS ->
          "Inspect table details or referenced tables next, because triggers do not show the full"
              + " table picture.";
    };
  }

  @Override
  protected SchemaCrawlerOptions createSchemaCrawlerOptions() {

    final InclusionRule grepTablesPattern = makeInclusionRule(commandOptions.tableName());

    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeSynonyms(new ExcludeAll())
            .includeSequences(new ExcludeAll())
            .includeRoutines(new ExcludeAll());
    final GrepOptionsBuilder grepOptionsBuilder =
        GrepOptionsBuilder.builder().includeGreppedTables(grepTablesPattern);
    return SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
        .withLimitOptions(limitOptionsBuilder.toOptions())
        .withGrepOptions(grepOptionsBuilder.toOptions());
  }

  private ArrayNode createDependantObjectsArray(
      final Collection<DependantObject<Table>> dependantObjects) {

    final InclusionRule dependantObjectinclusionRule =
        makeInclusionRule(commandOptions.dependantObjectName());
    final InclusionRule tableInclusionRule = makeInclusionRule(commandOptions.tableName());

    final ArrayNode list = mapper.createArrayNode();
    if (dependantObjects == null || dependantObjects.isEmpty()) {
      return list;
    }

    for (final DependantObject<Table> dependantObject : dependantObjects) {
      if (dependantObject == null
          || !dependantObjectinclusionRule.test(dependantObject.getFullName())
          || !tableInclusionRule.test(dependantObject.getParent().getFullName())) {
        continue;
      }

      final ObjectNode objectNode = createDependentObjectNode(dependantObject);
      list.add(objectNode);
    }

    return list;
  }

  private ObjectNode createDependentObjectNode(final DependantObject<Table> dependantObject) {

    final ERModel erModel = getERModel();
    final CompactCatalogBuilder catalogBuilder =
        CompactCatalogBuilder.builder(getCatalog(), erModel);
    final Document document =
        switch (dependantObject) {
          case final Column column -> catalogBuilder.buildColumnDocument(column);
          case final Index index -> catalogBuilder.buildIndexDocument(index);
          case final ForeignKey foreignKey -> catalogBuilder.buildForeignKeyDocument(foreignKey);
          case final Trigger trigger -> catalogBuilder.buildTriggerDocument(trigger);
          default -> null;
        };
    if (document == null) {
      return mapper.createObjectNode();
    }

    final ObjectNode objectNode = document.toObjectNode();
    // Add parent table
    final String schemaName = dependantObject.getSchema().getFullName();
    if (!isBlank(schemaName)) {
      objectNode.put("schema", schemaName);
    }
    objectNode.put("table", dependantObject.getParent().getName());
    return objectNode;
  }
}
