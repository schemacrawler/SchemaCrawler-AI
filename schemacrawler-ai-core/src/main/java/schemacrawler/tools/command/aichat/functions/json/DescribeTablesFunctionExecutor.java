/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.tools.command.aichat.functions.json;

import static schemacrawler.tools.command.aichat.functions.json.DescribeTablesFunctionParameters.TableDescriptionScope.DEFAULT;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;
import schemacrawler.inclusionrule.ExcludeAll;
import schemacrawler.schemacrawler.GrepOptionsBuilder;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.tools.command.aichat.functions.json.DescribeTablesFunctionParameters.TableDescriptionScope;
import schemacrawler.tools.command.aichat.tools.FunctionReturn;
import schemacrawler.tools.command.serialize.model.AdditionalTableDetails;
import schemacrawler.tools.command.serialize.model.CatalogDocument;
import schemacrawler.tools.command.serialize.model.CompactCatalogUtility;
import us.fatehi.utility.property.PropertyName;

public final class DescribeTablesFunctionExecutor
    extends AbstractJsonFunctionExecutor<DescribeTablesFunctionParameters> {

  protected DescribeTablesFunctionExecutor(final PropertyName functionName) {
    super(functionName);
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
    final Pattern grepTablesPattern =
        makeNameInclusionPattern(commandOptions.tableNameRegularExpression());
    final GrepOptionsBuilder grepOptionsBuilder =
        GrepOptionsBuilder.builder().includeGreppedTables(grepTablesPattern);
    return SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
        .withLimitOptions(limitOptionsBuilder.toOptions())
        .withGrepOptions(grepOptionsBuilder.toOptions());
  }
}
