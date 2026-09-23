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

import java.util.List;
import schemacrawler.importance.model.ImportanceModel;
import schemacrawler.importance.options.ImportanceOptions;
import schemacrawler.importance.options.ImportanceOptionsBuilder;
import schemacrawler.importance.report.ClusterReportEntry;
import schemacrawler.importance.report.ImportanceReportGenerator;
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
    final ImportanceModel requireImportanceModel =
        requireNonNull(getImportanceModel(), "No importance model provided");
    final ImportanceOptions importanceOptions =
        ImportanceOptionsBuilder.builder()
            .withTableInclusionRule(makeInclusionRule(commandOptions.tableName()))
            .withMaxClusters(commandOptions.maxCommunities())
            .withMaxClusterSize(commandOptions.maxCommunitySize())
            .toOptions();
    // Quote-tolerant matching, cluster-membership filtering, and result limiting are all
    // handled by the report generator, shared with table importance reporting.
    final List<ClusterReportEntry> communities =
        new ImportanceReportGenerator(requireImportanceModel).report(importanceOptions).clusters();

    final DetectClustersDocument document = new DetectClustersDocument(communities);
    return new JsonFunctionReturn(mapper.<JsonNode>valueToTree(document))
        .withSummary("Returned %d schema communities".formatted(communities.size()));
  }

  @Override
  protected SchemaCrawlerOptions createSchemaCrawlerOptions() {
    return SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();
  }
}
