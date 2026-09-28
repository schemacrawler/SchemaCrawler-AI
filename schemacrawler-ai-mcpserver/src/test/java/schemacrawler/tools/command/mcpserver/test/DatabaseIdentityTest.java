/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.command.mcpserver.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;
import schemacrawler.tools.ai.mcpserver.server.DatabaseIdentity;
import us.fatehi.utility.jdbc.serverfingerprint.DatabaseServerFingerprint;
import us.fatehi.utility.jdbc.serverfingerprint.FingerprintConfidence;
import us.fatehi.utility.jdbc.serverfingerprint.HostClassification;

public class DatabaseIdentityTest {

  private static final DatabaseServerFingerprint SERVER_FINGERPRINT =
      new DatabaseServerFingerprint(
          "postgresql", HostClassification.INTERNAL, "a1b2c3", FingerprintConfidence.MEDIUM);

  @Test
  public void blankValuesAreEmpty() {
    final DatabaseIdentity identity = new DatabaseIdentity("  ", "\t", null, null);

    assertThat(identity.alias(), is(""));
    assertThat(identity.description(), is(""));
    assertThat(identity.databaseProduct().getName(), is(""));
    assertThat(identity.serverFingerprint(), is(notNullValue()));
    assertThat(identity.serverFingerprint().fingerprint(), is(""));
    assertThat(identity.isEmpty(), is(true));
  }

  @Test
  public void descriptionOnlyIsEmpty() {
    assertThat(new DatabaseIdentity(null, "CRM", null, null).isEmpty(), is(true));
  }

  @Test
  public void empty() {
    assertThat(DatabaseIdentity.empty().isEmpty(), is(true));
  }

  @Test
  public void notEmpty() {
    assertThat(new DatabaseIdentity("crm-prod", null, null, null).isEmpty(), is(false));
    assertThat(new DatabaseIdentity(null, null, null, null).isEmpty(), is(true));
    assertThat(new DatabaseIdentity(null, null, null, SERVER_FINGERPRINT).isEmpty(), is(true));
  }

  @Test
  public void trimmedValues() {
    final DatabaseIdentity identity =
        new DatabaseIdentity(" crm-prod ", " CRM ", null, SERVER_FINGERPRINT);

    assertThat(identity.alias(), is("crm-prod"));
    assertThat(identity.description(), is("CRM"));
    assertThat(identity.databaseProduct().getName(), is(""));
    assertThat(identity.serverFingerprint().fingerprint(), is("a1b2c3"));
  }
}
