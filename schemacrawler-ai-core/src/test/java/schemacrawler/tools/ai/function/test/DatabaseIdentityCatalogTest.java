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
    assertThat(identity.serverFingerprint().fingerprint(), is(not("")));
    assertThat(identity.databaseProduct().getName(), is("HSQL Database Engine"));

    final ObjectNode resultNode = DatabaseIdentityUtility.toResultNode(identity);
    assertThat(resultNode.get("alias").asString(), is("test-db"));
    assertThat(resultNode.get("database-product").asString(), is("HSQL Database Engine"));
  }
}
