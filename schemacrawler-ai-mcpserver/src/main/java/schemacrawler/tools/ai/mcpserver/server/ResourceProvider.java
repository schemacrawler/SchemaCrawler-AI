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
import java.util.List;
import java.util.stream.Collectors;
import org.springaicommunity.mcp.annotation.McpArg;
import org.springaicommunity.mcp.annotation.McpResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import schemacrawler.ermodel.model.ERModel;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.Routine;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.exceptions.IORuntimeException;
import schemacrawler.tools.ai.model.AdditionalRoutineDetails;
import schemacrawler.tools.ai.model.AdditionalTableDetails;
import schemacrawler.tools.ai.model.CompactCatalogBuilder;
import schemacrawler.tools.ai.model.RoutineDocument;
import schemacrawler.tools.ai.model.TableDocument;

@Service
public class ResourceProvider {

  @Autowired public Catalog catalog;
  @Autowired public ERModel erModel;

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
    final Table table = lookupDatabaseObject(tableName, catalog.getTables());
    final EnumSet<AdditionalTableDetails> allTableDetails =
        EnumSet.allOf(AdditionalTableDetails.class);
    final TableDocument document =
        CompactCatalogBuilder.builder(catalog, erModel)
            .withAdditionalTableDetails(allTableDetails)
            .buildTableDocument(table);
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
      throw new IORuntimeException("<%s> not found".formatted(databaseObjectName));
    }
    if (databaseObjects.size() > 1) {
      throw new IORuntimeException(
          "<%s> has too many matches - provide a fully-qualified name"
              .formatted(databaseObjectName));
    }

    final DO databaseObject = databaseObjects.get(0);
    return databaseObject;
  }
}
