/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */


package schemacrawler.tools.command.aichat.functions.json;

import static schemacrawler.tools.command.aichat.functions.json.DescribeRoutinesFunctionParameters.RoutineDescriptionScope.DEFAULT;
import java.util.ArrayList;
import java.util.Collection;
import schemacrawler.inclusionrule.ExcludeAll;
import schemacrawler.inclusionrule.IncludeAll;
import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.schemacrawler.GrepOptionsBuilder;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.tools.command.aichat.functions.json.DescribeRoutinesFunctionParameters.RoutineDescriptionScope;
import schemacrawler.tools.command.aichat.tools.FunctionReturn;
import schemacrawler.tools.command.serialize.model.AdditionalRoutineDetails;
import schemacrawler.tools.command.serialize.model.CatalogDocument;
import schemacrawler.tools.command.serialize.model.CompactCatalogUtility;
import us.fatehi.utility.property.PropertyName;

public final class DescribeRoutinesFunctionExecutor
    extends AbstractJsonFunctionExecutor<DescribeRoutinesFunctionParameters> {

  protected DescribeRoutinesFunctionExecutor(final PropertyName functionName) {
    super(functionName);
  }

  @Override
  public FunctionReturn call() throws Exception {
    refilterCatalog();

    final Collection<AdditionalRoutineDetails> routineDetails = getRoutineDetails();
    final CatalogDocument catalogDocument =
        new CompactCatalogUtility()
            .withAdditionalRoutineDetails(routineDetails)
            .createCatalogDocument(catalog);
    return () -> catalogDocument.toString();
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
}
