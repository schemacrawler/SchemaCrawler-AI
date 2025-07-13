/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.mcpserver.server;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** Simple controller to check if the server is running. */
@RestController
public class HealthController {

  @GetMapping("/health")
  public Map<String, Object> healthCheck() {
    final Map<String, Object> response = new HashMap<>();
    response.put("status", "UP");
    response.put("service", "SchemaCrawler MCP Server");
    response.put("timestamp", LocalDateTime.now().toString());
    return response;
  }

  @GetMapping("/")
  public Map<String, Object> root() {
    final Map<String, Object> response = new HashMap<>();
    response.put("message", "SchemaCrawler AI MCP Server is running");
    response.put("health_endpoint", "/health");
    response.put("timestamp", LocalDateTime.now().toString());
    return response;
  }
}
