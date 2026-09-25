/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.functions;

import static java.util.Objects.requireNonNull;
import static schemacrawler.tools.ai.utility.JsonUtility.mapper;
import static us.fatehi.utility.Utility.isBlank;

import java.util.List;
import java.util.Optional;
import schemacrawler.filter.NamedObjectFilter;
import schemacrawler.filter.NamedObjectFilters;
import schemacrawler.importance.model.DatabaseObjectVertexId;
import schemacrawler.importance.model.ImportanceModel;
import schemacrawler.importance.path.PathFinder;
import schemacrawler.importance.path.PathResult;
import schemacrawler.inclusionrule.RegularExpressionInclusionRule;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import schemacrawler.tools.ai.tools.JsonFunctionReturn;
import schemacrawler.tools.ai.tools.base.AbstractJsonFunctionExecutor;
import tools.jackson.databind.JsonNode;
import us.fatehi.utility.property.PropertyName;

public final class TablePathFunctionExecutor
    extends AbstractJsonFunctionExecutor<TablePathFunctionParameters> {

  protected TablePathFunctionExecutor(final PropertyName functionName) {
    super(functionName);
  }

  @Override
  public JsonFunctionReturn call() {
    final ImportanceModel importanceModel =
        requireNonNull(getImportanceModel(), "No importance model provided");

    final DatabaseObjectVertexId source =
        resolveTableVertexId(importanceModel, commandOptions.sourceTableName(), "source");
    final DatabaseObjectVertexId target =
        resolveTableVertexId(importanceModel, commandOptions.targetTableName(), "target");

    final PathResult pathResult =
        new PathFinder(importanceModel)
            .findShortestPath(source, target, commandOptions.maxPathDepth());
    final List<String> path =
        pathResult.path().stream()
            .map(importanceModel::lookupByVertexId)
            .map(Optional::orElseThrow)
            .map(DatabaseObject::getFullName)
            .toList();

    return new JsonFunctionReturn(
            mapper.<JsonNode>valueToTree(
                new TablePathDocument(path, pathResult.usesImpliedAssociations())))
        .withSummary("Returned a path with %d tables and views".formatted(path.size()));
  }

  @Override
  protected SchemaCrawlerOptions createSchemaCrawlerOptions() {
    return SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();
  }

  private DatabaseObjectVertexId resolveTableVertexId(
      final ImportanceModel importanceModel, final String patternText, final String role) {

    if (isBlank(patternText)) {
      throw new ExecutionRuntimeException("No %s table pattern provided".formatted(role));
    }

    // Preserve the exact-one-table contract while using case-insensitive, quote-tolerant full-name
    // matching.
    final NamedObjectFilter<DatabaseObject> tableFilter =
        NamedObjectFilters.fullName(new RegularExpressionInclusionRule(patternText));
    final List<DatabaseObjectVertexId> matchingVertexIds =
        importanceModel.getTableVertexIds().stream()
            .filter(
                vertexId -> {
                  final DatabaseObject object =
                      importanceModel.lookupByVertexId(vertexId).orElse(null);
                  return object instanceof Table && tableFilter.test(object);
                })
            .toList();

    if (matchingVertexIds.isEmpty()) {
      throw new ExecutionRuntimeException(
          "No table matches %s pattern: %s".formatted(role, patternText));
    }
    if (matchingVertexIds.size() > 1) {
      throw new ExecutionRuntimeException(
          "Multiple tables match %s pattern: %s".formatted(role, patternText));
    }

    return matchingVertexIds.getFirst();
  }
}
