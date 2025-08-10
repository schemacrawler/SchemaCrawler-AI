/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.mcpserver.server;

import jakarta.annotation.PostConstruct;
import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** Simple controller to check if the server is running. */
@RestController
public class HealthController {

  private static final Logger LOGGER = Logger.getLogger(HealthController.class.getCanonicalName());

  public static Duration serverUptime() {
    final long uptime = ManagementFactory.getRuntimeMXBean().getUptime() / 1_000;
    final Duration duration = Duration.ofSeconds(uptime);
    return duration;
  }

  @Value("${server.name}")
  private String serverName;

  @Value("${server.version}")
  private String serverVersion;

  @GetMapping({"/", "/health"})
  public Map<String, Object> healthCheck() {
    final Map<String, Object> response = new HashMap<>();
    response.put("status", "UP");
    response.put("service", serverName());
    response.put("timestamp", LocalDateTime.now().toString());
    response.put("uptime", serverUptime());
    return response;
  }

  @PostConstruct
  public void init() {
    LOGGER.log(Level.INFO, serverName());
  }

  private String serverName() {
    return String.format("%s %s", serverName, serverVersion);
  }
}
