/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.utility;

import static java.util.Objects.requireNonNull;
import static schemacrawler.tools.ai.utility.JsonUtility.mapper;
import static us.fatehi.utility.Utility.isBlank;

import java.util.Locale;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.CrawlInfo;
import schemacrawler.schema.DatabaseInfo;
import schemacrawler.tools.ai.tools.DatabaseIdentity;
import tools.jackson.databind.node.ObjectNode;
import us.fatehi.utility.UtilityMarker;
import us.fatehi.utility.jdbc.serverfingerprint.DatabaseServerFingerprint;

@UtilityMarker
public final class DatabaseIdentityUtility {

  public static DatabaseIdentity from(
      final String alias, final String description, final Catalog catalog) {
    String databaseProductName = null;
    DatabaseServerFingerprint serverFingerprint = null;
    if (catalog != null) {
      final DatabaseInfo databaseInfo = catalog.getDatabaseInfo();
      if (databaseInfo != null) {
        databaseProductName = databaseInfo.getDatabaseProductName();
      }
      final CrawlInfo crawlInfo = catalog.getCrawlInfo();
      if (crawlInfo != null) {
        serverFingerprint = crawlInfo.getDatabaseServerFingerprint();
      }
    }
    return new DatabaseIdentity(alias, description, databaseProductName, serverFingerprint);
  }

  /** Detailed identity, for the about database tool. */
  public static ObjectNode toDetailNode(final DatabaseIdentity identity) {
    requireNonNull(identity, "No database identity provided");
    final DatabaseServerFingerprint serverFingerprint = identity.serverFingerprint();

    final ObjectNode node = mapper.createObjectNode();
    putIfNotBlank(node, "alias", identity.alias());
    putIfNotBlank(node, "description", identity.description());
    putIfNotBlank(node, "database-product-name", identity.databaseProductName());
    putIfNotBlank(node, "fingerprint", identity.fingerprint());
    putIfNotBlank(node, "database-system-identifier", serverFingerprint.databaseSystemIdentifier());
    // Host classification and confidence only qualify a fingerprint
    if (!isBlank(identity.fingerprint())) {
      if (serverFingerprint.hostClassification() != null) {
        node.put("host-classification", lowerCase(serverFingerprint.hostClassification()));
      }
      node.put("confidence", lowerCase(serverFingerprint.confidence()));
    }
    return node;
  }

  /** Identity block that is added to every tool result. */
  public static ObjectNode toResultNode(final DatabaseIdentity identity) {
    requireNonNull(identity, "No database identity provided");

    final ObjectNode node = mapper.createObjectNode();
    putIfNotBlank(node, "alias", identity.alias());
    putIfNotBlank(node, "database-product-name", identity.databaseProductName());
    putIfNotBlank(node, "fingerprint", identity.fingerprint());
    return node;
  }

  private static String lowerCase(final Enum<?> value) {
    return value.name().toLowerCase(Locale.ROOT);
  }

  private static void putIfNotBlank(final ObjectNode node, final String key, final String value) {
    if (!isBlank(value)) {
      node.put(key, value);
    }
  }

  private DatabaseIdentityUtility() {
    // Prevent instantiation
  }
}
