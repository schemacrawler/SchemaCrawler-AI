/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.tools.command.chatgpt.utility;

import static java.util.Objects.requireNonNull;
import java.sql.Connection;
import java.util.function.Consumer;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.datasource.PooledConnectionUtility;

public final class ConnectionDatabaseConnectionSource implements DatabaseConnectionSource {

  private final Connection connection;

  public ConnectionDatabaseConnectionSource(final Connection connection) {
    this.connection = requireNonNull(connection, "No connection provided");
  }

  @Override
  public void close() throws Exception {
    connection.close();
  }

  @Override
  public Connection get() {
    // Do not close this connection
    return PooledConnectionUtility.newPooledConnection(connection, this);
  }

  @Override
  public boolean releaseConnection(final Connection connection) {
    // No-op
    return true;
  }

  @Override
  public void setFirstConnectionInitializer(final Consumer<Connection> connectionInitializer) {
    // No-op
  }
}
