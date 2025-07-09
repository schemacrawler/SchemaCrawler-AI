package schemacrawler.tools.command.aichat.mcp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot application for the SchemaCrawler AI MCP server. This class enables the Spring AI MCP
 * server capabilities.
 */
@SpringBootApplication
public class SseMcpServer {

  public static void main(final String[] args) {
    start();
  }

  public static void start() {
    SpringApplication app = new SpringApplication(SseMcpServer.class);
    app.setAdditionalProfiles("sse");
    app.run();
  }
}
