/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.mcpserver.server;

import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import schemacrawler.tools.ai.mcpserver.ExcludeTools;
import schemacrawler.tools.ai.mcpserver.McpServerTransportType;
import schemacrawler.tools.ai.utility.SchemaCrawlerAiVersion;
import us.fatehi.utility.property.ProductVersion;

@Component
public class ServerHealth {

  private static final ProductVersion serverName = new SchemaCrawlerAiVersion();

  @Autowired private boolean isInErrorState;
  @Autowired private McpServerTransportType mcpTransport;
  @Autowired private ExcludeTools excludeTools;

  public Map<String, Object> currentState() {
    final Map<String, Object> currentState = new HashMap<>();
    currentState.put("_server", getServerName());
    currentState.put("current-timestamp", String.valueOf(ZonedDateTime.now(ZoneOffset.UTC)));
    currentState.put("in-error-state", isInErrorState);
    currentState.put("server-uptime", String.valueOf(getServerUptime()));
    currentState.put("transport", mcpTransport.name());
    currentState.put("exclude-tools", excludeTools.excludeTools());
    return currentState;
  }

  public McpServerTransportType getMcpTransport() {
    return mcpTransport;
  }

  public String getServerName() {
    return serverName.toString();
  }

  public Duration getServerUptime() {
    final long uptime = ManagementFactory.getRuntimeMXBean().getUptime() / 1_000;
    final Duration duration = Duration.ofSeconds(uptime);
    return duration;
  }

  public boolean isInErrorState() {
    return isInErrorState;
  }
}
