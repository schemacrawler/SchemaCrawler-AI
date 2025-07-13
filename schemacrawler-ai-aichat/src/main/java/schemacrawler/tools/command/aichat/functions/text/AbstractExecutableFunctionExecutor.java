/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.command.aichat.functions.text;

import java.io.StringWriter;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.ai.tools.AbstractSchemaCrawlerFunctionExecutor;
import schemacrawler.tools.ai.tools.FunctionParameters;
import schemacrawler.tools.ai.tools.FunctionReturn;
import schemacrawler.tools.command.aichat.utility.ConnectionDatabaseConnectionSource;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.OutputOptionsBuilder;
import schemacrawler.utility.MetaDataUtility;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.property.PropertyName;

public abstract class AbstractExecutableFunctionExecutor<P extends FunctionParameters>
    extends AbstractSchemaCrawlerFunctionExecutor<P> {

  protected AbstractExecutableFunctionExecutor(final PropertyName functionName) {
    super(functionName);
  }

  @Override
  public final FunctionReturn call() {

    final SchemaCrawlerExecutable executable = createExecutable();
    // Execute and generate output
    final StringWriter writer = new StringWriter();
    final OutputOptions outputOptions =
        OutputOptionsBuilder.builder().withOutputWriter(writer).toOptions();

    executable.setOutputOptions(outputOptions);
    executable.setCatalog(catalog);
    executable.execute();

    if (!hasResults()) {
      return new NoResultsReturn();
    }
    return () -> writer.toString();
  }

  protected abstract Config createAdditionalConfig();

  protected abstract String getCommand();

  protected abstract boolean hasResults();

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
}
