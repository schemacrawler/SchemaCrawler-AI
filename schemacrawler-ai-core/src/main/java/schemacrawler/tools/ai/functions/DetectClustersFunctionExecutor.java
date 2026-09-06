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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import schemacrawler.importance.model.DatabaseObjectVertexId;
import schemacrawler.importance.model.SchemaGraphModel;
import schemacrawler.importance.model.TableCluster;
import schemacrawler.importance.report.ClusterReportEntry;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.tools.ai.tools.JsonFunctionReturn;
import schemacrawler.tools.ai.tools.base.AbstractJsonFunctionExecutor;
import tools.jackson.databind.JsonNode;
import us.fatehi.utility.property.PropertyName;

public final class DetectClustersFunctionExecutor
    extends AbstractJsonFunctionExecutor<DetectClustersFunctionParameters> {

  protected DetectClustersFunctionExecutor(final PropertyName functionName) {
    super(functionName);
  }

  @Override
  public JsonFunctionReturn call() {
    final SchemaGraphModel schemaGraphModel = requireSchemaGraphModel();
    final Pattern tableNamePattern = makeTableNamePattern(commandOptions.tableName());
    final List<ClusterReportEntry> communities = new ArrayList<>();
    for (final TableCluster tableCluster : schemaGraphModel.getTableClusters()) {
      final List<String> memberFullNames =
          tableCluster.memberVertexIds().stream()
              .map(vertexId -> getFullName(schemaGraphModel, vertexId))
              .toList();
      if (tableNamePattern != null
          && memberFullNames.stream()
              .noneMatch(fullName -> tableNamePattern.matcher(fullName).matches())) {
        continue;
      }

      final int maxCommunitySize = commandOptions.maxCommunitySize();
      final int memberLimit =
          maxCommunitySize > 0
              ? Math.min(maxCommunitySize, tableCluster.memberVertexIds().size())
              : tableCluster.memberVertexIds().size();
      communities.add(
          new ClusterReportEntry(
              tableCluster.id(),
              tableCluster.anchorVertexId(),
              getFullName(schemaGraphModel, tableCluster.anchorVertexId()),
              tableCluster.memberVertexIds().size(),
              tableCluster.memberVertexIds().subList(0, memberLimit),
              memberFullNames.subList(0, memberLimit)));

      if (commandOptions.maxCommunities() > 0
          && communities.size() >= commandOptions.maxCommunities()) {
        break;
      }
    }

    final DetectClustersDocument document = new DetectClustersDocument(communities);
    return new JsonFunctionReturn(mapper.<JsonNode>valueToTree(document))
        .withSummary("Returned %d schema communities".formatted(communities.size()));
  }

  @Override
  protected SchemaCrawlerOptions createSchemaCrawlerOptions() {
    return SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();
  }

  private String getFullName(
      final SchemaGraphModel schemaGraphModel, final DatabaseObjectVertexId vertexId) {
    final Optional<DatabaseObject> databaseObjectOptional =
        schemaGraphModel.lookupByVertexId(vertexId);
    return databaseObjectOptional.isEmpty()
        ? vertexId.key().toString()
        : databaseObjectOptional.get().getFullName();
  }

  private Pattern makeTableNamePattern(final String tableName) {
    return tableName == null || tableName.isBlank() ? null : Pattern.compile(tableName);
  }

  private SchemaGraphModel requireSchemaGraphModel() {
    return requireNonNull(getSchemaGraphModel(), "No schema graph model provided");
  }
}
