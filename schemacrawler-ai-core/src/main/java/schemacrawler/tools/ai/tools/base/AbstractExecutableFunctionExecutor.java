/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.tools.base;

import static java.util.Objects.requireNonNull;
import static schemacrawler.tools.ai.utility.JsonUtility.mapper;

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
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.exceptions.IORuntimeException;
import schemacrawler.tools.ai.tools.ExceptionFunctionReturn;
import schemacrawler.tools.ai.tools.FunctionParameters;
import schemacrawler.tools.ai.tools.FunctionReturn;
import schemacrawler.tools.ai.tools.JsonFunctionReturn;
import schemacrawler.tools.ai.tools.NoResultsFunctionReturn;
import schemacrawler.tools.ai.tools.TextFunctionReturn;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptionsBuilder;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.OutputOptionsBuilder;
import schemacrawler.utility.MetaDataUtility;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.datasource.DatabaseConnectionSources;
import us.fatehi.utility.property.PropertyName;

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

  protected final Path execute(final ExecutionParameters executionParameters) {

    requireNonNull(executionParameters, "No execution parameters provided");

    // Crate SchemaCrawler options
    final SchemaCrawlerOptions options = adjustSchemaCrawlerOptions();

    // Re-filter catalog
    MetaDataUtility.reduceCatalog(catalog, options);

    // Create output file path
    final String outputFormatValue = executionParameters.outputFormat();
    final Path outputFilePath =
        Paths.get(System.getProperty("java.io.tmpdir"))
            .resolve(UUID.randomUUID().toString() + "." + outputFormatValue);

    // Create output options
    final OutputOptions outputOptions = createOutputOptions(outputFormatValue, outputFilePath);

    // Create additional config
    final Config config = createAdditionalConfig(executionParameters.additionalConfig());

    // Create executable
    final SchemaCrawlerExecutable executable =
        new SchemaCrawlerExecutable(executionParameters.command());
    executable.setSchemaCrawlerOptions(options);
    executable.setCatalog(catalog);
    if (connection != null) {
      final DatabaseConnectionSource databaseConnectionSource =
          DatabaseConnectionSources.fromConnection(connection);
      executable.setDataSource(databaseConnectionSource);
    }
    executable.setOutputOptions(outputOptions);
    executable.setAdditionalConfiguration(config);
    executable.execute();

    return outputFilePath;
  }

  protected boolean hasResults() {
    return !catalog.getTables().isEmpty();
  }

  protected final FunctionReturn returnJson(final Path outputFilePath) {
    if (!outputFileHasResults(outputFilePath)) {
      return new NoResultsFunctionReturn();
    }

    try {
      final String results = Files.readString(outputFilePath);
      try {
        final JsonNode node = mapper.readTree(results);
        return new JsonFunctionReturn(node);
      } catch (final JacksonException e) {
        LOGGER.log(
            Level.WARNING,
            "Could not convert results from <%s> to JSON".formatted(getCommandName().getName()),
            e);
        return new TextFunctionReturn(results);
      }
    } catch (final Exception e) {
      return new ExceptionFunctionReturn(e);
    } finally {
      deleteTempFile(outputFilePath);
    }
  }

  protected final FunctionReturn returnText(final Path outputFilePath) {
    if (!outputFileHasResults(outputFilePath)) {
      return new NoResultsFunctionReturn();
    }

    try {
      final String results = Files.readString(outputFilePath);
      return new TextFunctionReturn(results);
    } catch (IOException e) {
      return new ExceptionFunctionReturn(e);
    } finally {
      deleteTempFile(outputFilePath);
    }
  }

  private final SchemaCrawlerOptions adjustSchemaCrawlerOptions() {

    final SchemaCrawlerOptions baseOptions = createSchemaCrawlerOptions();
    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeSynonyms(new ExcludeAll())
            .includeSequences(new ExcludeAll())
            .includeRoutines(new ExcludeAll());
    SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLimitOptions(limitOptionsBuilder.toOptions());
    if (baseOptions != null) {
      schemaCrawlerOptions =
          schemaCrawlerOptions
              .withFilterOptions(baseOptions.filterOptions())
              .withGrepOptions(baseOptions.grepOptions());
    }
    return schemaCrawlerOptions;
  }

  private Config createAdditionalConfig(final Config additionalConfig) {
    final Config config = SchemaTextOptionsBuilder.builder().noInfo().toConfig();
    config.merge(additionalConfig);
    return config;
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

  private void deleteTempFile(final Path outputFilePath) {
    if (outputFilePath == null) {
      return;
    }
    try {
      Files.deleteIfExists(outputFilePath);
    } catch (IOException e) {
      LOGGER.log(
          Level.WARNING, "Could not delete temporary file <%s>".formatted(outputFilePath), e);
    }
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

    return true;
  }
}
