/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
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
import schemacrawler.schemacrawler.Version;
import schemacrawler.tools.ai.mcpserver.McpServerTransportType;
import schemacrawler.tools.ai.utility.JsonUtility;
import schemacrawler.tools.ai.utility.SchemaCrawlerAiVersion;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ObjectNode;
import us.fatehi.utility.UtilityMarker;

@UtilityMarker
public class LoggingUtility {

  private static final Logger LOGGER = Logger.getLogger(LoggingUtility.class.getCanonicalName());

  public static void log(
      final McpSyncServerExchange exchange, final String message, final JsonNode logData) {
    if (exchange == null || logData == null) {
      return;
    }
    // Log to client
    final String clientLogMessage = message + "\n" + logData.toPrettyString().indent(2);
    exchange.loggingNotification(
        LoggingMessageNotification.builder()
            .logger(LOGGER.getName())
            .level(LoggingLevel.INFO)
            .data(clientLogMessage)
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
        LoggingMessageNotification.builder()
            .logger(LOGGER.getName())
            .level(LoggingLevel.ERROR)
            .data(clientLogMessage)
            .build());
  }

  public static void logStartup(final McpServerTransportType mcpTransport) {
    if (!LOGGER.isLoggable(Level.INFO)) {
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
          "SchemaCrawler AI MCP Server is running with <%s> transport".formatted(mcpTransport));

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
}
