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
import schemacrawler.schema.Catalog;

public class CatalogFactory {

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
}
