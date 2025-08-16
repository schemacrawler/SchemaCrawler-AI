/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.command.aichat.functions.text;

import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.tools.ai.tools.AbstractExecutableFunctionExecutor;
import schemacrawler.tools.ai.tools.FunctionReturnType;
import schemacrawler.tools.command.aichat.functions.text.DatabaseObjectDescriptionFunctionParameters.DatabaseObjectsScope;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptionsBuilder;
import schemacrawler.tools.options.Config;
import us.fatehi.utility.property.PropertyName;

public final class DatabaseObjectDescriptionFunctionExecutor
    extends AbstractExecutableFunctionExecutor<DatabaseObjectDescriptionFunctionParameters> {

  DatabaseObjectDescriptionFunctionExecutor(
      final PropertyName functionName, final FunctionReturnType returnType) {
    super(functionName, returnType);
  }

  @Override
  protected Config createAdditionalConfig() {
    final DatabaseObjectsScope scope = commandOptions.databaseObjectsScope();
    final SchemaTextOptionsBuilder schemaTextOptionsBuilder = SchemaTextOptionsBuilder.builder();

    schemaTextOptionsBuilder.noInfo();
    // First turn off output of all database objects
    schemaTextOptionsBuilder.noTables();
    schemaTextOptionsBuilder.noRoutines();
    schemaTextOptionsBuilder.noSequences();
    schemaTextOptionsBuilder.noSynonyms();

    // Next, turn on the ones that are needed
    switch (scope) {
      case TABLES:
        schemaTextOptionsBuilder.noTables(false);
        break;
      case ROUTINES:
        schemaTextOptionsBuilder.noRoutines(false);
        break;
      case SEQUENCES:
        schemaTextOptionsBuilder.noSequences(false);
        break;
      case SYNONYMS:
        schemaTextOptionsBuilder.noSynonyms(false);
        break;
      default:
        // No action
    }

    return schemaTextOptionsBuilder.toConfig();
  }

  @Override
  protected SchemaCrawlerOptions createSchemaCrawlerOptions() {

    final InclusionRule inclusionPattern = makeInclusionRule(commandOptions.databaseObjectName());
    final DatabaseObjectsScope scope = commandOptions.databaseObjectsScope();
    final LimitOptionsBuilder limitOptionsBuilder = LimitOptionsBuilder.builder();
    switch (scope) {
      case TABLES:
        limitOptionsBuilder.includeTables(inclusionPattern);
        break;
      case ROUTINES:
        limitOptionsBuilder.includeRoutines(inclusionPattern);
        break;
      case SEQUENCES:
        limitOptionsBuilder.includeSequences(inclusionPattern);
        break;
      case SYNONYMS:
        limitOptionsBuilder.includeSynonyms(inclusionPattern);
        break;
      default:
        // No action
    }

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
      case TABLES:
        return !catalog.getTables().isEmpty();
      case ROUTINES:
        return !catalog.getRoutines().isEmpty();
      case SEQUENCES:
        return !catalog.getSequences().isEmpty();
      case SYNONYMS:
        return !catalog.getSynonyms().isEmpty();
      default:
        return false;
    }
  }
}
