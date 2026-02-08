/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.mcpserver.utility;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.DriverManager;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import schemacrawler.ermodel.model.ERModel;
import schemacrawler.schema.Catalog;
import us.fatehi.utility.UtilityMarker;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@UtilityMarker
public class EmptyFactory {

  private static final String baseErrorMessage =
      """
      The SchemaCrawler AI MCP server is in an error state.
      Database schema metadata is not available,
      since it could not make a connection to the database.
      Correct the error, and restart the server.
      """
          .strip()
          .trim();

  public static Catalog createEmptyCatalog(final Exception e) {
    final String errorMessage =
        e != null ? baseErrorMessage + "\n" + e.getMessage() : baseErrorMessage;

    final InvocationHandler handler =
        (proxy, method, args) -> {
          final String methodName = method.getName();

          return switch (methodName) {
            case "getName", "getFullName", "toString" -> "empty-catalog";
            case "equals" -> proxy == args[0];
            case "hashCode" -> System.identityHashCode(proxy);
            default -> throw new IllegalStateException(errorMessage);
          };
        };

    return (Catalog)
        Proxy.newProxyInstance(
            Catalog.class.getClassLoader(), new Class<?>[] {Catalog.class}, handler);
  }

  public static DatabaseConnectionSource createEmptyDatabaseConnectionSource() {

    final InvocationHandler handler =
        (proxy, method, args) ->
            (switch (method.getName()) {
              case "get" -> DriverManager.getConnection("jdbc:hsqldb:mem:testdb");
              case "releaseConnection" -> true;
              case "toString" -> "empty-data-source"; // For debugging
              default -> returnEmpty(method);
            });

    return (DatabaseConnectionSource)
        Proxy.newProxyInstance(
            DatabaseConnectionSource.class.getClassLoader(),
            new Class<?>[] {DatabaseConnectionSource.class},
            handler);
  }

  public static ERModel createEmptyERModel() {

    final InvocationHandler handler =
        (proxy, method, args) -> {
          method.getReturnType();

          return switch (method.getName()) {
            case "toString" -> "empty-ermodel";
            case "equals" -> proxy == args[0];
            case "hashCode" -> System.identityHashCode(proxy);
            default -> throw new IllegalStateException(baseErrorMessage);
          };
        };

    return (ERModel)
        Proxy.newProxyInstance(
            ERModel.class.getClassLoader(), new Class<?>[] {ERModel.class}, handler);
  }

  private static Object returnEmpty(final Method method) {
    if (method == null) {
      return null;
    }

    final Class<?> returnType = method.getReturnType();

    if (returnType == Void.TYPE) {
      return null;
    }
    if (Optional.class.isAssignableFrom(returnType)) {
      return Optional.empty();
    }
    if (Collection.class.isAssignableFrom(returnType)) {
      return Collections.emptyList();
    }
    throw new UnsupportedOperationException(method.toString());
  }
}
