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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import schemacrawler.ermodel.model.ERModel;
import schemacrawler.schema.Catalog;
import us.fatehi.utility.UtilityMarker;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@UtilityMarker
public final class InErrorFactory {

  private static final String errorMessage =
      """
      The SchemaCrawler AI MCP server is in an error state.
      Database schema metadata is not available,
      since it could not make a connection to the database.
      Check the server error logs, correct the error, and
      restart the server.
      """
          .strip()
          .trim();

  public static Catalog createErroredCatalog() {

    final InvocationHandler handler =
        (proxy, method, args) ->
            switch (method.getName()) {
              case "getName", "getFullName", "toString" -> "empty-catalog"; // For debugging
              case "equals" -> proxy == args[0];
              case "hashCode" -> System.identityHashCode(proxy);
              default -> returnEmpty(method);
            };

    return (Catalog)
        Proxy.newProxyInstance(
            Catalog.class.getClassLoader(), new Class<?>[] {Catalog.class}, handler);
  }

  public static DatabaseConnectionSource createErroredConnectionSource() {

    final InvocationHandler handler =
        (proxy, method, args) ->
            switch (method.getName()) {
              case "get" -> DriverManager.getConnection("jdbc:hsqldb:mem:testdb");
              case "releaseConnection" -> true;
              case "toString" -> "empty-data-source"; // For debugging
              case "equals" -> proxy == args[0];
              case "hashCode" -> System.identityHashCode(proxy);
              default -> returnEmpty(method);
            };

    return (DatabaseConnectionSource)
        Proxy.newProxyInstance(
            DatabaseConnectionSource.class.getClassLoader(),
            new Class<?>[] {DatabaseConnectionSource.class},
            handler);
  }

  public static ERModel createErroredERModel() {

    final InvocationHandler handler =
        (proxy, method, args) -> {
          method.getReturnType();

          return switch (method.getName()) {
            case "toString" -> "empty-ermodel"; // For debugging
            case "equals" -> proxy == args[0];
            case "hashCode" -> System.identityHashCode(proxy);
            default -> returnEmpty(method);
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
    if (Map.class.isAssignableFrom(returnType)) {
      return Map.of();
    }
    if (Set.class.isAssignableFrom(returnType)) {
      return Set.of();
    }
    if (Collection.class.isAssignableFrom(returnType)) {
      return List.of();
    }
    if (Iterator.class.isAssignableFrom(returnType)) {
      return List.of().iterator();
    }

    // Handle primitives
    if (returnType == boolean.class) {
      return false;
    }
    if (returnType == int.class) {
      return 0;
    }
    if (returnType == long.class) {
      return 0L;
    }
    if (returnType == double.class) {
      return 0.0d;
    }
    if (returnType == float.class) {
      return 0.0f;
    }
    if (returnType == byte.class) {
      return (byte) 0;
    }
    if (returnType == short.class) {
      return (short) 0;
    }
    if (returnType == char.class) {
      return '\0';
    }

    // Handle primitive wrappers
    if (returnType == Boolean.class) {
      return Boolean.FALSE;
    }
    if (returnType == Integer.class) {
      return Integer.valueOf(0);
    }
    if (returnType == Long.class) {
      return Long.valueOf(0L);
    }
    if (returnType == Double.class) {
      return Double.valueOf(0.0d);
    }
    if (returnType == Float.class) {
      return Float.valueOf(0.0f);
    }
    if (returnType == Byte.class) {
      return Byte.valueOf((byte) 0);
    }
    if (returnType == Short.class) {
      return Short.valueOf((short) 0);
    }
    if (returnType == Character.class) {
      return Character.valueOf('\0');
    }
    if (returnType == String.class) {
      return "";
    }

    throw new UnsupportedOperationException(method.toString());
  }

  private InErrorFactory() {
    // Prevent instantiation
  }
}
