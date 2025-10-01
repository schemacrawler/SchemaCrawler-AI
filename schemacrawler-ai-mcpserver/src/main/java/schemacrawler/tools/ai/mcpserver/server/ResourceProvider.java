/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.mcpserver.server;

import static java.util.Objects.requireNonNull;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static schemacrawler.tools.ai.model.CatalogDocument.allRoutineDetails;
import static schemacrawler.tools.ai.model.CatalogDocument.allTableDetails;
import static us.fatehi.utility.Utility.trimToEmpty;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.springaicommunity.mcp.annotation.McpArg;
import org.springaicommunity.mcp.annotation.McpResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.Routine;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.exceptions.IORuntimeException;
import schemacrawler.tools.ai.model.CompactCatalogUtility;
import schemacrawler.tools.ai.model.RoutineDocument;
import schemacrawler.tools.ai.model.TableDocument;

@Service
public class ResourceProvider {

  @Autowired public Catalog catalog;

  @McpResource(
      uri = "catalog://routines/{routine-name}",
      name = "routine-details",
      title = "Routine metadata details",
      description = "Provides detailed database metadata for the specified routine, as JSON.",
      mimeType = APPLICATION_JSON_VALUE)
  public String getRoutineDetails(
      @McpArg(name = "routine-name", description = "Fully-qualified routine name.", required = true)
          final String routineName) {
    final Routine routine = lookupDatabaseObject(routineName, catalog.getRoutines());
    final RoutineDocument document =
        new CompactCatalogUtility()
            .withAdditionalRoutineDetails(allRoutineDetails())
            .getRoutineDocument(routine);
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
    final Table table = lookupDatabaseObject(tableName, catalog.getTables());
    final TableDocument document =
        new CompactCatalogUtility()
            .withAdditionalTableDetails(allTableDetails())
            .getTableDocument(table);
    return document.toObjectNode().toPrettyString();
  }

  private <DO extends DatabaseObject> DO lookupDatabaseObject(
      final String databaseObjectName, final Collection<DO> allDatabaseObjects) {
    requireNonNull(allDatabaseObjects, "No database objects provided");
    final String searchObjectName = trimToEmpty(databaseObjectName);
    final List<DO> databaseObjects =
        allDatabaseObjects.stream()
            .filter(
                databaseObject ->
                    databaseObject.getName().equalsIgnoreCase(searchObjectName)
                        || databaseObject.getFullName().equalsIgnoreCase(searchObjectName))
            .collect(Collectors.toList());
    if (databaseObjects.isEmpty()) {
      throw new IORuntimeException(String.format("<%s> not found", databaseObjectName));
    }
    if (databaseObjects.size() > 1) {
      throw new IORuntimeException(
          String.format(
              "<%s> has too many matches - provide a fully-qualified name", databaseObjectName));
    }

    final DO databaseObject = databaseObjects.get(0);
    return databaseObject;
  }
}
