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

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.Collection;
import schemacrawler.inclusionrule.ExcludeAll;
import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.schema.Column;
import schemacrawler.schema.DependantObject;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.GrepOptionsBuilder;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.tools.ai.functions.ListAcrossTablesFunctionParameters.DependantObjectType;
import schemacrawler.tools.ai.tools.FunctionReturn;
import schemacrawler.tools.ai.tools.FunctionReturnType;
import us.fatehi.utility.property.PropertyName;

public final class ListAcrossTablesFunctionExecutor
    extends AbstractJsonFunctionExecutor<ListAcrossTablesFunctionParameters> {

  protected ListAcrossTablesFunctionExecutor(
      final PropertyName functionName, final FunctionReturnType returnType) {
    super(functionName, returnType);
  }

  @Override
  public FunctionReturn call() throws Exception {
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

    return () -> {
      final ArrayNode list = createTypedObjectsArray(dependantObjects, dependantObjectType);
      final ObjectNode listObject = wrapList(list);
      return listObject.toString();
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

    final String nameAttribute = dependantObjectType.nameAttribute();

    for (final DependantObject<Table> dependantObject : dependantObjects) {
      if (dependantObject == null
          || !dependantObjectinclusionRule.test(dependantObject.getFullName())
          || !tableInclusionRule.test(dependantObject.getParent().getFullName())) {
        continue;
      }

      final ObjectNode objectNode = mapper.createObjectNode();
      final String schemaName = dependantObject.getSchema().getFullName();
      if (!isBlank(schemaName)) {
        objectNode.put("schema", schemaName);
      }
      objectNode.put("table", dependantObject.getParent().getName());
      objectNode.put(nameAttribute, dependantObject.getName());
      if (dependantObject instanceof final Column column) {
        objectNode.put("data-type", column.getColumnDataType().getName());
      }

      list.add(objectNode);
    }

    return list;
  }
}
