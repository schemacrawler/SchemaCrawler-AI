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

import schemacrawler.schema.Catalog;
import schemacrawler.schema.CrawlInfo;
import schemacrawler.schema.DatabaseInfo;
import schemacrawler.tools.ai.tools.DatabaseIdentity;
import tools.jackson.databind.node.ObjectNode;
import us.fatehi.utility.UtilityMarker;
import us.fatehi.utility.jdbc.serverfingerprint.DatabaseServerFingerprint;
import us.fatehi.utility.jdbc.serverfingerprint.FingerprintConfidence;
import us.fatehi.utility.property.BaseProductVersion;
import us.fatehi.utility.property.ProductVersion;

@UtilityMarker
public final class DatabaseIdentityUtility {

  public static DatabaseIdentity from(
      final String alias, final String description, final Catalog catalog) {
    ProductVersion databaseProduct = null;
    DatabaseServerFingerprint serverFingerprint = null;
    if (catalog != null) {
      final DatabaseInfo databaseInfo = catalog.getDatabaseInfo();
      if (databaseInfo != null) {
        databaseProduct = new BaseProductVersion(databaseInfo);
      }
      final CrawlInfo crawlInfo = catalog.getCrawlInfo();
      if (crawlInfo != null) {
        serverFingerprint = crawlInfo.getDatabaseServerFingerprint();
      }
    }
    return new DatabaseIdentity(alias, description, databaseProduct, serverFingerprint);
  }

  /** Detailed identity, for the about database tool. */
  public static ObjectNode toDetailNode(final DatabaseIdentity identity) {
    requireNonNull(identity, "No database identity provided");

    final ObjectNode node = toResultNode(identity);
    putIfNotBlank(node, "description", identity.description());
    final DatabaseServerFingerprint serverFingerprint = identity.serverFingerprint();
    if (serverFingerprint.confidence() == FingerprintConfidence.HIGH) {
      node.putPOJO("database-server-fingerprint", serverFingerprint);
    }

    return node;
  }

  /** Identity block that is added to every tool result. */
  public static ObjectNode toResultNode(final DatabaseIdentity identity) {
    requireNonNull(identity, "No database identity provided");

    final ObjectNode node = mapper.createObjectNode();
    putIfNotBlank(node, "alias", identity.alias());
    putIfNotBlank(node, "database-product", identity.databaseProduct().getName());
    return node;
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
