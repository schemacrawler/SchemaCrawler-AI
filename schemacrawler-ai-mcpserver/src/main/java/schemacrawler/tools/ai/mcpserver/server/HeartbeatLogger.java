/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.mcpserver.server;

import static schemacrawler.tools.ai.mcpserver.server.HealthController.serverUptime;
import static schemacrawler.tools.ai.utility.JsonUtility.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import schemacrawler.tools.command.mcpserver.McpServerTransportType;

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
  private McpServerTransportType mcpTransport;

  private final Supplier<String> heartbeatMessage =
      () -> {
        final String server = String.format("%s %s", serverName, serverVersion);
        final Map<String, String> heartbeatMessage = new HashMap<>();
        heartbeatMessage.put("_server", server);
        heartbeatMessage.put("in-error-state", Boolean.toString(isInErrorState));
        heartbeatMessage.put("server-uptime", String.valueOf(serverUptime()));
        heartbeatMessage.put("transport", mcpTransport.name());
        try {
          return "\n"
              + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(heartbeatMessage);
        } catch (final JsonProcessingException e) {
          return String.format(
              "%s%nin-error-state=%s; server-uptime=%s; transport=%s",
              server, isInErrorState, serverUptime(), mcpTransport);
        }
      };

  @PostConstruct
  public void init() {
    isInErrorState = ConfigurationManager.getInstance().isInErrorState();
    mcpTransport = ConfigurationManager.getInstance().getMcpTransport();
    LOGGER.log(Level.INFO, heartbeatMessage);
  }

  @Scheduled(timeUnit = TimeUnit.SECONDS, fixedRate = 30)
  public void logHeartbeat() {
    if (!heartbeat) {
      return;
    }

    LOGGER.log(Level.INFO, heartbeatMessage);
  }
}
