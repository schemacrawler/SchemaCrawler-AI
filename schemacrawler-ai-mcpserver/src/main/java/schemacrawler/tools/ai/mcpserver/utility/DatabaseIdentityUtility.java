/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.mcpserver.utility;

import static java.util.Objects.requireNonNull;
import static schemacrawler.tools.ai.utility.JsonUtility.mapper;
import static us.fatehi.utility.Utility.isBlank;

import com.oblac.nomen.Nomen;
import java.util.Map;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.CrawlInfo;
import schemacrawler.schema.DatabaseInfo;
import schemacrawler.tools.ai.mcpserver.server.DatabaseIdentity;
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
    final String databaseServerAlias;
    if (isBlank(alias)) {
      databaseServerAlias = Nomen.est().adjective().separator().noun().get();
    } else {
      databaseServerAlias = alias;
    }
    return new DatabaseIdentity(
        databaseServerAlias, description, databaseProduct, serverFingerprint);
  }

  /** Detailed identity, for the about database tool. */
  public static ObjectNode toDetailNode(final DatabaseIdentity databaseIdentity) {
    requireNonNull(databaseIdentity, "No database identity provided");

    final ObjectNode node = mapper.createObjectNode();
    putIfNotBlank(node, "alias", databaseIdentity.alias());
    putIfNotBlank(node, "description", databaseIdentity.description());
    final ProductVersion databaseProduct = databaseIdentity.databaseProduct();
    if (databaseProduct != null && !isBlank(databaseProduct.getProductName())) {
      node.set(
          "database_product",
          mapper.valueToTree(
              Map.of(
                  "name",
                  databaseProduct.getProductName(),
                  "version",
                  databaseProduct.getProductVersion())));
    }
    final DatabaseServerFingerprint serverFingerprint = databaseIdentity.serverFingerprint();
    if (serverFingerprint.confidence() == FingerprintConfidence.HIGH) {
      node.set("database_server_fingerprint", mapper.valueToTree(serverFingerprint));
    }

    return node;
  }

  /** Identity block that is added to every tool result. */
  public static ObjectNode toResultNode(final DatabaseIdentity databaseIdentity) {
    requireNonNull(databaseIdentity, "No database identity provided");

    final ObjectNode node = mapper.createObjectNode();
    putIfNotBlank(node, "alias", databaseIdentity.alias());
    putIfNotBlank(node, "database_product", databaseIdentity.databaseProduct().getName());
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
