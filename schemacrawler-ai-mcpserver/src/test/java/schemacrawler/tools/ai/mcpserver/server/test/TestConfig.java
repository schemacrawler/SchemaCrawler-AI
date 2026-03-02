package schemacrawler.tools.ai.mcpserver.server.test;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import schemacrawler.tools.ai.mcpserver.ExcludeTools;
import schemacrawler.tools.ai.mcpserver.McpServerTransportType;

@TestConfiguration
public class TestConfig {
  @Bean
  ExcludeTools excludeTools() {
    return new ExcludeTools();
  }

  @Bean
  boolean isInErrorState() {
    return false;
  }

  @Bean
  boolean isOffline() {
    return false;
  }

  @Bean
  McpServerTransportType mcpTransport() {
    return McpServerTransportType.http;
  }
}
