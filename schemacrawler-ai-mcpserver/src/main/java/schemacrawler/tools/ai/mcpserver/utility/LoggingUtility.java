/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.mcpserver.utility;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema.Implementation;
import io.modelcontextprotocol.spec.McpSchema.LoggingLevel;
import io.modelcontextprotocol.spec.McpSchema.LoggingMessageNotification;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.tools.ai.utility.JsonUtility;
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
