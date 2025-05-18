package schemacrawler.tools.command.aichat.mcp;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot application for the SchemaCrawler AI MCP server.
 * This class enables the Spring AI MCP server capabilities.
 */
@SpringBootApplication
public class SchemaCrawlerMCPServer {

  public static void main(String[] args) {
    SpringApplication.run(SchemaCrawlerMCPServer.class, args);
  }

  @Tool(name = "get-weather", description = "Get the weather in a city", returnDirect = true)
  public String weather(@ToolParam(description = """
    Name of the city, including state and country.
    For example, Boston, MA, USA
    """, required = true) final String city) {
    String message = String.format("The weather in %s is sunny", city);
    System.out.printf(">>> %s", message);
    return message;
  }

}
