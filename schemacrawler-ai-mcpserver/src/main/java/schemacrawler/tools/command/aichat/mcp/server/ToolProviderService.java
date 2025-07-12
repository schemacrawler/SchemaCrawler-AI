/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.command.aichat.mcp.server;

import java.util.List;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import schemacrawler.Version;

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

  @Tool(
      name = "get-schemacrawler-version",
      description = "Gets the version of SchemaCrawler",
      returnDirect = true)
  public String getSchemaCrawlerVersion(
      @ToolParam(description = "Current date, as an ISO 8601 local date.", required = false)
          final String date) {
    System.out.printf("get-schemacrawler-version called with %s", date);
    return Version.about();
  }
}
