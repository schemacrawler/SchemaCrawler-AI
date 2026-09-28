/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.mcpserver.server;

import static java.util.Objects.requireNonNullElseGet;
import static us.fatehi.utility.Utility.isBlank;
import static us.fatehi.utility.Utility.trimToEmpty;

import us.fatehi.utility.jdbc.serverfingerprint.DatabaseServerFingerprint;
import us.fatehi.utility.property.ProductVersion;

/**
 * Identity of the database described by a server instance. Values that are not set are blank, and
 * are left out of the JSON views.
 */
public record DatabaseIdentity(
    String alias,
    String description,
    ProductVersion databaseProduct,
    DatabaseServerFingerprint serverFingerprint) {

  private static final class EmptyProductVersion implements ProductVersion {
    /** */
    private static final long serialVersionUID = 5413473984636621997L;

    @Override
    public String getDescription() {
      return "";
    }

    @Override
    public String getName() {
      return "";
    }

    @Override
    public Object getValue() {
      return "";
    }
  }

  private static final DatabaseIdentity EMPTY = new DatabaseIdentity(null, null, null, null);

  public static DatabaseIdentity empty() {
    return EMPTY;
  }

  public DatabaseIdentity {
    alias = trimToEmpty(alias);
    description = trimToEmpty(description);
    databaseProduct = requireNonNullElseGet(databaseProduct, EmptyProductVersion::new);
    serverFingerprint = requireNonNullElseGet(serverFingerprint, DatabaseServerFingerprint::new);
  }

  /** True when there is nothing to report in tool results. */
  public boolean isEmpty() {
    return isBlank(alias)
        && isBlank(databaseProduct.getName())
        && isBlank(serverFingerprint.databaseSystemIdentifier());
  }
}
