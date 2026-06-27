/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.mcpserver;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.event.ContextRefreshedEvent;

public class LoggingRestorationListenerTest {

  private Level originalScLevel;
  private Level originalUtLevel;

  @AfterEach
  public void restoreLogLevels() {
    Logger.getLogger("schemacrawler").setLevel(originalScLevel);
    Logger.getLogger("us.fatehi").setLevel(originalUtLevel);
  }

  @BeforeEach
  public void setupLogLevels() {
    originalScLevel = Logger.getLogger("schemacrawler").getLevel();
    originalUtLevel = Logger.getLogger("us.fatehi").getLevel();
  }

  @Test
  public void testConstructorWithNullLogLevel() {
    // Should not throw - null is allowed
    final LoggingRestorationListener listener = new LoggingRestorationListener(null);

    assertThat(listener, instanceOf(LoggingRestorationListener.class));
  }

  @Test
  public void testConstructorWithValidLogLevel() {
    final Level logLevel = Level.FINE;
    final LoggingRestorationListener listener = new LoggingRestorationListener(logLevel);

    assertThat(listener, instanceOf(LoggingRestorationListener.class));
  }

  @Test
  public void testMultipleContextRefreshEvents() {
    final Level logLevel = Level.WARNING;
    final LoggingRestorationListener listener = new LoggingRestorationListener(logLevel);

    final ContextRefreshedEvent event = mock(ContextRefreshedEvent.class);

    // Should be idempotent - calling multiple times is safe
    assertDoesNotThrow(
        () -> {
          listener.onContextRefreshed(event);
          listener.onContextRefreshed(event);
          listener.onContextRefreshed(event);
        });

    assertThat(Logger.getLogger("schemacrawler").getLevel(), is(logLevel));
  }

  @Test
  public void testOnContextRefreshedWithDifferentLevels() {
    final Level[] levels = {
      Level.OFF, Level.SEVERE, Level.WARNING, Level.INFO, Level.FINE, Level.FINER, Level.FINEST
    };

    for (final Level level : levels) {
      final LoggingRestorationListener listener = new LoggingRestorationListener(level);

      final ContextRefreshedEvent event = mock(ContextRefreshedEvent.class);
      listener.onContextRefreshed(event);

      assertThat(Logger.getLogger("schemacrawler").getLevel(), is(level));
      assertThat(Logger.getLogger("us.fatehi").getLevel(), is(level));
    }
  }

  @Test
  public void testOnContextRefreshedWithNullLevel() {
    final LoggingRestorationListener listener = new LoggingRestorationListener(null);

    final ContextRefreshedEvent event = mock(ContextRefreshedEvent.class);

    // Should not throw
    assertDoesNotThrow(() -> listener.onContextRefreshed(event));
  }

  @Test
  public void testOnContextRefreshedWithValidLevel() {
    final Level logLevel = Level.FINE;
    final LoggingRestorationListener listener = new LoggingRestorationListener(logLevel);

    final ContextRefreshedEvent event = mock(ContextRefreshedEvent.class);
    listener.onContextRefreshed(event);

    // Verify log levels were set
    assertThat(Logger.getLogger("schemacrawler").getLevel(), is(logLevel));
    assertThat(Logger.getLogger("us.fatehi").getLevel(), is(logLevel));
  }
}
