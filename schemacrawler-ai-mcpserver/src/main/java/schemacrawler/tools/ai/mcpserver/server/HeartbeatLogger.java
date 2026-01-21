/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.mcpserver.server;

import static schemacrawler.tools.ai.utility.JsonUtility.mapper;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;

@Component
@EnableScheduling
public class HeartbeatLogger {

  private static final Logger LOGGER = Logger.getLogger(HeartbeatLogger.class.getCanonicalName());

  @Value("${server.heartbeat}")
  private boolean heartbeat;

  @Autowired private ServerHealth serverHealth;

  private final Supplier<String> heartbeatMessage =
      () -> {
        final Map<String, Object> heartbeatMessage = serverHealth.currentState();
        try {
          return "\n"
              + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(heartbeatMessage);
        } catch (final JacksonException e) {
          LOGGER.log(Level.WARNING, "Could not convert server state to JSON", e);
          return heartbeatMessage.toString();
        }
      };

  @Scheduled(timeUnit = TimeUnit.SECONDS, fixedRate = 120)
  public void logHeartbeat() {
    if (!heartbeat) {
      return;
    }

    LOGGER.log(Level.INFO, heartbeatMessage);
  }
}
