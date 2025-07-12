/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */


package schemacrawler.tools.command.aichat.functions.text;

import static schemacrawler.tools.command.aichat.options.DatabaseObjectType.ALL;
import static schemacrawler.tools.command.aichat.options.DatabaseObjectType.ROUTINES;
import static schemacrawler.tools.command.aichat.options.DatabaseObjectType.SEQUENCES;
import static schemacrawler.tools.command.aichat.options.DatabaseObjectType.SYNONYMS;
import static schemacrawler.tools.command.aichat.options.DatabaseObjectType.TABLES;
import schemacrawler.inclusionrule.ExcludeAll;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.tools.command.aichat.options.DatabaseObjectType;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptionsBuilder;
import schemacrawler.tools.options.Config;
import us.fatehi.utility.property.PropertyName;

public final class DatabaseObjectListFunctionExecutor
    extends AbstractExecutableFunctionExecutor<DatabaseObjectListFunctionParameters> {

  protected DatabaseObjectListFunctionExecutor(final PropertyName functionName) {
    super(functionName);
  }

  @Override
  protected Config createAdditionalConfig() {
    final DatabaseObjectType databaseObjectType = commandOptions.databaseObjectType();
    final SchemaTextOptionsBuilder schemaTextOptionsBuilder = SchemaTextOptionsBuilder.builder();
    if (databaseObjectType != ALL) {
      if (databaseObjectType != TABLES) {
        schemaTextOptionsBuilder.noTables();
      } // fall through - no else
      if (databaseObjectType != ROUTINES) {
        schemaTextOptionsBuilder.noRoutines();
      } // fall through - no else
      if (databaseObjectType != SEQUENCES) {
        schemaTextOptionsBuilder.noSequences();
      } // fall through - no else
      if (databaseObjectType != SYNONYMS) {
        schemaTextOptionsBuilder.noSynonyms();
      } // fall through - no else
    }
    schemaTextOptionsBuilder.noInfo();
    return schemaTextOptionsBuilder.toConfig();
  }

  @Override
  protected SchemaCrawlerOptions createSchemaCrawlerOptions() {
    final DatabaseObjectType databaseObjectType = commandOptions.databaseObjectType();
    final LimitOptionsBuilder limitOptionsBuilder = LimitOptionsBuilder.builder();
    if (databaseObjectType != TABLES && databaseObjectType != ALL) {
      limitOptionsBuilder.includeTables(new ExcludeAll());
    } // fall through - no else
    if (databaseObjectType == ROUTINES || databaseObjectType == ALL) {
      limitOptionsBuilder.includeAllRoutines();
    } // fall through - no else
    if (databaseObjectType == SEQUENCES || databaseObjectType == ALL) {
      limitOptionsBuilder.includeAllSequences();
    } // fall through - no else
    if (databaseObjectType == SYNONYMS || databaseObjectType == ALL) {
      limitOptionsBuilder.includeAllSynonyms();
    } // fall through - no else

    return SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
        .withLimitOptions(limitOptionsBuilder.toOptions());
  }

  @Override
  protected String getCommand() {
    return "list";
  }

  @Override
  protected boolean hasResults() {
    final DatabaseObjectType databaseObjectType = commandOptions.databaseObjectType();
    switch (databaseObjectType) {
      case TABLES:
        return !catalog.getTables().isEmpty();
      case ROUTINES:
        return !catalog.getRoutines().isEmpty();
      case SEQUENCES:
        return !catalog.getSequences().isEmpty();
      case SYNONYMS:
        return !catalog.getSynonyms().isEmpty();
      default:
        return !catalog.getTables().isEmpty()
            || !catalog.getRoutines().isEmpty()
            || !catalog.getSequences().isEmpty()
            || !catalog.getSynonyms().isEmpty();
    }
  }
}
