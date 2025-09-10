/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.mcpserver.server;

import static schemacrawler.tools.ai.mcpserver.server.HealthController.serverUptime;

import jakarta.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import us.fatehi.utility.string.StringFormat;

@Component
@EnableScheduling
public class HeartbeatLogger {

  private static final Logger LOGGER = Logger.getLogger(HeartbeatLogger.class.getCanonicalName());

  @Value("${server.heartbeat}")
  private boolean heartbeat;

  @Value("${server.name}")
  private String serverName;

  @Value("${server.version}")
  private String serverVersion;

  private boolean isInErrorState;

  @PostConstruct
  public void init() {
    isInErrorState = ConfigurationManager.getInstance().isInErrorState();
    LOGGER.log(
        Level.INFO,
        String.format(
            "%s; heartbeat=%b; inErrorState=%b", serverName(), heartbeat, isInErrorState));
  }

  @Scheduled(timeUnit = TimeUnit.SECONDS, fixedRate = 30)
  public void logHeartbeat() {
    if (!heartbeat) {
      return;
    }

    LOGGER.log(
        Level.INFO,
        new StringFormat(
            "Heartbeat: %s is running with uptime %s%s",
            serverName(), serverUptime(), isInErrorState ? "; server is in error state" : ""));
  }

  private String serverName() {
    return String.format("%s %s", serverName, serverVersion);
  }
}
