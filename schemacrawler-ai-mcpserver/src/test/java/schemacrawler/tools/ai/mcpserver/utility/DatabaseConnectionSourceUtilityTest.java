/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.mcpserver.utility;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import org.junit.jupiter.api.Test;
import schemacrawler.tools.offline.jdbc.OfflineConnection;
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
    final DatabaseConnectionSource connectionSource = mock(DatabaseConnectionSource.class);
    final Connection connection = mock(Connection.class);
    when(connectionSource.get()).thenReturn(connection);
    when(connection.isClosed()).thenReturn(false);

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
    final DatabaseConnectionSource connectionSource = mock(DatabaseConnectionSource.class);
    final Connection connection = mock(Connection.class);
    // First call to canConnect succeeds
    // Second call to isOffline fails
    when(connectionSource.get()).thenReturn(connection).thenThrow(new RuntimeException("Error"));
    when(connection.isClosed()).thenReturn(false);

    assertThat(DatabaseConnectionSourceUtility.isOffline(connectionSource), is(false));
  }

  @Test
  public void testIsOfflineFalse() throws Exception {
    final DatabaseConnectionSource connectionSource = mock(DatabaseConnectionSource.class);
    final Connection connection = mock(Connection.class);
    when(connectionSource.get()).thenReturn(connection);
    when(connection.isClosed()).thenReturn(false);
    when(connection.unwrap(Connection.class)).thenReturn(connection);

    assertThat(DatabaseConnectionSourceUtility.isOffline(connectionSource), is(false));
  }

  @Test
  public void testIsOfflineNull() {
    assertThat(DatabaseConnectionSourceUtility.isOffline(null), is(true));
  }

  @Test
  public void testIsOfflineTrue() throws Exception {
    final DatabaseConnectionSource connectionSource = mock(DatabaseConnectionSource.class);
    final OfflineConnection connection = mock(OfflineConnection.class);
    when(connectionSource.get()).thenReturn(connection);
    when(connection.isClosed()).thenReturn(false);
    when(connection.unwrap(Connection.class)).thenReturn(connection);

    assertThat(DatabaseConnectionSourceUtility.isOffline(connectionSource), is(true));
  }
}
