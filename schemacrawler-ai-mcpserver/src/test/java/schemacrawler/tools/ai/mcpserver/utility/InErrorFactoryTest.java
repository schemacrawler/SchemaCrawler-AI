/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.mcpserver.utility;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.Connection;
import org.junit.jupiter.api.Test;
import schemacrawler.ermodel.model.ERModel;
import schemacrawler.schema.Catalog;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

public class InErrorFactoryTest {

  @Test
  public void shouldCreateErroredCatalog() {
    final Catalog catalog = InErrorFactory.createErroredCatalog();

    assertThat(catalog.getName(), is("empty-catalog"));
    assertThat(catalog.getFullName(), is("empty-catalog"));
    assertThat(catalog.toString(), is("empty-catalog"));
    assertThat(catalog.equals(catalog), is(true));
    assertThat(catalog.equals(InErrorFactory.createErroredCatalog()), is(false));
    assertThrows(IllegalStateException.class, catalog::getSchemas);
  }

  @Test
  public void shouldCreateErroredConnectionSource() throws Exception {
    final DatabaseConnectionSource connectionSource =
        InErrorFactory.createErroredConnectionSource();

    assertThat(connectionSource.toString(), is("empty-data-source"));
    assertThat(connectionSource.releaseConnection(null), is(true));
    connectionSource.setFirstConnectionInitializer(null);
    try (Connection connection = connectionSource.get()) {
      assertThat(connection.isValid(1), is(true));
    }
  }

  @Test
  public void shouldCreateErroredERModel() {
    final ERModel erModel = InErrorFactory.createErroredERModel();

    assertThat(erModel.toString(), is("empty-ermodel"));
    assertThat(erModel.equals(erModel), is(true));
    assertThat(erModel.equals(InErrorFactory.createErroredERModel()), is(false));
    assertThat(erModel.getEntities().isEmpty(), is(true));
    assertThat(erModel.getUnmodeledTableReferences().isEmpty(), is(true));
    assertThat(erModel.lookupEntity("missing").isEmpty(), is(true));
  }
}
