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

package schemacrawler.tools.command.aichat.functions;

import static schemacrawler.tools.command.aichat.functions.DatabaseObjectDescriptionFunctionParameters.DatabaseObjectsScope.ROUTINES;
import static schemacrawler.tools.command.aichat.functions.DatabaseObjectDescriptionFunctionParameters.DatabaseObjectsScope.SEQUENCES;
import static schemacrawler.tools.command.aichat.functions.DatabaseObjectDescriptionFunctionParameters.DatabaseObjectsScope.SYNONYMS;
import java.util.regex.Pattern;
import schemacrawler.inclusionrule.ExcludeAll;
import schemacrawler.inclusionrule.RegularExpressionInclusionRule;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.tools.command.aichat.functions.DatabaseObjectDescriptionFunctionParameters.DatabaseObjectsScope;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptionsBuilder;
import schemacrawler.tools.options.Config;
import us.fatehi.utility.property.PropertyName;

public final class DatabaseObjectDescriptionFunctionExecutor
    extends AbstractExecutableFunctionExecutor<DatabaseObjectDescriptionFunctionParameters> {

  DatabaseObjectDescriptionFunctionExecutor(final PropertyName functionName) {
    super(functionName);
  }

  @Override
  protected Config createAdditionalConfig() {
    final DatabaseObjectsScope scope = commandOptions.databaseObjectsScope();
    final SchemaTextOptionsBuilder schemaTextOptionsBuilder = SchemaTextOptionsBuilder.builder();
    if (scope != SEQUENCES) {
      schemaTextOptionsBuilder.noSequences();
    } // fall through - no else
    if (scope != SYNONYMS) {
      schemaTextOptionsBuilder.noSynonyms();
    } // fall through - no else
    if (scope != ROUTINES) {
      schemaTextOptionsBuilder.noRoutines();
    } // fall through - no else
    schemaTextOptionsBuilder.noInfo();
    return schemaTextOptionsBuilder.toConfig();
  }

  @Override
  protected SchemaCrawlerOptions createSchemaCrawlerOptions() {

    final Pattern inclusionPattern = makeNameInclusionPattern(commandOptions.databaseObjectName());
    final DatabaseObjectsScope scope = commandOptions.databaseObjectsScope();
    final LimitOptionsBuilder limitOptionsBuilder = LimitOptionsBuilder.builder();
    switch (scope) {
      case SEQUENCES:
        limitOptionsBuilder.includeSequences(new RegularExpressionInclusionRule(inclusionPattern));
      case SYNONYMS:
        limitOptionsBuilder.includeSynonyms(new RegularExpressionInclusionRule(inclusionPattern));
      case ROUTINES:
        limitOptionsBuilder.includeRoutines(new RegularExpressionInclusionRule(inclusionPattern));
      default:
        // No action
    }
    limitOptionsBuilder.includeTables(new ExcludeAll());

    return SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
        .withLimitOptions(limitOptionsBuilder.toOptions());
  }

  @Override
  protected String getCommand() {
    return "schema";
  }

  @Override
  protected boolean hasResults() {
    final DatabaseObjectsScope scope = commandOptions.databaseObjectsScope();
    switch (scope) {
      case SEQUENCES:
        return !catalog.getSequences().isEmpty();
      case SYNONYMS:
        return !catalog.getSynonyms().isEmpty();
      case ROUTINES:
        return !catalog.getRoutines().isEmpty();
      default:
        return false;
    }
  }
}
