/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.tools.base;

import static schemacrawler.tools.ai.utility.JsonUtility.mapper;
import static us.fatehi.utility.Utility.isBlank;
import static us.fatehi.utility.Utility.requireNotBlank;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.inclusionrule.ExcludeAll;
import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.schemacrawler.GrepOptionsBuilder;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.exceptions.IORuntimeException;
import schemacrawler.tools.ai.tools.ExceptionFunctionReturn;
import schemacrawler.tools.ai.tools.FunctionParameters;
import schemacrawler.tools.ai.tools.FunctionReturn;
import schemacrawler.tools.ai.tools.FunctionReturnType;
import schemacrawler.tools.ai.tools.JsonFunctionReturn;
import schemacrawler.tools.ai.tools.NoResultsFunctionReturn;
import schemacrawler.tools.ai.tools.TextFunctionReturn;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptionsBuilder;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.OutputOptionsBuilder;
import schemacrawler.utility.MetaDataUtility;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ObjectNode;
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
  public boolean usesConnection() {
    return true;
  }

  @Override
  protected final SchemaCrawlerOptions createSchemaCrawlerOptions() {
    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeSynonyms(new ExcludeAll())
            .includeSequences(new ExcludeAll())
            .includeRoutines(new ExcludeAll());
    final InclusionRule grepTablesInclusionRule = grepTablesInclusionRule();
    final GrepOptionsBuilder grepOptionsBuilder =
        GrepOptionsBuilder.builder().includeGreppedTables(grepTablesInclusionRule);
    return SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
        .withLimitOptions(limitOptionsBuilder.toOptions())
        .withGrepOptions(grepOptionsBuilder.toOptions());
  }

  protected final Path execute(
      final String command, final Config additionalConfig, final String outputFormat) {

    requireNotBlank(command, "No command provided");

    final String outputFormatValue;
    if (isBlank(outputFormat)) {
      outputFormatValue = "txt";
    } else {
      outputFormatValue = outputFormat.strip();
    }

    final Path outputFilePath =
        Paths.get(System.getProperty("java.io.tmpdir"))
            .resolve(UUID.randomUUID().toString() + "." + outputFormat);

    final OutputOptions outputOptions = createOutputOptions(outputFormatValue, outputFilePath);

    final Config config = SchemaTextOptionsBuilder.builder().noInfo().toConfig();
    config.merge(additionalConfig);

    final SchemaCrawlerExecutable executable = createExecutable(command);
    executable.setOutputOptions(outputOptions);
    executable.setAdditionalConfiguration(config);
    executable.execute();

    return outputFilePath;
  }

  protected abstract InclusionRule grepTablesInclusionRule();

  protected boolean hasResults() {
    return !catalog.getTables().isEmpty();
  }

  protected final FunctionReturn returnFileUrl(final Path outputFilePath) {
    if (!outputFileHasResults(outputFilePath)) {
      return new NoResultsFunctionReturn();
    }

    final ObjectNode node = mapper.createObjectNode();
    node.put("url-path", "/temp/" + outputFilePath.getFileName());
    node.put(
        "instructions",
        """
        The output is file that is available from a web URL.
        Construct the URL using the external host and port of
        the MCP server, and add the "url-path" to it.
        Instruct the user to obtain the output from that URL.
        """);

    return new JsonFunctionReturn(node);
  }

  protected final FunctionReturn returnJson(final Path outputFilePath) {
    if (!outputFileHasResults(outputFilePath)) {
      return new NoResultsFunctionReturn();
    }

    try {
      String results = Files.readString(outputFilePath);
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
    } catch (IOException e) {
      return new ExceptionFunctionReturn(e);
    }
  }

  private SchemaCrawlerExecutable createExecutable(final String command) {

    final SchemaCrawlerOptions options = createSchemaCrawlerOptions();

    // Re-filter catalog
    MetaDataUtility.reduceCatalog(catalog, options);

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(command);
    executable.setSchemaCrawlerOptions(options);
    executable.setCatalog(catalog);
    if (connection != null) {
      final DatabaseConnectionSource databaseConnectionSource =
          DatabaseConnectionSources.fromConnection(connection);
      executable.setDataSource(databaseConnectionSource);
    }

    return executable;
  }

  private OutputOptions createOutputOptions(final String outputFormatValue, final Path filePath) {
    Writer writer;
    try {
      writer =
          Files.newBufferedWriter(
              filePath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    } catch (IOException e) {
      throw new IORuntimeException("Could not create writer for temporary file", e);
    }
    final OutputOptions outputOptions =
        OutputOptionsBuilder.builder()
            .withOutputWriter(writer)
            .withOutputFormatValue(outputFormatValue)
            .toOptions();
    return outputOptions;
  }

  private boolean outputFileHasResults(final Path outputFilePath) {
    if (outputFilePath == null
        || !Files.exists(outputFilePath)
        || !Files.isRegularFile(outputFilePath)
        || !Files.isReadable(outputFilePath)) {
      return false;
    }

    try {
      if (!hasResults() || Files.size(outputFilePath) == 0) {
        return false;
      }
    } catch (IOException e) {
      LOGGER.log(Level.FINE, "Could not detemine results file length", e);
      return false;
    }

    final FunctionReturnType functionReturnType = commandOptions.getFunctionReturnType();
    if (functionReturnType != FunctionReturnType.JSON) {
      return false;
    }
    return true;
  }
}
