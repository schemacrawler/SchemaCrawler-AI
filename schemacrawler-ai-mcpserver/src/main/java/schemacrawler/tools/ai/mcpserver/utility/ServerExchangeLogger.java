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
import schemacrawler.tools.ai.utility.JsonUtility;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ObjectNode;

public final class ServerExchangeLogger {

  private static final Logger LOGGER =
      Logger.getLogger(ServerExchangeLogger.class.getCanonicalName());

  private static String makeClientLogMessage(final String message, final JsonNode logData) {
    final String clientLogMessage;
    if (logData == null) {
      clientLogMessage = message;
    } else {
      clientLogMessage = "%s%n%s".formatted(message, logData.toPrettyString().indent(2));
    }
    return clientLogMessage;
  }

  private static String makeServerLogMessage(
      final McpSyncServerExchange exchange, final String message, final JsonNode logData) {

    final ObjectNode clientSession = JsonUtility.mapper.createObjectNode();
    if (exchange != null) {
      try {
        final Implementation clientInfo = exchange.getClientInfo();
        if (clientInfo != null) {
          clientSession.put("client-name", clientInfo.name());
          clientSession.put("client-version", clientInfo.version());
        }
        clientSession.put("session-id", exchange.sessionId());
      } catch (final Exception e) {
        // Ignore
      }
    }

    final String serverLogMessage =
        "%s%s"
            .formatted(
                makeClientLogMessage(message, logData),
                makeClientLogMessage("for client session:", clientSession));
    return serverLogMessage;
  }

  private final McpSyncServerExchange exchange;
  private JsonNode functionCallbackNode;

  public ServerExchangeLogger(final McpSyncServerExchange exchange) {
    this.exchange = exchange;
  }

  public void log(final Exception e) {
    if (exchange == null || e == null) {
      return;
    }
    // Log to client
    final StringWriter stWriter = new StringWriter();
    e.printStackTrace(new PrintWriter(stWriter));
    final String clientLogMessage =
        "\n"
            + makeClientLogMessage(e.getMessage(), functionCallbackNode)
            + stWriter.toString().indent(2);
    exchange.loggingNotification(
        LoggingMessageNotification.builder(LoggingLevel.ERROR, clientLogMessage)
            .logger(LOGGER.getName())
            .build());
  }

  public void log(final String message) {
    if (exchange == null) {
      return;
    }
    // Log to client
    final String clientLogMessage = "\n" + makeClientLogMessage(message, functionCallbackNode);
    exchange.loggingNotification(
        LoggingMessageNotification.builder(LoggingLevel.INFO, clientLogMessage)
            .logger(LOGGER.getName())
            .build());
    // Log to server
    final String serverLogMessage =
        "\n" + makeServerLogMessage(exchange, message, functionCallbackNode);
    LOGGER.log(Level.INFO, serverLogMessage);
  }

  public void setFunctionCallbackNode(final JsonNode functionCallbackNode) {
    this.functionCallbackNode = functionCallbackNode;
  }
}
