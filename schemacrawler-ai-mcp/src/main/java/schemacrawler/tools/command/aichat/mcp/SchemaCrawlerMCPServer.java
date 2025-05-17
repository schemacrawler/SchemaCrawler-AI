package schemacrawler.tools.command.aichat.mcp;

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
}
