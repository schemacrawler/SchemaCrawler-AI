/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.mcpserver.utility;

import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema.Implementation;
import io.modelcontextprotocol.spec.McpSchema.LoggingLevel;
import io.modelcontextprotocol.spec.McpSchema.LoggingMessageNotification;
import java.util.logging.Level;
import java.util.logging.Logger;
import us.fatehi.utility.UtilityMarker;
import us.fatehi.utility.string.StringFormat;

@UtilityMarker
public class LoggingUtility {

  private static final Logger LOGGER = Logger.getLogger(LoggingUtility.class.getCanonicalName());

  public static void log(final McpSyncServerExchange exchange, final String logMessage) {
    if (exchange == null) {
      return;
    }
    // Log to client
    exchange.loggingNotification(
        LoggingMessageNotification.builder()
            .logger(LOGGER.getName())
            .level(LoggingLevel.INFO)
            .data(logMessage)
            .build());
    // Log to server
    LOGGER.log(Level.INFO, logMessage);
    // Log connected client information
    final Implementation clientInfo = exchange.getClientInfo();
    if (clientInfo != null) {
      LOGGER.log(Level.FINE, new StringFormat("%s %s", clientInfo.name(), clientInfo.version()));
    }
  }
}
