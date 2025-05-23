package schemacrawler.tools.command.aichat.mcp.server;

import java.sql.Connection;
import java.util.Objects;
import java.util.logging.Logger;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.command.aichat.utility.FunctionToolExecutor;

public final class SpringAIToolCallback implements ToolCallback {

  private static final Logger LOGGER =
      Logger.getLogger(SpringAIToolCallback.class.getCanonicalName());

  private final boolean isDryRun;
  private final ToolDefinition toolDefinition;
  private final FunctionToolExecutor functionToolExecutor;

  public SpringAIToolCallback(
      final boolean isDryRun,
      final ToolDefinition toolDefinition,
      final Catalog catalog,
      final Connection connection) {
    this.isDryRun = isDryRun;
    this.toolDefinition = Objects.requireNonNull(toolDefinition, "No tool definition provided");
    if (!isDryRun) {
      functionToolExecutor = new FunctionToolExecutor(toolDefinition.name(), catalog, connection);
    } else {
      functionToolExecutor = null;
    }
  }

  @Override
  public String call(final String toolInput) {
    if (!StringUtils.hasText(toolInput)) {
      return "";
    }

    final String callMessage = String.format("Call to <%s>%n%s", toolDefinition.name(), toolInput);
    LOGGER.info(callMessage);

    if (isDryRun) {
      return callMessage;
    }
    return functionToolExecutor.execute(toolInput);
  }

  @Override
  public String call(final String toolInput, @Nullable final ToolContext tooContext) {
    return call(toolInput);
  }

  @Override
  public ToolDefinition getToolDefinition() {
    return toolDefinition;
  }
}
