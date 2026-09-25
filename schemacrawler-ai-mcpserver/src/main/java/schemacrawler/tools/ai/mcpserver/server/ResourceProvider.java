/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.mcpserver.server;

import static java.util.Objects.requireNonNull;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static us.fatehi.utility.Utility.trimToEmpty;

import java.util.Collection;
import java.util.EnumSet;
import java.util.regex.Pattern;
import org.springframework.ai.mcp.annotation.McpArg;
import org.springframework.ai.mcp.annotation.McpResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import schemacrawler.ermodel.model.ERModel;
import schemacrawler.filter.CatalogSearcher;
import schemacrawler.filter.NamedObjectFilter;
import schemacrawler.filter.NamedObjectFilters;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.NamedObject;
import schemacrawler.schema.Routine;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import schemacrawler.tools.ai.model.AdditionalRoutineDetails;
import schemacrawler.tools.ai.model.AdditionalTableDetails;
import schemacrawler.tools.ai.model.CompactCatalogBuilder;
import schemacrawler.tools.ai.model.RoutineDocument;
import schemacrawler.tools.ai.model.TableDocument;

@Service
public class ResourceProvider {

  /**
   * Matches a resource identifier exactly and literally against either an object's simple name or
   * full name.
   *
   * <p>This preserves the MCP resource lookup contract: identifiers are not user-supplied regular
   * expressions, a simple name remains valid, and ambiguous results are rejected by the caller. (In
   * contrast, {@link NamedObjectFilters#fullName} accepts an inclusion rule and only considers full
   * names. The component filters remain case-insensitive, and the full-name filter also accepts
   * quoted identifier forms.)
   *
   * @param databaseObjectName resource identifier to match
   * @return a literal simple-name-or-full-name filter
   */
  private static NamedObjectFilter<NamedObject> exactNameOrFullNameFilter(
      final String databaseObjectName) {
    // Quote user input so a literal database-object name is never interpreted as a
    // regex
    final String exactNameRegex = Pattern.quote(trimToEmpty(databaseObjectName));
    return NamedObjectFilters.nameRegex(exactNameRegex)
            .or(NamedObjectFilters.fullNameRegex(exactNameRegex))
        ::test;
  }

  @Autowired private Catalog catalog;

  @Autowired private ERModel erModel;

  @McpResource(
      uri = "catalog://routines/{routine-name}",
      name = "routine-details",
      title = "Routine metadata details",
      description = "Provides detailed database metadata for the specified routine, as JSON.",
      mimeType = APPLICATION_JSON_VALUE)
  public String getRoutineDetails(
      @McpArg(name = "routine-name", description = "Fully-qualified routine name.", required = true)
          final String routineName) {

    final Collection<Routine> routines =
        CatalogSearcher.search(catalog).findRoutines(exactNameOrFullNameFilter(routineName));
    final Routine routine = lookupDatabaseObject(routineName, routines);
    final EnumSet<AdditionalRoutineDetails> allRoutineDetails =
        EnumSet.allOf(AdditionalRoutineDetails.class);
    final RoutineDocument document =
        CompactCatalogBuilder.builder(catalog, erModel)
            .withAdditionalRoutineDetails(allRoutineDetails)
            .buildRoutineDocument(routine);
    return document.toObjectNode().toPrettyString();
  }

  @McpResource(
      uri = "catalog://tables/{table-name}",
      name = "table-details",
      title = "Table metadata details",
      description = "Provides detailed database metadata for the specified table, as JSON.",
      mimeType = APPLICATION_JSON_VALUE)
  public String getTableDetails(
      @McpArg(name = "table-name", description = "Fully-qualified table name.", required = true)
          final String tableName) {

    final Collection<Table> tables =
        CatalogSearcher.search(catalog).findTables(exactNameOrFullNameFilter(tableName));
    final Table table = lookupDatabaseObject(tableName, tables);
    final EnumSet<AdditionalTableDetails> allTableDetails =
        EnumSet.allOf(AdditionalTableDetails.class);
    final TableDocument document =
        CompactCatalogBuilder.builder(catalog, erModel)
            .withAdditionalTableDetails(allTableDetails)
            .buildTableDocument(table);
    return document.toObjectNode().toPrettyString();
  }

  private <DO extends DatabaseObject> DO lookupDatabaseObject(
      final String databaseObjectName, final Collection<DO> databaseObjects) {

    requireNonNull(databaseObjects, "No database objects provided");

    if (databaseObjects.isEmpty()) {
      throw new ExecutionRuntimeException("<%s> not found".formatted(databaseObjectName));
    }
    if (databaseObjects.size() > 1) {
      throw new ExecutionRuntimeException(
          "<%s> has too many matches - provide a fully-qualified name"
              .formatted(databaseObjectName));
    }

    final DO databaseObject = databaseObjects.iterator().next();
    return databaseObject;
  }
}
