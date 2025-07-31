/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.mcpserver.server;

import java.lang.management.ManagementFactory;
import java.time.Duration;
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

  @Scheduled(timeUnit = TimeUnit.SECONDS, fixedRate = 30)
  public void logHeartbeat() {
    if (!heartbeat) {
      return;
    }

    final long uptime = ManagementFactory.getRuntimeMXBean().getUptime() / 1_000;
    final Duration duration = Duration.ofSeconds(uptime);

    LOGGER.log(
        Level.INFO,
        new StringFormat(
            "Heartbeat: %s %s is running; uptime %s.", serverName, serverVersion, duration));
  }
}
