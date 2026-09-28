/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.mcpserver.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.ai.mcpserver.server.DatabaseIdentity;
import schemacrawler.tools.ai.mcpserver.utility.DatabaseIdentityUtility;
import tools.jackson.databind.node.ObjectNode;
import us.fatehi.utility.jdbc.serverfingerprint.DatabaseServerFingerprint;
import us.fatehi.utility.jdbc.serverfingerprint.FingerprintConfidence;
import us.fatehi.utility.jdbc.serverfingerprint.HostClassification;

public class DatabaseIdentityUtilityTest {

  private static final DatabaseServerFingerprint SERVER_FINGERPRINT =
      new DatabaseServerFingerprint(
          "postgresql", HostClassification.INTERNAL, "a1b2c3", FingerprintConfidence.MEDIUM);

  @Test
  public void detailNodeAllSet() {
    final DatabaseIdentity identity =
        new DatabaseIdentity("crm-prod", "CRM system of record", null, SERVER_FINGERPRINT);

    assertThat(
        DatabaseIdentityUtility.toDetailNode(identity).toString(),
        is(
            """
            {\"alias\":\"crm-prod\",\
            \"description\":\"CRM system of record\"}
            """
                .strip()));
  }

  @Test
  public void detailNodeNothingSet() {
    assertThat(DatabaseIdentityUtility.toDetailNode(DatabaseIdentity.empty()).isEmpty(), is(true));
  }

  @Test
  public void detailNodeWithoutFingerprint() {
    final DatabaseIdentity identity =
        new DatabaseIdentity(
            "", "CRM", null, new DatabaseServerFingerprint("postgresql", null, " ", null));

    final ObjectNode detailNode = DatabaseIdentityUtility.toDetailNode(identity);

    assertThat(detailNode.has("alias"), is(false));
    assertThat(detailNode.has("database-product"), is(false));
    assertThat(detailNode.has("database-server-fingerprint"), is(false));
    assertThat(detailNode.get("description").asString(), is("CRM"));
  }

  @Test
  public void fromCatalogWithoutDatabaseInfoAndCrawlInfo() {
    final Catalog catalog = mock(Catalog.class);
    when(catalog.getDatabaseInfo()).thenReturn(null);
    when(catalog.getCrawlInfo()).thenReturn(null);

    final DatabaseIdentity identity = DatabaseIdentityUtility.from("crm-prod", null, catalog);

    assertThat(identity.alias(), is("crm-prod"));
    assertThat(identity.databaseProduct().getName(), is(""));
    assertThat(identity.serverFingerprint().fingerprint(), is(""));
  }

  @Test
  public void fromNullCatalog() {
    final DatabaseIdentity identity = DatabaseIdentityUtility.from(" crm-prod ", " CRM ", null);

    assertThat(identity.alias(), is("crm-prod"));
    assertThat(identity.description(), is("CRM"));
    assertThat(identity.databaseProduct().getName(), is(""));
    assertThat(identity.serverFingerprint().fingerprint(), is(""));
  }

  @Test
  public void nullIdentity() {
    assertThrows(NullPointerException.class, () -> DatabaseIdentityUtility.toDetailNode(null));
    assertThrows(NullPointerException.class, () -> DatabaseIdentityUtility.toResultNode(null));
  }

  @Test
  public void resultNodeAliasOnly() {
    final DatabaseIdentity identity = new DatabaseIdentity("crm-prod", "CRM", null, null);

    assertThat(
        DatabaseIdentityUtility.toResultNode(identity).toString(), is("{\"alias\":\"crm-prod\"}"));
  }

  @Test
  public void resultNodeAllSet() {
    final DatabaseIdentity identity =
        new DatabaseIdentity("crm-prod", "CRM system of record", null, SERVER_FINGERPRINT);

    assertThat(
        DatabaseIdentityUtility.toResultNode(identity).toString(),
        is(
            """
            {"alias":"crm-prod"}\
            """));
  }

  @Test
  public void resultNodeFingerprintOnly() {
    final DatabaseIdentity identity = new DatabaseIdentity(null, null, null, SERVER_FINGERPRINT);

    final ObjectNode resultNode = DatabaseIdentityUtility.toResultNode(identity);

    assertThat(resultNode.has("confidence"), is(false));
    assertThat(resultNode.has("host-classification"), is(false));
  }

  @Test
  public void resultNodeNothingSet() {
    final DatabaseIdentity identity = new DatabaseIdentity("", "CRM", null, null);

    assertThat(identity.isEmpty(), is(true));
    assertThat(DatabaseIdentityUtility.toResultNode(identity).isEmpty(), is(true));
  }

  @Test
  public void resultNodeProductNameOnly() {
    final DatabaseIdentity identity = new DatabaseIdentity(null, null, null, null);

    assertThat(DatabaseIdentityUtility.toResultNode(identity).toString(), is("{}"));
  }
}
