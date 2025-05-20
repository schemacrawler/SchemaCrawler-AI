package schemacrawler.tools.command.aichat.mcp;

import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.command.aichat.utility.ConnectionDatabaseConnectionSource;

import java.sql.Connection;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * Spring Boot application for the SchemaCrawler AI MCP server. This class enables the Spring AI MCP
 * server capabilities.
 */
@SpringBootApplication
public class SchemaCrawlerMcpServer {
  public static void main(final String[] args) {
    start();
  }

  public static void start() {
    SpringApplication.run(SchemaCrawlerMcpServer.class);
  }
}
