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

import static schemacrawler.tools.command.aichat.functions.json.TableDecriptionFunctionParameters.TableDescriptionScope.COLUMNS;
import static schemacrawler.tools.command.aichat.functions.json.TableDecriptionFunctionParameters.TableDescriptionScope.DEFAULT;
import static schemacrawler.tools.command.aichat.functions.json.TableDecriptionFunctionParameters.TableDescriptionScope.FOREIGN_KEYS;
import static schemacrawler.tools.command.aichat.functions.json.TableDecriptionFunctionParameters.TableDescriptionScope.INDEXES;
import static schemacrawler.tools.command.aichat.functions.json.TableDecriptionFunctionParameters.TableDescriptionScope.PRIMARY_KEY;
import static schemacrawler.tools.command.aichat.functions.json.TableDecriptionFunctionParameters.TableDescriptionScope.TRIGGERS;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import schemacrawler.inclusionrule.ExcludeAll;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.GrepOptionsBuilder;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.tools.command.aichat.functions.json.TableDecriptionFunctionParameters.TableDescriptionScope;
import schemacrawler.tools.command.aichat.functions.text.AbstractExecutableFunctionExecutor;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptionsBuilder;
import schemacrawler.tools.options.Config;
import us.fatehi.utility.property.PropertyName;

public final class TableDecriptionFunctionExecutor
    extends AbstractExecutableFunctionExecutor<TableDecriptionFunctionParameters> {

  protected TableDecriptionFunctionExecutor(final PropertyName functionName) {
    super(functionName);
  }

  @Override
  protected Config createAdditionalConfig() {
    final TableDescriptionScope scope = commandOptions.descriptionScope();
    final SchemaTextOptionsBuilder schemaTextOptionsBuilder = SchemaTextOptionsBuilder.builder();
    if (scope != DEFAULT) {
      if (scope != COLUMNS) {
        schemaTextOptionsBuilder.noTableColumns();
      } // fall through - no else
      if (scope != PRIMARY_KEY) {
        schemaTextOptionsBuilder.noPrimaryKeys();
      } // fall through - no else
      if (scope != FOREIGN_KEYS) {
        schemaTextOptionsBuilder.noForeignKeys();
        schemaTextOptionsBuilder.noWeakAssociations();
      } // fall through - no else
      if (scope != INDEXES) {
        schemaTextOptionsBuilder.noIndexes();
      } // fall through - no else
      if (scope != TRIGGERS) {
        schemaTextOptionsBuilder.noTriggers();
      } // fall through - no else
    }
    schemaTextOptionsBuilder.noTableConstraints().noAlternateKeys().noInfo();
    return schemaTextOptionsBuilder.toConfig();
  }

  @Override
  protected SchemaCrawlerOptions createSchemaCrawlerOptions() {
    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeSynonyms(new ExcludeAll())
            .includeSequences(new ExcludeAll())
            .includeRoutines(new ExcludeAll());
    final Pattern grepTablesPattern = makeNameInclusionPattern(commandOptions.tableName());
    final GrepOptionsBuilder grepOptionsBuilder =
        GrepOptionsBuilder.builder().includeGreppedTables(grepTablesPattern);
    return SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
        .withLimitOptions(limitOptionsBuilder.toOptions())
        .withGrepOptions(grepOptionsBuilder.toOptions());
  }

  @Override
  protected String getCommand() {
    return "schema";
  }

  @Override
  protected boolean hasResults() {
    final Collection<Table> tables = catalog.getTables();
    if (tables.isEmpty()) {
      return false;
    }
    final Pattern grepTablesPattern = makeNameInclusionPattern(commandOptions.tableName());
    final List<Table> greppedTables =
        tables.stream()
            .filter(table -> grepTablesPattern.matcher(table.getFullName()).matches())
            .collect(Collectors.toList());
    final TableDescriptionScope scope = commandOptions.descriptionScope();
    boolean hasResults = false;
    for (final Table table : greppedTables) {
      hasResults = hasData(table, scope);
      if (hasResults) {
        break;
      }
    }
    return hasResults;
  }

  private boolean hasData(final Table table, final TableDescriptionScope scope) {
    if (table == null) {
      return false;
    }
    return switch (scope) {
      case COLUMNS:
        yield !table.getColumns().isEmpty();
      case PRIMARY_KEY:
        yield table.hasPrimaryKey();
      case FOREIGN_KEYS:
        yield table.hasForeignKeys();
      case INDEXES:
        yield table.hasIndexes();
      case TRIGGERS:
        yield table.hasTriggers();
      default:
        yield true;
    };
  }
}
