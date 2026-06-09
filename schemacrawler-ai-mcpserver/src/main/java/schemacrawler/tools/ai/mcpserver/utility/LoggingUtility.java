/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.mcpserver.utility;

import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema.Implementation;
import io.modelcontextprotocol.spec.McpSchema.LoggingLevel;
import io.modelcontextprotocol.spec.McpSchema.LoggingMessageNotification;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.BeanFactory;
import schemacrawler.ermodel.model.ERModel;
import schemacrawler.loader.catalog.summary.CatalogSummaryUtility;
import schemacrawler.loader.ermodel.summary.ERModelSummaryUtility;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.Version;
import schemacrawler.tools.ai.mcpserver.McpServerTransportType;
import schemacrawler.tools.ai.utility.JsonUtility;
import schemacrawler.tools.ai.utility.SchemaCrawlerAiVersion;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ObjectNode;
import us.fatehi.utility.UtilityMarker;

@UtilityMarker
public final class LoggingUtility {

  private static final Logger LOGGER = Logger.getLogger(LoggingUtility.class.getCanonicalName());

  public static void log(
      final McpSyncServerExchange exchange, final String message, final JsonNode logData) {
    if (exchange == null || logData == null) {
      return;
    }
    // Log to client
    final String clientLogMessage = message + "\n" + logData.toPrettyString().indent(2);
    exchange.loggingNotification(
        LoggingMessageNotification.builder(LoggingLevel.INFO, clientLogMessage)
            .logger(LOGGER.getName())
            .build());
    // Log to server
    final String serverLogMessage = "\n" + toServerLog(exchange, message, logData);
    LOGGER.log(Level.INFO, serverLogMessage);
  }

  public static void logExceptionToClient(
      final McpSyncServerExchange exchange, final String message, final Exception e) {
    if (exchange == null || e == null) {
      return;
    }
    // Log to client
    final StringWriter stWriter = new StringWriter();
    e.printStackTrace(new PrintWriter(stWriter));
    final String clientLogMessage = message + "\n" + stWriter.toString().indent(2);
    exchange.loggingNotification(
        LoggingMessageNotification.builder(LoggingLevel.ERROR, clientLogMessage)
            .logger(LOGGER.getName())
            .build());
  }

  public static void logStartup(final BeanFactory beanFactory) {
    if (beanFactory == null || !LOGGER.isLoggable(Level.INFO)) {
      return;
    }

    final Catalog catalog = beanFactory.getBean("catalog", Catalog.class);
    final ERModel erModel = beanFactory.getBean("erModel", ERModel.class);

    try (final StringWriter stringWriter = new StringWriter();
        final PrintWriter writer = new PrintWriter(stringWriter)) {

      if (catalog != null) {
        writer.println("Catalog summary:%n%s".formatted(CatalogSummaryUtility.summarize(catalog)));
        writer.println();
      }

      if (erModel != null) {
        writer.println("ER Model summary:%n%s".formatted(ERModelSummaryUtility.summarize(erModel)));
        writer.println();
      }

      writer.close();

      LOGGER.log(Level.INFO, stringWriter.toString());
    } catch (final Exception e) {
      // Ignore exception
    }
  }

  public static void logStartup(final McpServerTransportType mcpTransport) {
    if (mcpTransport == null || !LOGGER.isLoggable(Level.INFO)) {
      return;
    }

    try (final StringWriter stringWriter = new StringWriter();
        final PrintWriter writer = new PrintWriter(stringWriter)) {

      writer.println();
      writer.println("-".repeat(80));

      writer.println(new SchemaCrawlerAiVersion());
      writer.println(Version.version());
      writer.println(new SpringAiVersion());
      writer.println(new SpringBootFrameworkVersion());
      writer.println(new SpringFrameworkVersion());

      writer.println();
      writer.println(
          "SchemaCrawler AI MCP Server is running with %s transport"
              .formatted(mcpTransport.getDescription()));

      writer.println("-".repeat(80));
      writer.println();

      writer.close();

      LOGGER.log(Level.INFO, stringWriter.toString());
    } catch (final Exception e) {
      // Ignore exception
    }
  }

  private static String toServerLog(
      final McpSyncServerExchange exchange, final String message, final JsonNode logData) {
    if (exchange == null) {
      return "";
    }
    try {
      final Implementation clientInfo = exchange.getClientInfo();
      final ObjectNode logRecord = JsonUtility.mapper.createObjectNode();
      final ObjectNode logMessage = logRecord.putObject("log-message");
      logMessage.put("message", message);
      logMessage.set("data", logData);
      final ObjectNode session = logRecord.putObject("session");
      session.put("session-id", exchange.sessionId());
      if (clientInfo != null) {
        session.put("client-name", clientInfo.name());
        session.put("client-version", clientInfo.version());
      }
      return logRecord.toPrettyString();
    } catch (final Exception e) {
      return "";
    }
  }

  private LoggingUtility() {
    // Prevent instantiation
  }
}
