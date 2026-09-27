/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.tools;

import static java.util.Objects.requireNonNullElseGet;
import static us.fatehi.utility.Utility.isBlank;
import static us.fatehi.utility.Utility.trimToEmpty;

import us.fatehi.utility.jdbc.serverfingerprint.DatabaseServerFingerprint;

/**
 * Identity of the database described by a server instance. Values that are not set are blank, and
 * are left out of the JSON views.
 */
public record DatabaseIdentity(
    String alias,
    String description,
    String databaseProductName,
    DatabaseServerFingerprint serverFingerprint) {

  private static final DatabaseIdentity EMPTY = new DatabaseIdentity(null, null, null, null);

  public static DatabaseIdentity empty() {
    return EMPTY;
  }

  public DatabaseIdentity {
    alias = trimToEmpty(alias);
    description = trimToEmpty(description);
    databaseProductName = trimToEmpty(databaseProductName);
    serverFingerprint = requireNonNullElseGet(serverFingerprint, DatabaseServerFingerprint::new);
  }

  public String fingerprint() {
    return serverFingerprint.fingerprint();
  }

  /** True when there is nothing to report in tool results. */
  public boolean isEmpty() {
    return isBlank(alias) && isBlank(databaseProductName) && isBlank(fingerprint());
  }
}
