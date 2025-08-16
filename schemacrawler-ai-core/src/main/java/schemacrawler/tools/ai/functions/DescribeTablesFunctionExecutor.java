/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.functions;

import static schemacrawler.tools.ai.functions.DescribeTablesFunctionParameters.TableDescriptionScope.DEFAULT;

import java.util.ArrayList;
import java.util.Collection;
import schemacrawler.inclusionrule.ExcludeAll;
import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.schemacrawler.GrepOptionsBuilder;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.tools.ai.functions.DescribeTablesFunctionParameters.TableDescriptionScope;
import schemacrawler.tools.ai.model.AdditionalTableDetails;
import schemacrawler.tools.ai.model.CatalogDocument;
import schemacrawler.tools.ai.model.CompactCatalogUtility;
import schemacrawler.tools.ai.tools.FunctionReturn;
import schemacrawler.tools.ai.tools.FunctionReturnType;
import us.fatehi.utility.property.PropertyName;

public final class DescribeTablesFunctionExecutor
    extends AbstractJsonFunctionExecutor<DescribeTablesFunctionParameters> {

  protected DescribeTablesFunctionExecutor(
      final PropertyName functionName, final FunctionReturnType returnType) {
    super(functionName, returnType);
  }

  @Override
  public FunctionReturn call() throws Exception {
    refilterCatalog();

    final Collection<AdditionalTableDetails> tableDetails = getTableDetails();
    final CatalogDocument catalogDocument =
        new CompactCatalogUtility()
            .withAdditionalTableDetails(tableDetails)
            .createCatalogDocument(catalog);
    return () -> catalogDocument.toString();
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

  @Override
  protected SchemaCrawlerOptions createSchemaCrawlerOptions() {
    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeSynonyms(new ExcludeAll())
            .includeSequences(new ExcludeAll())
            .includeRoutines(new ExcludeAll());
    final InclusionRule grepTablesPattern = makeInclusionRule(commandOptions.tableName());
    final GrepOptionsBuilder grepOptionsBuilder =
        GrepOptionsBuilder.builder().includeGreppedTables(grepTablesPattern);
    return SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
        .withLimitOptions(limitOptionsBuilder.toOptions())
        .withGrepOptions(grepOptionsBuilder.toOptions());
  }
}
