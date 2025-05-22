package schemacrawler.tools.command.aichat.mcp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import schemacrawler.tools.command.aichat.mcp.SpringAIToolUtility;

/**
 * Spring Boot application for the SchemaCrawler AI MCP server. This class enables the Spring AI MCP
 * server capabilities.
 */
@SpringBootApplication
public class SchemaCrawlerMcpServer {

  // Set isDryRun to true in a static initializer block so it's always set before any tests run
  static {
    SpringAIToolUtility.isDryRun = true;
  }

  public static void main(final String[] args) {
    start();
  }

  public static void start() {
    SpringApplication.run(SchemaCrawlerMcpServer.class);
  }
}
