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

package schemacrawler.tools.command.chatgpt.functions;

import java.util.function.Function;
import java.util.regex.Pattern;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import schemacrawler.inclusionrule.ExcludeAll;
import schemacrawler.inclusionrule.RegularExpressionInclusionRule;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptionsBuilder;
import schemacrawler.tools.options.Config;

public final class DatabaseObjectDescriptionFunctionDefinition
    extends AbstractExecutableFunctionDefinition {

  public enum DatabaseObjectsScope {
    NONE,
    SEQUENCES,
    SYNONYMS,
    ROUTINES,
    ;
  }

  @JsonPropertyDescription("Name of database object to describe.")
  private String databaseObjectName;

  @JsonPropertyDescription(
      "Indicates what details of database objects to show - sequences, synonyms, or routines (that is, stored procedures or functions).")
  private DatabaseObjectsScope databaseObjectsScope;

  public String getDatabaseObjectName() {
    return databaseObjectName;
  }

  public DatabaseObjectsScope getDatabaseObjectsScope() {
    if (databaseObjectsScope == null) {
      return DatabaseObjectsScope.NONE;
    }
    return databaseObjectsScope;
  }

  @Override
  public String getDescription() {
    return "Gets the details and description of database objects like routines (that is, functions and stored procedures), sequences, or synonyms.";
  }

  public void setDatabaseObjectName(final String databaseObjectName) {
    this.databaseObjectName = databaseObjectName;
  }

  public void setDatabaseObjectsScope(final DatabaseObjectsScope databaseObjectsScope) {
    this.databaseObjectsScope = databaseObjectsScope;
  }

  @Override
  protected Config createAdditionalConfig() {
    final DatabaseObjectsScope scope = getDatabaseObjectsScope();
    final SchemaTextOptionsBuilder schemaTextOptionsBuilder = SchemaTextOptionsBuilder.builder();
    schemaTextOptionsBuilder.noSequences();
    schemaTextOptionsBuilder.noSynonyms();
    schemaTextOptionsBuilder.noRoutines();

    switch (scope) {
      case SEQUENCES:
        schemaTextOptionsBuilder.noSequences(false);
        break;
      case SYNONYMS:
        schemaTextOptionsBuilder.noSynonyms(false);
        break;
      case ROUTINES:
        schemaTextOptionsBuilder.noRoutines(false);
        break;
      default:
        // No action
    }
    schemaTextOptionsBuilder.noInfo();
    return schemaTextOptionsBuilder.toConfig();
  }

  @Override
  protected SchemaCrawlerOptions createSchemaCrawlerOptions() {

    final Pattern inclusionPattern = makeNameInclusionPattern(getDatabaseObjectName());
    final DatabaseObjectsScope scope = getDatabaseObjectsScope();
    final LimitOptionsBuilder limitOptionsBuilder = LimitOptionsBuilder.builder();
    switch (scope) {
      case SEQUENCES:
        limitOptionsBuilder.includeSequences(new RegularExpressionInclusionRule(inclusionPattern));
        break;
      case SYNONYMS:
        limitOptionsBuilder.includeSynonyms(new RegularExpressionInclusionRule(inclusionPattern));
        break;
      case ROUTINES:
        limitOptionsBuilder.includeRoutines(new RegularExpressionInclusionRule(inclusionPattern));
        break;
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
  protected Function<Catalog, Boolean> getResultsChecker() {
    final DatabaseObjectsScope scope = getDatabaseObjectsScope();
    switch (scope) {
      case SEQUENCES:
        return catalog -> !catalog.getSequences().isEmpty();
      // Return, not break
      case SYNONYMS:
        return catalog -> !catalog.getSynonyms().isEmpty();
      // Return, not break
      case ROUTINES:
        return catalog -> !catalog.getRoutines().isEmpty();
      // Return, not break
      default:
        return catalog -> false;
    }
  }
}
