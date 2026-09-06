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

import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import schemacrawler.importance.model.DatabaseObjectVertexId;
import schemacrawler.importance.model.SchemaGraphModel;
import schemacrawler.importance.path.PathFinder;
import schemacrawler.importance.path.PathResult;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
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
    final SchemaGraphModel schemaGraphModel = requireSchemaGraphModel();
    final DatabaseObjectVertexId source =
        resolveTableNode(schemaGraphModel, commandOptions.sourceTableName(), "source");
    final DatabaseObjectVertexId target =
        resolveTableNode(schemaGraphModel, commandOptions.targetTableName(), "target");
    final PathResult pathResult =
        new PathFinder(schemaGraphModel)
            .findShortestPath(source, target, commandOptions.maxPathDepth());
    final List<String> path =
        pathResult.path().stream()
            .map(schemaGraphModel::lookupByVertexId)
            .map(java.util.Optional::orElseThrow)
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

  private SchemaGraphModel requireSchemaGraphModel() {
    return requireNonNull(getSchemaGraphModel(), "No schema graph model provided");
  }

  private DatabaseObjectVertexId resolveTableNode(
      final SchemaGraphModel schemaGraphModel, final String patternText, final String role) {
    if (patternText == null || patternText.isBlank()) {
      throw new IllegalArgumentException("No %s table pattern provided".formatted(role));
    }
    final Pattern pattern = Pattern.compile(patternText);
    final List<DatabaseObjectVertexId> matchingNodes =
        schemaGraphModel.getTableVertexIds().stream()
            .filter(
                vertexId -> {
                  final DatabaseObject object =
                      schemaGraphModel.lookupByVertexId(vertexId).orElse(null);
                  return object instanceof Table && pattern.matcher(object.getFullName()).matches();
                })
            .sorted(
                Comparator.comparing(
                    vertexId ->
                        schemaGraphModel.lookupByVertexId(vertexId).orElseThrow().getFullName()))
            .toList();
    if (matchingNodes.isEmpty()) {
      throw new IllegalArgumentException(
          "No table matches %s pattern: %s".formatted(role, patternText));
    }
    if (matchingNodes.size() > 1) {
      final String matches =
          matchingNodes.stream()
              .map(schemaGraphModel::lookupByVertexId)
              .map(java.util.Optional::orElseThrow)
              .map(DatabaseObject::getFullName)
              .reduce((first, second) -> "%s, %s".formatted(first, second))
              .orElse("");
      throw new IllegalArgumentException(
          "Multiple tables match %s pattern %s: %s".formatted(role, patternText, matches));
    }
    return matchingNodes.getFirst();
  }
}
