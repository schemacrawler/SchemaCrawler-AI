/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.tools;

import static schemacrawler.tools.ai.utility.JsonUtility.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.inclusionrule.ExcludeAll;
import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.schemacrawler.GrepOptionsBuilder;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptionsBuilder;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.OutputOptionsBuilder;
import schemacrawler.utility.MetaDataUtility;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.datasource.DatabaseConnectionSources;
import us.fatehi.utility.property.PropertyName;
import us.fatehi.utility.string.StringFormat;

public abstract class AbstractExecutableFunctionExecutor<P extends FunctionParameters>
    extends AbstractFunctionExecutor<P> {

  private static final Logger LOGGER =
      Logger.getLogger(AbstractExecutableFunctionExecutor.class.getCanonicalName());

  protected AbstractExecutableFunctionExecutor(final PropertyName functionName) {
    super(functionName);
  }

  @Override
  public final FunctionReturn call() {

    final FunctionReturnType functionReturnType = commandOptions.getFunctionReturnType();

    final SchemaCrawlerExecutable executable = createExecutable();
    // Execute and generate output
    final StringWriter writer = new StringWriter();
    final String outputFormat = functionReturnType.name();
    final OutputOptions outputOptions =
        OutputOptionsBuilder.builder()
            .withOutputWriter(writer)
            .withOutputFormatValue(outputFormat)
            .toOptions();

    executable.setOutputOptions(outputOptions);
    executable.setCatalog(catalog);
    executable.execute();

    if (!hasResults()) {
      switch (functionReturnType) {
        case JSON:
          final ObjectNode objectNode = mapper.createObjectNode();
          objectNode.put("message", "No results");
          return new JsonFunctionReturn(objectNode);
        case TEXT:
        default:
          return new TextFunctionReturn("There were no matching results for your query.");
      }
    }

    final String results = writer.toString();
    switch (functionReturnType) {
      case JSON:
        try {
          final JsonNode node = mapper.readTree(results);
          return new JsonFunctionReturn(node);
        } catch (final Exception e) {
          LOGGER.log(
              Level.WARNING,
              e,
              new StringFormat(
                  "Could not convert results from <%s> to JSON", getCommandName().getName()));
          return new TextFunctionReturn(results);
        }
      case TEXT:
      default:
        return new TextFunctionReturn(results);
    }
  }

  @Override
  public boolean usesConnection() {
    return true;
  }

  protected Config createAdditionalConfig() {
    final SchemaTextOptionsBuilder schemaTextOptionsBuilder = SchemaTextOptionsBuilder.builder();
    return schemaTextOptionsBuilder.noInfo().toConfig();
  }

  @Override
  protected SchemaCrawlerOptions createSchemaCrawlerOptions() {
    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeSynonyms(new ExcludeAll())
            .includeSequences(new ExcludeAll())
            .includeRoutines(new ExcludeAll());
    final InclusionRule grepTablesPattern = grepTablesInclusionRule();
    final GrepOptionsBuilder grepOptionsBuilder =
        GrepOptionsBuilder.builder().includeGreppedTables(grepTablesPattern);
    return SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
        .withLimitOptions(limitOptionsBuilder.toOptions())
        .withGrepOptions(grepOptionsBuilder.toOptions());
  }

  protected abstract String getCommand();

  protected abstract InclusionRule grepTablesInclusionRule();

  protected boolean hasResults() {
    return !catalog.getTables().isEmpty();
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
          DatabaseConnectionSources.fromConnection(connection);
      executable.setDataSource(databaseConnectionSource);
    }

    return executable;
  }
}
