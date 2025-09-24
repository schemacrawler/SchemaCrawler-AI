package schemacrawler.tools.ai.mcpserver.server;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static schemacrawler.tools.ai.model.CatalogDocument.allRoutineDetails;
import static schemacrawler.tools.ai.model.CatalogDocument.allTableDetails;
import static us.fatehi.utility.Utility.trimToEmpty;

import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema.ReadResourceRequest;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.springaicommunity.mcp.annotation.McpArg;
import org.springaicommunity.mcp.annotation.McpResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Routine;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.exceptions.SchemaCrawlerException;
import schemacrawler.tools.ai.mcpserver.utility.LoggingUtility;
import schemacrawler.tools.ai.model.CompactCatalogUtility;
import schemacrawler.tools.ai.model.Document;
import schemacrawler.tools.ai.model.RoutineDocument;
import schemacrawler.tools.ai.model.TableDocument;

@Service
public class DatabaseObjectResourceProvider {

  private static final Logger LOGGER =
      Logger.getLogger(DatabaseObjectResourceProvider.class.getCanonicalName());

  @Autowired public Catalog catalog;

  @McpResource(
      uri = "routines://{routine-name}",
      name = "routine-details",
      description = "Provides detailed database metadata for the specified routine.",
      mimeType = APPLICATION_JSON_VALUE)
  public String getRoutineDetails(
      final ReadResourceRequest resourceRequest,
      final McpSyncServerExchange exchange,
      @McpArg(name = "routine-name", description = "Fully-qualified routine name.", required = true)
          final String routineName) {
    try {
      final Document document = lookupRoutine(routineName);
      LoggingUtility.log(
          exchange, String.format("Could not read resource <%s>", resourceRequest.uri()));
      return document.toObjectNode().toPrettyString();
    } catch (final Exception e) {
      logException(exchange, resourceRequest, e);
      throw e;
    }
  }

  @McpResource(
      uri = "tables://{table-name}",
      name = "table-details",
      description = "Provides detailed database metadata for the specified table.",
      mimeType = APPLICATION_JSON_VALUE)
  public String getTableDetails(
      final ReadResourceRequest resourceRequest,
      final McpSyncServerExchange exchange,
      @McpArg(name = "table-name", description = "Fully-qualified table name.", required = true)
          final String tableName) {
    try {
      final Document document = lookupTable(tableName);
      LoggingUtility.log(
          exchange, String.format("Could not read resource <%s>", resourceRequest.uri()));
      return document.toObjectNode().toPrettyString();
    } catch (final Exception e) {
      logException(exchange, resourceRequest, e);
      throw e;
    }
  }

  private void logException(
      final McpSyncServerExchange exchange,
      final ReadResourceRequest resourceRequest,
      final Exception e) {

    if (exchange == null || e == null) {
      return;
    }

    final String logMessage;
    if (resourceRequest != null) {
      logMessage = String.format("Could not read resource <%s>", resourceRequest.uri());
    } else {
      logMessage = e.getMessage();
    }

    LoggingUtility.log(exchange, logMessage);
    LOGGER.log(Level.FINER, e.getMessage(), e);
  }

  private Document lookupRoutine(final String routineName) {
    final String searchRoutineName = trimToEmpty(routineName);
    final List<Routine> routines =
        catalog.getRoutines().stream()
            .filter(
                routine ->
                    routine.getName().equalsIgnoreCase(searchRoutineName)
                        || routine.getFullName().equalsIgnoreCase(searchRoutineName))
            .collect(Collectors.toList());
    if (routines.isEmpty()) {
      throw new SchemaCrawlerException(String.format("Routine <%s> not found", routineName));
    }
    if (routines.size() > 1) {
      throw new SchemaCrawlerException(
          String.format(
              "Too many routines match <%s> - provide a fully-qualified routine name",
              routineName));
    }

    final Routine routine = routines.get(0);
    final RoutineDocument routineDocument =
        new CompactCatalogUtility()
            .withAdditionalRoutineDetails(allRoutineDetails())
            .getRoutineDocument(routine);

    return routineDocument;
  }

  private Document lookupTable(final String tableName) {
    final String searchTableName = trimToEmpty(tableName);
    final List<Table> tables =
        catalog.getTables().stream()
            .filter(
                table ->
                    table.getName().equalsIgnoreCase(searchTableName)
                        || table.getFullName().equalsIgnoreCase(searchTableName))
            .collect(Collectors.toList());
    if (tables.isEmpty()) {
      throw new SchemaCrawlerException(String.format("Table <%s> not found", tableName));
    }
    if (tables.size() > 1) {
      throw new SchemaCrawlerException(
          String.format(
              "Too many tables match <%s> - provide a fully-qualified table name", tableName));
    }

    final Table table = tables.get(0);
    final TableDocument tableDocument =
        new CompactCatalogUtility()
            .withAdditionalTableDetails(allTableDetails())
            .getTableDocument(table);

    return tableDocument;
  }
}
