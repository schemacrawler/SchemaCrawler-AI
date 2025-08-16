/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.tools;

import static schemacrawler.tools.ai.utility.JsonUtility.mapper;
import java.io.StringWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.OutputOptionsBuilder;
import schemacrawler.utility.MetaDataUtility;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.datasource.DatabaseConnectionSources;
import us.fatehi.utility.property.PropertyName;

public abstract class AbstractExecutableFunctionExecutor<P extends FunctionParameters>
    extends AbstractSchemaCrawlerFunctionExecutor<P> {

  protected AbstractExecutableFunctionExecutor(final PropertyName functionName,
      final FunctionReturnType returnType) {
    super(functionName, returnType);
  }

  @Override
  public final FunctionReturn call() {

    final SchemaCrawlerExecutable executable = createExecutable();
    // Execute and generate output
    final StringWriter writer = new StringWriter();
    final String outputFormat = getFunctionReturnType().name();
    final OutputOptions outputOptions = OutputOptionsBuilder.builder().withOutputWriter(writer)
        .withOutputFormatValue(outputFormat).toOptions();

    executable.setOutputOptions(outputOptions);
    executable.setCatalog(catalog);
    executable.execute();

    if (!hasResults()) {
      if (getFunctionReturnType() == FunctionReturnType.TEXT) {
        return () -> "There were no matching results for your query.";
      }
      return () -> {
        final ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("message", "No results");
        return objectNode.toString();
      };
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
          DatabaseConnectionSources.fromConnection(connection);
      executable.setDataSource(databaseConnectionSource);
    }

    return executable;
  }
}
