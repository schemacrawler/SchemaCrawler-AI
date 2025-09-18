/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.mcpserver.server;

import java.sql.Connection;
import java.util.Objects;
import java.util.logging.Logger;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.ai.tools.FunctionCallback;
import schemacrawler.tools.ai.tools.ToolSpecification;

public final class SchemaCrawlerToolCallback implements ToolCallback {

  private static final Logger LOGGER =
      Logger.getLogger(SchemaCrawlerToolCallback.class.getCanonicalName());

  private final ToolDefinition toolDefinition;
  private final FunctionCallback functionToolExecutor;

  public SchemaCrawlerToolCallback(
      final ToolSpecification toolSpecification, final Catalog catalog) {
    Objects.requireNonNull(toolSpecification, "No tool specification provided");
    toolDefinition = toToolDefinition(toolSpecification);
    functionToolExecutor = new FunctionCallback(toolDefinition.name(), catalog);
  }

  @Override
  @NonNull
  public String call(@NonNull final String toolInput) {
    if (!StringUtils.hasText(toolInput)) {
      return "";
    }

    final String callMessage = String.format("Call to <%s>%n%s", toolDefinition.name(), toolInput);
    LOGGER.info(callMessage);

    final Connection connection = ConnectionService.getConnection();
    return functionToolExecutor.execute(toolInput, connection);
  }

  @Override
  @NonNull
  public String call(@NonNull final String toolInput, @Nullable final ToolContext tooContext) {
    return call(toolInput);
  }

  @Override
  @NonNull
  public ToolDefinition getToolDefinition() {
    return toolDefinition;
  }

  @Override
  public String toString() {
    return toolDefinition.name();
  }

  private ToolDefinition toToolDefinition(final ToolSpecification toolSpecification) {
    final ToolDefinition toolDefinition =
        ToolDefinition.builder()
            .name(toolSpecification.name())
            .description(toolSpecification.description())
            .inputSchema(toolSpecification.getParametersAsString())
            .build();
    return toolDefinition;
  }
}
