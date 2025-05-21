package schemacrawler.tools.command.aichat.mcp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



/**
 * Spring Boot application for the SchemaCrawler AI MCP server. This class enables the Spring AI MCP
 * server capabilities.
 */
@SpringBootApplication
public class SchemaCrawlerMcpServer {

  public static void main(final String[] args) {
    SpringAIUtility.isDryRun = true;
    start();
  }

  public static void start() {
    SpringApplication.run(SchemaCrawlerMcpServer.class);
  }
}
