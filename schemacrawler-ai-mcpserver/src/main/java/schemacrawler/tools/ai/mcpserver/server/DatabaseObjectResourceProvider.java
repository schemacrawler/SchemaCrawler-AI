package schemacrawler.tools.ai.mcpserver.server;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static schemacrawler.tools.ai.model.CatalogDocument.allRoutineDetails;
import static schemacrawler.tools.ai.model.CatalogDocument.allTableDetails;
import static us.fatehi.utility.Utility.trimToEmpty;

import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema.ReadResourceRequest;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springaicommunity.mcp.annotation.McpArg;
import org.springaicommunity.mcp.annotation.McpResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Routine;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Sequence;
import schemacrawler.schema.Synonym;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.exceptions.SchemaCrawlerException;
import schemacrawler.tools.ai.mcpserver.utility.LoggingUtility;
import schemacrawler.tools.ai.model.CompactCatalogUtility;
import schemacrawler.tools.ai.model.DatabaseObjectDocument;
import schemacrawler.tools.ai.model.Document;
import schemacrawler.tools.ai.model.RoutineDocument;
import schemacrawler.tools.ai.model.TableDocument;

@Service
public class DatabaseObjectResourceProvider {

  private static final Logger LOGGER =
      Logger.getLogger(DatabaseObjectResourceProvider.class.getCanonicalName());

  @Autowired public Catalog catalog;

  @McpResource(
      uri = "routines://{schema}/{routine-name}",
      name = "routine-details",
      description = "Provides detailed database metadata for the specified routine.",
      mimeType = APPLICATION_JSON_VALUE)
  public String getRoutineDetails(
      final ReadResourceRequest resourceRequest,
      final McpSyncServerExchange exchange,
      @McpArg(
              name = "schema",
              description =
                  "Fully qualified schema name. "
                      + "If the database has no schemas, use \"-\" for the schema.",
              required = false)
          final String schemaName,
      @McpArg(name = "routine-name", description = "Routine name.", required = true)
          final String routineName) {
    try {
      final Schema schema = lookupSchema(schemaName);
      final Document document = lookupRoutine(schema, routineName);
      logResourceRequest(exchange, schema, routineName);
      return document.toObjectNode().toPrettyString();
    } catch (final Exception e) {
      logException(exchange, resourceRequest, e);
      throw e;
    }
  }

  @McpResource(
      uri = "sequences://{schema}/{sequence-name}",
      name = "sequence-details",
      description = "Provides detailed database metadata for the specified sequence.",
      mimeType = APPLICATION_JSON_VALUE)
  public String getSequenceDetails(
      final ReadResourceRequest resourceRequest,
      final McpSyncServerExchange exchange,
      @McpArg(
              name = "schema",
              description =
                  "Fully qualified schema name. "
                      + "If the database has no schemas, use \"-\" for the schema.",
              required = false)
          final String schemaName,
      @McpArg(name = "sequence-name", description = "Sequence name.", required = true)
          final String sequenceName) {
    try {
      final Schema schema = lookupSchema(schemaName);
      final Document document = lookupSequence(schema, sequenceName);
      logResourceRequest(exchange, schema, sequenceName);
      return document.toObjectNode().toPrettyString();
    } catch (final Exception e) {
      logException(exchange, resourceRequest, e);
      throw e;
    }
  }

  @McpResource(
      uri = "synonyms://{schema}/{synonym-name}",
      name = "synonym-details",
      description = "Provides detailed database metadata for the specified synonym.",
      mimeType = APPLICATION_JSON_VALUE)
  public String getSynonymDetails(
      final ReadResourceRequest resourceRequest,
      final McpSyncServerExchange exchange,
      @McpArg(
              name = "schema",
              description =
                  "Fully qualified schema name. "
                      + "If the database has no schemas, use \"-\" for the schema.",
              required = false)
          final String schemaName,
      @McpArg(name = "synonym-name", description = "Synonym name.", required = true)
          final String synonymName) {
    try {
      final Schema schema = lookupSchema(schemaName);
      final Document document = lookupSynonym(schema, synonymName);
      logResourceRequest(exchange, schema, synonymName);
      return document.toObjectNode().toPrettyString();
    } catch (final Exception e) {
      logException(exchange, resourceRequest, e);
      throw e;
    }
  }

  @McpResource(
      uri = "tables://{schema}/{table-name}",
      name = "table-details",
      description = "Provides detailed database metadata for the specified table.",
      mimeType = APPLICATION_JSON_VALUE)
  public String getTableDetails(
      final ReadResourceRequest resourceRequest,
      final McpSyncServerExchange exchange,
      @McpArg(
              name = "schema",
              description =
                  "Fully qualified schema name. "
                      + "If the database has no schemas, use \"-\" for the schema.",
              required = false)
          final String schemaName,
      @McpArg(name = "table-name", description = "Table name.", required = true)
          final String tableName) {
    try {
      final Schema schema = lookupSchema(schemaName);
      final Document document = lookupTable(schema, tableName);
      logResourceRequest(exchange, schema, tableName);
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

  private void logResourceRequest(
      final McpSyncServerExchange exchange, final Schema schema, final String databaseObjectName) {
    final String logMessage =
        String.format("Resource requested for %s", schema.key().with(databaseObjectName));
    LoggingUtility.log(exchange, logMessage);
  }

  private Document lookupRoutine(final Schema schema, final String routineName) {
    final Routine routine =
        catalog
            .lookupRoutine(schema, trimToEmpty(routineName))
            .orElseThrow(
                () ->
                    new SchemaCrawlerException(
                        String.format(
                            "Routine <%s/%s> not found", schema.getFullName(), routineName)));

    final RoutineDocument routineDocument =
        new CompactCatalogUtility()
            .withAdditionalRoutineDetails(allRoutineDetails())
            .getRoutineDocument(routine);

    return routineDocument;
  }

  private Schema lookupSchema(final String schemaName) {
    final String lookupSchemaName = trimToEmpty(schemaName).replace("-", "");
    final Schema schema =
        catalog
            .lookupSchema(lookupSchemaName)
            .orElseThrow(
                () -> new SchemaCrawlerException("Please provide a valid database schema"));
    return schema;
  }

  private Document lookupSequence(final Schema schema, final String sequenceName) {
    final Sequence sequence =
        catalog
            .lookupSequence(schema, trimToEmpty(sequenceName))
            .orElseThrow(
                () ->
                    new SchemaCrawlerException(
                        String.format(
                            "Sequence <%s/%s> not found", schema.getFullName(), sequenceName)));

    final DatabaseObjectDocument sequenceDocument = new DatabaseObjectDocument(sequence);

    return sequenceDocument;
  }

  private Document lookupSynonym(final Schema schema, final String synonymName) {
    final Synonym synonym =
        catalog
            .lookupSynonym(schema, trimToEmpty(synonymName))
            .orElseThrow(
                () ->
                    new SchemaCrawlerException(
                        String.format(
                            "Synonym <%s/%s> not found", schema.getFullName(), synonymName)));

    final DatabaseObjectDocument synonymDocument = new DatabaseObjectDocument(synonym);

    return synonymDocument;
  }

  private Document lookupTable(final Schema schema, final String tableName) {
    final Table table =
        catalog
            .lookupTable(schema, trimToEmpty(tableName))
            .orElseThrow(
                () ->
                    new SchemaCrawlerException(
                        String.format("Table <%s/%s> not found", schema.getFullName(), tableName)));

    final TableDocument tableDocument =
        new CompactCatalogUtility()
            .withAdditionalTableDetails(allTableDetails())
            .getTableDocument(table);

    return tableDocument;
  }
}
