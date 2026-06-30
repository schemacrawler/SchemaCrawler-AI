/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.functions;

import static schemacrawler.tools.ai.functions.DescribeRoutinesFunctionParameters.RoutineDescriptionScope.DEFAULT;

import java.util.ArrayList;
import java.util.Collection;
import schemacrawler.ermodel.model.ERModel;
import schemacrawler.inclusionrule.ExcludeAll;
import schemacrawler.inclusionrule.IncludeAll;
import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.GrepOptionsBuilder;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.tools.ai.functions.DescribeRoutinesFunctionParameters.RoutineDescriptionScope;
import schemacrawler.tools.ai.model.AdditionalRoutineDetails;
import schemacrawler.tools.ai.model.CatalogDocument;
import schemacrawler.tools.ai.model.CompactCatalogBuilder;
import schemacrawler.tools.ai.tools.JsonFunctionReturn;
import schemacrawler.tools.ai.tools.base.AbstractJsonFunctionExecutor;
import us.fatehi.utility.property.PropertyName;

public final class DescribeRoutinesFunctionExecutor
    extends AbstractJsonFunctionExecutor<DescribeRoutinesFunctionParameters> {

  protected DescribeRoutinesFunctionExecutor(final PropertyName functionName) {
    super(functionName);
  }

  @Override
  public JsonFunctionReturn call() throws Exception {
    refilterCatalog();

    final Collection<AdditionalRoutineDetails> routineDetails = getRoutineDetails();
    final ERModel erModel = getERModel();
    final Catalog catalog = getCatalog();
    final CatalogDocument catalogDocument =
        CompactCatalogBuilder.builder(catalog, erModel)
            .withAdditionalRoutineDetails(routineDetails)
            .build();

    return new JsonFunctionReturn(catalogDocument)
        .withSummary("Returned %d routines".formatted(catalog.getRoutines().size()))
        .withNextSteps(describeRoutinesNextSteps(commandOptions.descriptionScope()));
  }

  private String describeRoutinesNextSteps(
      final Collection<RoutineDescriptionScope> descriptionScopes) {
    if (descriptionScopes == null
        || descriptionScopes.isEmpty()
        || descriptionScopes.stream().anyMatch(scope -> scope == null || scope == DEFAULT)) {
      return "Inspect routine attributes or referenced objects next.";
    }

    if (descriptionScopes.stream()
        .anyMatch(scope -> scope == RoutineDescriptionScope.REFERENCED_OBJECTS)) {
      return "Inspect the related tables or views next.";
    }

    if (descriptionScopes.stream().anyMatch(scope -> scope == RoutineDescriptionScope.ATTRIBUTES)) {
      return "Inspect the referenced objects for the same routines next.";
    }

    if (descriptionScopes.stream()
        .anyMatch(scope -> scope == RoutineDescriptionScope.DEFINIITION)) {
      return "Inspect routine attributes or referenced objects for more context next.";
    }

    return "Refine the routine filter or inspect related tables next.";
  }

  @Override
  protected SchemaCrawlerOptions createSchemaCrawlerOptions() {
    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeSynonyms(new ExcludeAll())
            .includeSequences(new ExcludeAll())
            .includeRoutines(new IncludeAll())
            .includeTables(new ExcludeAll());
    final InclusionRule grepRoutinesParametersPattern =
        makeInclusionRule(commandOptions.routineName());
    final GrepOptionsBuilder grepOptionsBuilder =
        GrepOptionsBuilder.builder().includeGreppedRoutineParameters(grepRoutinesParametersPattern);
    return SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
        .withLimitOptions(limitOptionsBuilder.toOptions())
        .withGrepOptions(grepOptionsBuilder.toOptions());
  }

  private Collection<AdditionalRoutineDetails> getRoutineDetails() {
    final Collection<AdditionalRoutineDetails> routineDetails = new ArrayList<>();
    final Collection<RoutineDescriptionScope> descriptionScopes = commandOptions.descriptionScope();
    for (final RoutineDescriptionScope descriptionScope : descriptionScopes) {
      if (descriptionScope == null || descriptionScope == DEFAULT) {
        continue;
      }
      routineDetails.add(descriptionScope.toAdditionalRoutineDetails());
    }
    return routineDetails;
  }
}
