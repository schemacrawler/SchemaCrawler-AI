/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.mcpserver.server;

import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import schemacrawler.Version;
import schemacrawler.tools.ai.utility.JsonUtility;

/**
 * Service class for managing tool providers. This class separates tool-related functionality to
 * avoid circular dependencies.
 */
@Service
public class ToolProviderService {

  /**
   * Creates a tool callback provider for common services.
   *
   * @param commonService The common service
   * @return A tool callback provider
   */
  @Bean
  public ToolCallbackProvider commonTools(final ToolProviderService commonService) {
    return MethodToolCallbackProvider.builder().toolObjects(commonService).build();
  }

  @Tool(
      name = "get-schemacrawler-version",
      description = "Gets the version of SchemaCrawler",
      returnDirect = true)
  public String getSchemaCrawlerVersion(
      @ToolParam(description = "MCP Client identification, if available.", required = false)
          final String clientId,
      @ToolParam(description = "Event id, if available.", required = false) final String eventId) {
    final ObjectNode objectNode = JsonUtility.mapper.createObjectNode();
    objectNode.put("schemacrawler-version", Version.version().toString());
    objectNode.put("mcp-client-id", clientId);
    objectNode.put("mcp-event-id", eventId);
    return objectNode.toString();
  }

  /**
   * Creates a tool callback provider for SchemaCrawler tools.
   *
   * @return A tool callback provider
   */
  @Bean
  public ToolCallbackProvider schemaCrawlerTools() {
    final List<ToolCallback> tools = SpringAIToolUtility.toolCallbacks(SpringAIToolUtility.tools());
    return ToolCallbackProvider.from(tools);
  }
}
