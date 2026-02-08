/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.test.utility;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.DriverManager;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import schemacrawler.ermodel.model.ERModel;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.CrawlInfo;
import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schema.JdbcDriverInfo;
import us.fatehi.utility.UtilityMarker;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.property.BaseProductVersion;
import us.fatehi.utility.property.ProductVersion;

@UtilityMarker
public class AiTestObjectUtility {

  public static DatabaseInfo makeDatabaseInfo() {
    final Class<DatabaseInfo> clazz = DatabaseInfo.class;
    final InvocationHandler handler =
        (proxy, method, args) -> {
          final String methodName = method.getName();

          return switch (methodName) {
            case "getDatabaseProductName", "getDatabaseProductVersion" -> "fake";
            default -> returnEmpty(method);
          };
        };

    return (DatabaseInfo)
        Proxy.newProxyInstance(
            AiTestObjectUtility.class.getClassLoader(), new Class<?>[] {clazz}, handler);
  }

  public static Catalog makeTestCatalog() {
    final InvocationHandler handler =
        (proxy, method, args) -> {
          final String methodName = method.getName();

          return switch (methodName) {
            case "getName", "getFullName", "toString" -> "empty-catalog";
            case "equals" -> proxy == args[0];
            case "hashCode" -> System.identityHashCode(proxy);
            case "getCrawlInfo" -> makeTestObject(CrawlInfo.class);
            case "getJdbcDriverInfo" -> makeTestObject(JdbcDriverInfo.class);
            case "getDatabaseInfo" -> makeDatabaseInfo();
            default -> returnEmpty(method);
          };
        };

    return (Catalog)
        Proxy.newProxyInstance(
            Catalog.class.getClassLoader(), new Class<?>[] {Catalog.class}, handler);
  }

  public static DatabaseConnectionSource makeTestDatabaseConnectionSource() {

    final InvocationHandler handler =
        (proxy, method, args) ->
            (switch (method.getName()) {
              case "get" -> DriverManager.getConnection("jdbc:hsqldb:mem:testdb");
              case "releaseConnection" -> true;
              case "toString" -> "empty-data-source";
              default -> returnEmpty(method);
            });

    return (DatabaseConnectionSource)
        Proxy.newProxyInstance(
            DatabaseConnectionSource.class.getClassLoader(),
            new Class<?>[] {DatabaseConnectionSource.class},
            handler);
  }

  public static ERModel makeTestERModel() {

    final InvocationHandler handler =
        (proxy, method, args) ->
            (switch (method.getName()) {
              case "toString" -> "empty-ermodel";
              case "equals" -> proxy == args[0];
              case "hashCode" -> System.identityHashCode(proxy);
              default -> returnEmpty(method);
            });

    return (ERModel)
        Proxy.newProxyInstance(
            ERModel.class.getClassLoader(), new Class<?>[] {ERModel.class}, handler);
  }

  public static <T extends Object> T makeTestObject(final Class<T> clazz) {
    final InvocationHandler handler =
        (proxy, method, args) -> {
          final String methodName = method.getName();

          return switch (methodName) {
            default -> returnEmpty(method);
          };
        };

    return (T)
        Proxy.newProxyInstance(
            AiTestObjectUtility.class.getClassLoader(), new Class<?>[] {clazz}, handler);
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
    if (ProductVersion.class.isAssignableFrom(returnType)) {
      return new BaseProductVersion("fake", "fake");
    }
    throw new UnsupportedOperationException(method.toString());
  }
}
