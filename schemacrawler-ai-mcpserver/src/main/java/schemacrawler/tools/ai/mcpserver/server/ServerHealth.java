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
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import schemacrawler.tools.ai.mcpserver.ExcludeTools;
import schemacrawler.tools.ai.mcpserver.McpServerTransportType;

@Component
public class ServerHealth {

  @Value("${server.name}")
  private String serverName;

  @Value("${server.version}")
  private String serverVersion;

  @Autowired private boolean isInErrorState;
  @Autowired private McpServerTransportType mcpTransport;
  @Autowired private ExcludeTools excludeTools;

  public Map<String, String> currentState() {
    final Map<String, String> currentState = new HashMap<>();
    currentState.put("_server", getServerName());
    currentState.put("current-timestamp", String.valueOf(ZonedDateTime.now(ZoneOffset.UTC)));
    currentState.put("in-error-state", Boolean.toString(isInErrorState));
    currentState.put("server-uptime", String.valueOf(getServerUptime()));
    currentState.put("transport", mcpTransport.name());
    currentState.put("exclude-tools", excludeTools.excludeTools().toString());
    return currentState;
  }

  public String currentStateString() {
    return String.format(
        "%s%nin-error-state=%s; server-uptime=%s; transport=%s",
        getServerName(), isInErrorState, getServerUptime(), mcpTransport);
  }

  public McpServerTransportType getMcpTransport() {
    return mcpTransport;
  }

  public String getServerName() {
    return String.format("%s %s", serverName, serverVersion);
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
