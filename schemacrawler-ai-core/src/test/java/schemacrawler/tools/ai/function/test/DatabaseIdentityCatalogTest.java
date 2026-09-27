/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.function.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;
import schemacrawler.tools.ai.tools.DatabaseIdentity;
import schemacrawler.tools.ai.utility.DatabaseIdentityUtility;
import tools.jackson.databind.node.ObjectNode;
import us.fatehi.utility.jdbc.serverfingerprint.DatabaseServerFingerprint;

public class DatabaseIdentityCatalogTest extends AbstractFunctionTest {

  @Test
  public void fromTestCatalog() {
    final DatabaseServerFingerprint expected =
        catalog.getCrawlInfo().getDatabaseServerFingerprint();

    final DatabaseIdentity identity = DatabaseIdentityUtility.from("test-db", null, catalog);

    assertThat(identity.serverFingerprint(), is(expected));
    assertThat(identity.fingerprint(), is(not("")));
    assertThat(identity.databaseProductName(), is("HSQL Database Engine"));

    final ObjectNode resultNode = DatabaseIdentityUtility.toResultNode(identity);
    assertThat(resultNode.get("fingerprint").asString(), is(expected.fingerprint()));
    assertThat(resultNode.get("database-product-name").asString(), is("HSQL Database Engine"));
    assertThat(
        DatabaseIdentityUtility.toDetailNode(identity).get("database-system-identifier").asString(),
        is(expected.databaseSystemIdentifier()));
  }
}
