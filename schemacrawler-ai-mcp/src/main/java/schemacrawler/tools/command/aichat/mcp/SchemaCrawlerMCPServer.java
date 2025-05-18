package schemacrawler.tools.command.aichat.mcp;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

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
    return MethodToolCallbackProvider.builder().toolObjects(weatherService).build();
  }

}
