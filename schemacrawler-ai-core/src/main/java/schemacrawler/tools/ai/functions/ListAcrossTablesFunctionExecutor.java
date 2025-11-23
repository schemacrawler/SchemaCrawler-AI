/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.functions;

import static schemacrawler.tools.ai.utility.JsonUtility.mapper;
import static us.fatehi.utility.Utility.isBlank;

import java.util.ArrayList;
import java.util.Collection;
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
import schemacrawler.tools.ai.model.ColumnDocument;
import schemacrawler.tools.ai.model.Document;
import schemacrawler.tools.ai.model.ForeignKeyDocument;
import schemacrawler.tools.ai.model.IndexDocument;
import schemacrawler.tools.ai.model.TriggerDocument;
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

    for (final Table table : catalog.getTables()) {
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
    final ArrayNode list = createTypedObjectsArray(dependantObjects, dependantObjectType);
    return new JsonFunctionReturn(listName, list);
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

  private ObjectNode createDependentObjectNode(
      final DependantObjectType dependantObjectType, final DependantObject<Table> dependantObject) {

    final Document document =
        switch (dependantObjectType) {
          case COLUMNS -> new ColumnDocument((Column) dependantObject, null);
          case INDEXES -> new IndexDocument((Index) dependantObject);
          case FOREIGN_KEYS -> new ForeignKeyDocument((ForeignKey) dependantObject);
          case TRIGGERS -> new TriggerDocument((Trigger) dependantObject);
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

  private ArrayNode createTypedObjectsArray(
      final Collection<DependantObject<Table>> dependantObjects,
      final DependantObjectType dependantObjectType) {

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

      final ObjectNode objectNode = createDependentObjectNode(dependantObjectType, dependantObject);
      list.add(objectNode);
    }

    return list;
  }
}
