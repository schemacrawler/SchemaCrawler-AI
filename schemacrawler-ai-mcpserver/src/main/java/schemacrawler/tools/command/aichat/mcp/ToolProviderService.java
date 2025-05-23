package schemacrawler.tools.command.aichat.mcp;

import java.util.List;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

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
  public ToolCallbackProvider commonTools(final CommonService commonService) {
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
}
