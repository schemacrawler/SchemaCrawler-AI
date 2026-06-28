/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.mcpserver.server;

import static java.lang.System.lineSeparator;
import static us.fatehi.utility.Utility.isBlank;

import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema.Implementation;
import io.modelcontextprotocol.spec.McpSchema.LoggingLevel;
import io.modelcontextprotocol.spec.McpSchema.LoggingMessageNotification;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.tools.ai.utility.JsonUtility;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ObjectNode;

public final class CallToolLogger {

  private static final Logger LOGGER = Logger.getLogger(CallToolLogger.class.getCanonicalName());

  private final UUID instanceId;
  private final McpSyncServerExchange exchange;
  private JsonNode functionCallbackNode;

  public CallToolLogger(final McpSyncServerExchange exchange) {
    instanceId = UUID.randomUUID();
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
        "%s%nStack trace: %n%s"
            .formatted(
                makeMessage(null, e.getMessage(), functionCallbackNode),
                stWriter.toString().indent(2));
    exchange.loggingNotification(
        LoggingMessageNotification.builder(LoggingLevel.ERROR, clientLogMessage)
            .logger(LOGGER.getName())
            .build());
  }

  public void log(final String message) {
    if (exchange != null) {
      // Log to client
      final String clientLogMessage = makeMessage(null, message, functionCallbackNode);
      exchange.loggingNotification(
          LoggingMessageNotification.builder(LoggingLevel.INFO, clientLogMessage)
              .logger(LOGGER.getName())
              .build());
    }
    // Log to server
    final String serverLogMessage =
        "\n" + makeServerLogMessage(exchange, message, functionCallbackNode);
    LOGGER.log(Level.INFO, serverLogMessage);
  }

  public void setFunctionCallbackNode(final JsonNode functionCallbackNode) {
    if (this.functionCallbackNode != null) {
      throw new IllegalArgumentException("Cannot reset function callback information");
    }
    this.functionCallbackNode = functionCallbackNode;
  }

  private String makeMessage(
      final JsonNode clientSession, final String message, final JsonNode logData) {
    final StringBuilder builder = new StringBuilder();
    builder.append(lineSeparator());
    builder.append("Call tool request id: ").append(instanceId).append(lineSeparator());
    if (!isBlank(message)) {
      builder.append("Message: ").append(lineSeparator()).append(message).append(lineSeparator());
    }
    if (logData != null) {
      builder.append("Call tool request: ").append(lineSeparator());
      builder.append(logData.toPrettyString().indent(2)).append(lineSeparator());
    }
    if (clientSession != null) {
      builder.append("For client: ").append(lineSeparator());
      builder.append(clientSession.toPrettyString().indent(2)).append(lineSeparator());
    }
    return builder.toString();
  }

  private String makeServerLogMessage(
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

    return makeMessage(clientSession, message, logData);
  }
}
