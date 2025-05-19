package schemacrawler.tools.command.aichat.mcp;

import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * Spring Boot application for the SchemaCrawler AI MCP server. This class enables the Spring AI MCP
 * server capabilities.
 */
@SpringBootApplication
public class SchemaCrawlerMCPServer {

  public static void main(final String[] args) {
    SpringApplication.run(SchemaCrawlerMCPServer.class, args);
  }

  @Bean
  public ToolCallbackProvider schemaCrawlerTools() {
    final List<ToolCallback> tools = SpringAIUtility.toolCallbacks(SpringAIUtility.tools());
    final ToolCallbackProvider toolCallbackProvider = ToolCallbackProvider.from(tools);
    return toolCallbackProvider;
  }

  @Bean
  public ToolCallbackProvider weatherTools(final CommonService weatherService) {
    final MethodToolCallbackProvider toolCallbackProvider =
        MethodToolCallbackProvider.builder().toolObjects(weatherService).build();
    return toolCallbackProvider;
  }
}
