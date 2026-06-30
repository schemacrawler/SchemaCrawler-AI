/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.functions;

import static schemacrawler.tools.ai.functions.DescribeTablesFunctionParameters.TableDescriptionScope.DEFAULT;

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
import schemacrawler.tools.ai.functions.DescribeTablesFunctionParameters.TableDescriptionScope;
import schemacrawler.tools.ai.model.AdditionalTableDetails;
import schemacrawler.tools.ai.model.CatalogDocument;
import schemacrawler.tools.ai.model.CompactCatalogBuilder;
import schemacrawler.tools.ai.tools.JsonFunctionReturn;
import schemacrawler.tools.ai.tools.base.AbstractJsonFunctionExecutor;
import us.fatehi.utility.property.PropertyName;

public final class DescribeTablesFunctionExecutor
    extends AbstractJsonFunctionExecutor<DescribeTablesFunctionParameters> {

  protected DescribeTablesFunctionExecutor(final PropertyName functionName) {
    super(functionName);
  }

  @Override
  public JsonFunctionReturn call() throws Exception {
    refilterCatalog();

    final Collection<AdditionalTableDetails> tableDetails = getTableDetails();
    final ERModel erModel = getERModel();
    final Catalog catalog = getCatalog();
    final CatalogDocument catalogDocument =
        CompactCatalogBuilder.builder(catalog, erModel)
            .withAdditionalTableDetails(tableDetails)
            .build();

    return new JsonFunctionReturn(catalogDocument)
        .withSummary("Returned %d tables".formatted(catalog.getTables().size()))
        .withNextSteps(describeTablesNextSteps(commandOptions.descriptionScope()));
  }

  private String describeTablesNextSteps(
      final Collection<TableDescriptionScope> descriptionScopes) {
    if (descriptionScopes == null
        || descriptionScopes.isEmpty()
        || descriptionScopes.stream().anyMatch(scope -> scope == null || scope == DEFAULT)) {
      return "Inspect table indexes next, because the table description output does not include"
                 + " index details.";
    }

    if (descriptionScopes.stream().anyMatch(scope -> scope == TableDescriptionScope.INDEXES)) {
      return "Inspect referenced tables, triggers, or objects that use this table next, because the"
                 + " current table details do not show downstream usage.";
    }

    if (descriptionScopes.stream()
        .anyMatch(scope -> scope == TableDescriptionScope.REFERENCED_TABLES)) {
      return "Inspect table indexes or triggers next, because those details are not included in the"
                 + " current result.";
    }

    if (descriptionScopes.stream().anyMatch(scope -> scope == TableDescriptionScope.TRIGGERS)) {
      return "Inspect referenced tables or objects that use this table next, because trigger"
                 + " behavior often depends on related objects.";
    }

    if (descriptionScopes.stream()
        .anyMatch(scope -> scope == TableDescriptionScope.USED_BY_OBJECTS)) {
      return "Inspect table indexes or referenced tables next, because the current result does not"
                 + " include those relationships.";
    }

    return "Refine the table filter or inspect related entities next, because the current selection"
               + " may be too broad.";
  }

  @Override
  protected SchemaCrawlerOptions createSchemaCrawlerOptions() {
    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeSynonyms(new ExcludeAll())
            .includeSequences(new ExcludeAll())
            .includeRoutines(new ExcludeAll())
            .includeTables(new IncludeAll());

    final InclusionRule grepTablesPattern = makeInclusionRule(commandOptions.tableName());
    final GrepOptionsBuilder grepOptionsBuilder =
        GrepOptionsBuilder.builder().includeGreppedTables(grepTablesPattern);

    return SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
        .withLimitOptions(limitOptionsBuilder.toOptions())
        .withGrepOptions(grepOptionsBuilder.toOptions());
  }

  private Collection<AdditionalTableDetails> getTableDetails() {
    final Collection<AdditionalTableDetails> tableDetails = new ArrayList<>();
    final Collection<TableDescriptionScope> descriptionScopes = commandOptions.descriptionScope();
    for (final TableDescriptionScope descriptionScope : descriptionScopes) {
      if (descriptionScope == null || descriptionScope == DEFAULT) {
        continue;
      }
      tableDetails.add(descriptionScope.toAdditionalTableDetails());
    }
    return tableDetails;
  }
}
