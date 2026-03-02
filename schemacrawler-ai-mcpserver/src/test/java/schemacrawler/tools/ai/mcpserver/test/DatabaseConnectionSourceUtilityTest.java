/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.mcpserver.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import org.junit.jupiter.api.Test;
import schemacrawler.tools.ai.mcpserver.utility.DatabaseConnectionSourceUtility;
import schemacrawler.tools.offline.jdbc.OfflineConnection;
import us.fatehi.test.utility.TestObjectUtility;
import us.fatehi.utility.datasource.ConnectionDatabaseConnectionSource;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

public class DatabaseConnectionSourceUtilityTest {

  @Test
  public void testCanConnectInvalid() throws Exception {
    final DatabaseConnectionSource connectionSource = mock(DatabaseConnectionSource.class);
    when(connectionSource.get()).thenThrow(new RuntimeException("Connection failed"));

    assertThat(DatabaseConnectionSourceUtility.canConnect(connectionSource), is(false));
  }

  @Test
  public void testCanConnectNull() {
    assertThat(DatabaseConnectionSourceUtility.canConnect(null), is(false));
  }

  @Test
  public void testCanConnectValid() throws Exception {
    final Connection connection = TestObjectUtility.mockConnection();
    final DatabaseConnectionSource connectionSource =
        new ConnectionDatabaseConnectionSource(connection);

    assertThat(DatabaseConnectionSourceUtility.canConnect(connectionSource), is(true));
  }

  @Test
  public void testIsOfflineCannotConnect() throws Exception {
    final DatabaseConnectionSource connectionSource = mock(DatabaseConnectionSource.class);
    when(connectionSource.get()).thenThrow(new RuntimeException("Connection failed"));

    assertThat(DatabaseConnectionSourceUtility.isOffline(connectionSource), is(true));
  }

  @Test
  public void testIsOfflineException() throws Exception {
    final Connection connection = TestObjectUtility.mockConnection();
    final DatabaseConnectionSource connectionSource = mock(DatabaseConnectionSource.class);
    when(connectionSource.get()).thenReturn(connection).thenThrow(new RuntimeException("Error"));

    assertThat(DatabaseConnectionSourceUtility.isOffline(connectionSource), is(false));
  }

  @Test
  public void testIsOfflineFalse() throws Exception {
    final Connection connection = TestObjectUtility.mockConnection();
    final DatabaseConnectionSource connectionSource =
        new ConnectionDatabaseConnectionSource(connection);
    when(connection.unwrap(Connection.class)).thenReturn(connection);

    assertThat(DatabaseConnectionSourceUtility.isOffline(connectionSource), is(false));
  }

  @Test
  public void testIsOfflineNull() {
    assertThat(DatabaseConnectionSourceUtility.isOffline(null), is(true));
  }

  @Test
  public void testIsOfflineTrue() throws Exception {
    final Connection connection = mock(OfflineConnection.class);
    final DatabaseConnectionSource connectionSource =
        new ConnectionDatabaseConnectionSource(connection);
    when(connection.unwrap(Connection.class)).thenReturn(connection);

    assertThat(DatabaseConnectionSourceUtility.isOffline(connectionSource), is(true));
  }
}
