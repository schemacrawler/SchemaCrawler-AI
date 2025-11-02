/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.mcpserver.server.test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static schemacrawler.tools.ai.utility.JsonUtility.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.ai.mcpserver.ExcludeTools;
import schemacrawler.tools.ai.mcpserver.McpServerTransportType;
import schemacrawler.tools.ai.mcpserver.server.HeartbeatLogger;
import schemacrawler.tools.ai.mcpserver.server.ServerHealth;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@TestInstance(Lifecycle.PER_CLASS)
@SpringJUnitConfig(classes = {HeartbeatLogger.class, HeartbeatLoggerTest.MockConfig.class})
@TestPropertySource(properties = {"server.heartbeat=true"})
public class HeartbeatLoggerTest {

  @TestConfiguration
  static class MockConfig {
    @Bean
    Catalog catalog() {
      return mock(Catalog.class);
    }

    @Bean
    DatabaseConnectionSource databaseConnectionSource() {
      return mock(DatabaseConnectionSource.class);
    }

    @Bean
    ExcludeTools excludeTools() {
      return new ExcludeTools();
    }

    @Bean
    boolean isInErrorState() {
      return false;
    }

    @Bean
    McpServerTransportType mcpTransport() {
      return McpServerTransportType.http;
    }

    @Bean
    ServerHealth serverHealth() {
      final ServerHealth serverHealth = mock(ServerHealth.class);
      when(serverHealth.currentState()).thenReturn(currentState());
      return serverHealth;
    }
  }

  static Map<String, Object> currentState() {
    final Map<String, Object> state = new HashMap<>();
    state.put("_server", "Test Server");
    state.put("some-junk", true);
    return state;
  }

  @Autowired private HeartbeatLogger heartbeatLogger;

  @Test
  @DisplayName("HeartbeatLogger should log valid JSON when heartbeat is enabled")
  public void testHeartbeatLogsValidJson() throws Exception {
    final Logger logger = Logger.getLogger(HeartbeatLogger.class.getCanonicalName());

    final List<String> messages = new ArrayList<>();

    final Handler handler =
        new Handler() {
          @Override
          public void close() throws SecurityException {}

          @Override
          public void flush() {}

          @Override
          public void publish(final LogRecord record) {
            if (record.getLevel().intValue() >= Level.INFO.intValue()) {
              messages.add(record.getMessage());
            }
          }
        };

    logger.addHandler(handler);
    final Level previousLevel = logger.getLevel();
    try {
      logger.setLevel(Level.INFO);

      // Act
      heartbeatLogger.logHeartbeat();

      // Assert
      assertThat("A heartbeat message should be logged", messages.isEmpty(), is(false));

      // -- Validate the message
      final String currentStatusJson = messages.get(messages.size() - 1);
      final JsonNode node = mapper.readTree(currentStatusJson);
      assertThat("Parsed JSON should not be null", node, notNullValue());

      final Map<String, Object> currentStateMap = mapper.convertValue(node, HashMap.class);
      assertThat("Current state should match", currentStateMap, equalTo(currentState()));
    } finally {
      logger.removeHandler(handler);
      logger.setLevel(previousLevel);
    }
  }
}
