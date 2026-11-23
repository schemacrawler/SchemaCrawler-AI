/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.mcpserver.server;

import static java.lang.System.lineSeparator;
import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.isBlank;

import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema.Implementation;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.tools.ai.utility.JsonUtility;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ObjectNode;
import us.fatehi.utility.logging.LoggerMarker;

@LoggerMarker
public final class CallToolLogger {

  public enum TurnType {
    REQUEST,
    RESPONSE
  }

  private final Logger logger;
  private final UUID instanceId;
  private final McpSyncServerExchange exchange;
  private JsonNode functionCallbackNode;

  public CallToolLogger(final Logger logger, final McpSyncServerExchange exchange) {
    this.logger = requireNonNull(logger, "No logger provided");
    instanceId = UUID.randomUUID();
    this.exchange = exchange;
  }

  public void log(final Exception e) {
    if (e == null) {
      return;
    }
    logger.log(Level.WARNING, e.getMessage(), e);
  }

  public void log(final TurnType turnType, final String message) {
    final Level level = Level.FINE;
    if (!logger.isLoggable(level)) {
      return;
    }
    final String serverLogMessage = makeLogMessage(turnType, message);
    logger.log(level, serverLogMessage);
  }

  public void setFunctionCallbackNode(final JsonNode functionCallbackNode) {
    if (this.functionCallbackNode != null) {
      throw new IllegalArgumentException("Cannot reset function callback information");
    }
    this.functionCallbackNode = functionCallbackNode;
  }

  /** Appends a heading line followed by the indented body text. */
  private void appendSection(final StringBuilder builder, final String heading, final String body) {
    builder.append(heading).append(lineSeparator());
    builder.append(body.indent(2)).append(lineSeparator());
  }

  /** Describes the connected client (name, version, session id), if the exchange provides it. */
  private ObjectNode describeClientSession() {
    final ObjectNode clientSession = JsonUtility.mapper.createObjectNode();
    if (exchange == null) {
      return clientSession;
    }
    try {
      final Implementation clientInfo = exchange.getClientInfo();
      if (clientInfo != null) {
        clientSession.put("client-name", clientInfo.name());
        clientSession.put("client-version", clientInfo.version());
      }
      clientSession.put("session-id", exchange.sessionId());
    } catch (final Exception e) {
      // Client details are best-effort; logging must never fail the tool call
    }
    return clientSession;
  }

  private String makeLogMessage(final TurnType turnType, final String message) {
    final StringBuilder builder = new StringBuilder();
    builder.append(lineSeparator());
    appendSection(builder, "Call tool request id: ", instanceId.toString());
    if (turnType != null) {
      builder.append(turnType).append(lineSeparator());
    }
    if (!isBlank(message)) {
      appendSection(builder, "Message: ", message);
    }
    if (functionCallbackNode != null) {
      appendSection(builder, "Call tool request: ", functionCallbackNode.toPrettyString());
    }

    final ObjectNode clientSession = describeClientSession();
    appendSection(builder, "For client: ", clientSession.toPrettyString());

    return builder.toString();
  }
}
