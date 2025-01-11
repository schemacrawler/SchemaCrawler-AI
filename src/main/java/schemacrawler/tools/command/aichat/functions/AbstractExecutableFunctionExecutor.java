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

import java.io.StringWriter;
import java.util.Collection;
import java.util.function.Function;
import java.util.regex.Pattern;
import static us.fatehi.utility.Utility.isBlank;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Schema;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.command.aichat.FunctionParameters;
import schemacrawler.tools.command.aichat.FunctionReturn;
import schemacrawler.tools.command.aichat.utility.ConnectionDatabaseConnectionSource;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.OutputOptionsBuilder;
import schemacrawler.utility.MetaDataUtility;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.property.PropertyName;

public abstract class AbstractExecutableFunctionExecutor<P extends FunctionParameters>
    extends AbstractFunctionExecutor<P> {

  protected AbstractExecutableFunctionExecutor(final PropertyName functionName) {
    super(functionName);
  }

  @Override
  public FunctionReturn call() {

    final SchemaCrawlerExecutable executable = createExecutable();
    final Function<Catalog, Boolean> resultsChecker = getResultsChecker();
    // Execute and generate output
    final StringWriter writer = new StringWriter();
    final OutputOptions outputOptions =
        OutputOptionsBuilder.builder().withOutputWriter(writer).toOptions();

    executable.setOutputOptions(outputOptions);
    executable.setCatalog(catalog);
    executable.execute();

    if (!resultsChecker.apply(executable.getCatalog())) {
      return new NoResultsReturn();
    }
    return () -> writer.toString();
  }

  protected abstract Config createAdditionalConfig();

  protected abstract SchemaCrawlerOptions createSchemaCrawlerOptions();

  protected abstract String getCommand();

  protected abstract Function<Catalog, Boolean> getResultsChecker();

  protected Pattern makeNameInclusionPattern(final String name) {
    if (isBlank(name)) {
      return Pattern.compile(".*");
    }
    final boolean hasDefaultSchema = hasDefaultSchema();
    return Pattern.compile(String.format(".*%s(?i)%s(?-i)", hasDefaultSchema ? "" : "\\.", name));
  }

  private SchemaCrawlerExecutable createExecutable() {

    final SchemaCrawlerOptions options = createSchemaCrawlerOptions();
    final Config config = createAdditionalConfig();
    final String command = getCommand();

    // Re-filter catalog
    MetaDataUtility.reduceCatalog(catalog, options);

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(command);
    executable.setSchemaCrawlerOptions(options);
    executable.setAdditionalConfiguration(config);
    executable.setCatalog(catalog);
    if (connection != null) {
      final DatabaseConnectionSource databaseConnectionSource =
          new ConnectionDatabaseConnectionSource(connection);
      executable.setDataSource(databaseConnectionSource);
    }

    return executable;
  }

  private boolean hasDefaultSchema() {
    final Collection<Schema> schemas = catalog.getSchemas();
    final int schemaCount = schemas.size();
    for (final Schema schema : schemas) {
      if (isBlank(schema.getFullName()) && schemaCount == 1) {
        return true;
      }
    }
    return false;
  }
}
