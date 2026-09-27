/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.tools.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.tools.ai.tools.DatabaseIdentity;
import schemacrawler.tools.ai.tools.FunctionReturn;
import schemacrawler.tools.ai.tools.NoParameters;
import schemacrawler.tools.ai.tools.TextFunctionReturn;
import schemacrawler.tools.ai.tools.base.AbstractJsonFunctionExecutor;
import us.fatehi.utility.property.PropertyName;

public class FunctionExecutorDatabaseIdentityTest {

  private static final class TestExecutor extends AbstractJsonFunctionExecutor<NoParameters> {

    TestExecutor() {
      super(new PropertyName("test-executor"));
    }

    @Override
    public FunctionReturn call() {
      return new TextFunctionReturn("ok");
    }

    DatabaseIdentity databaseIdentity() {
      return getDatabaseIdentity();
    }

    @Override
    protected SchemaCrawlerOptions createSchemaCrawlerOptions() {
      return SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();
    }
  }

  @Test
  public void defaultIdentityIsEmpty() {
    assertThat(new TestExecutor().databaseIdentity(), is(DatabaseIdentity.empty()));
  }

  @Test
  public void nullIdentityIsEmpty() {
    final TestExecutor executor = new TestExecutor();
    executor.setDatabaseIdentity(null);

    assertThat(executor.databaseIdentity(), is(DatabaseIdentity.empty()));
  }

  @Test
  public void setIdentity() {
    final DatabaseIdentity identity = new DatabaseIdentity("crm-prod", "CRM", null, null);
    final TestExecutor executor = new TestExecutor();
    executor.setDatabaseIdentity(identity);

    assertThat(executor.databaseIdentity(), is(identity));
  }
}
