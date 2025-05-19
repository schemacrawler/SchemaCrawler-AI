package schemacrawler.tools.command.aichat.mcp;

import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * Spring Boot application for the SchemaCrawler AI MCP server.
 * This class enables the Spring AI MCP server capabilities.
 */
@SpringBootApplication
public class SchemaCrawlerMCPServer {

  public static void main(String[] args) {
    SpringApplication.run(SchemaCrawlerMCPServer.class, args);
  }

  @Bean
  public ToolCallbackProvider weatherTools(WeatherService weatherService) {
    MethodToolCallbackProvider toolCallbackProvider = MethodToolCallbackProvider.builder().toolObjects(weatherService).build();
    printTools(toolCallbackProvider);
    return toolCallbackProvider;
  }

  @Bean
  public ToolCallbackProvider schemaCrawlerTools() {
    List<ToolCallback> tools = SpringAIUtility.toolCallbacks(SpringAIUtility.tools());
    List<ToolCallback> tools1 = tools.stream().filter(toolCallback -> toolCallback.getToolDefinition().name().equals("lint")).collect(java.util.stream.Collectors.toList());
    ToolCallbackProvider toolCallbackProvider = ToolCallbackProvider.from(tools1);
    printTools(toolCallbackProvider);
    return toolCallbackProvider;
  }

  private void printTools(ToolCallbackProvider toolCallbackProvider) {
    List.of(toolCallbackProvider.getToolCallbacks()).forEach(toolCallback -> {
      System.out.println(toolCallback.getToolDefinition().name());
      System.out.println(toolCallback.getToolDefinition().description());
      System.out.println(toolCallback.getToolDefinition().inputSchema());
      System.out.println("----------------------------------------");
    });
  }

}
