/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.mcpserver.utility;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.DriverManager;
import schemacrawler.schema.Catalog;
import us.fatehi.utility.UtilityMarker;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@UtilityMarker
public class EmptyFactory {

  public static Catalog createEmptyCatalog(final Exception e) {
    final String baseErrorMessage =
        """
        The SchemaCrawler AI MCP server is in an error state.
        Database schema metadata is not available,
        since it could not make a connection to the database.
        Correct the error, and restart the server.
        """
            .strip()
            .trim();
    final String errorMessage =
        e != null ? baseErrorMessage + "\n" + e.getMessage() : baseErrorMessage;

    final InvocationHandler handler =
        (proxy, method, args) -> {
          final String methodName = method.getName();

          switch (methodName) {
            case "getName":
            case "getFullName":
            case "toString":
              return "empty-catalog";
            case "equals":
              return proxy == args[0];
            case "hashCode":
              return System.identityHashCode(proxy);
            case null:
            default:
              throw new IllegalStateException(errorMessage);
          }
        };

    return (Catalog)
        Proxy.newProxyInstance(
            Catalog.class.getClassLoader(), new Class<?>[] {Catalog.class}, handler);
  }

  public static DatabaseConnectionSource createEmptyDatabaseConnectionSource() {

    final InvocationHandler handler =
        (proxy, method, args) -> {
          switch (method.getName()) {
            case "get":
              return DriverManager.getConnection("jdbc:hsqldb::memory:");
            case "releaseConnection":
              return true;
            case "close":
            case "setFirstConnectionInitializer":
              return null; // No-op
            default:
              throw new UnsupportedOperationException("Method not supported: " + method.getName());
          }
        };

    return (DatabaseConnectionSource)
        Proxy.newProxyInstance(
            DatabaseConnectionSource.class.getClassLoader(),
            new Class<?>[] {DatabaseConnectionSource.class},
            handler);
  }
}
