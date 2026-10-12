/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.function.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import schemacrawler.ermodel.model.ERModel;
import schemacrawler.filter.CatalogProjectionBuilder;
import schemacrawler.inclusionrule.ExcludeAll;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.tools.ai.functions.DescribeErRelationshipsFunctionDefinition;
import schemacrawler.tools.ai.functions.DescribeErRelationshipsFunctionParameters;
import schemacrawler.tools.ai.functions.DescribeErRelationshipsFunctionParameters.Cardinality;
import schemacrawler.tools.ai.tools.FunctionCallback;
import schemacrawler.tools.ai.tools.JsonFunctionReturn;
import schemacrawler.tools.ai.utility.test.FunctionExecutionTestUtility;
import schemacrawler.tools.utility.SchemaCrawlerUtility;
import tools.jackson.databind.JsonNode;
import us.fatehi.test.utility.extensions.ResolveTestContext;
import us.fatehi.test.utility.extensions.TestContext;

@ResolveTestContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DescribeErRelationshipsFunctionTest extends AbstractFunctionTest {

  @Test
  public void describe1NRelationships(final TestContext testContext) throws Exception {
    final DescribeErRelationshipsFunctionParameters args =
        new DescribeErRelationshipsFunctionParameters("FK_SALES_REGIONS", Cardinality.ONE_MANY);
    describeRelationship(testContext, args, true);
  }

  @Test
  public void describeAllRelationships(final TestContext testContext) throws Exception {
    final DescribeErRelationshipsFunctionParameters args =
        new DescribeErRelationshipsFunctionParameters(null, null);
    describeRelationship(testContext, args, true);
  }

  @Test
  public void relationshipsUseModelBuiltFromSelectedCatalog() throws Exception {
    final Catalog noTablesCatalog =
        CatalogProjectionBuilder.builder(catalog)
            .withOptions(
                SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
                    .withLimitOptions(
                        LimitOptionsBuilder.builder().includeTables(new ExcludeAll()).toOptions()))
            .build();
    final ERModel unrelatedERModel = SchemaCrawlerUtility.buildERModel(noTablesCatalog);
    final DescribeErRelationshipsFunctionDefinition functionDefinition =
        new DescribeErRelationshipsFunctionDefinition();
    final FunctionCallback<DescribeErRelationshipsFunctionParameters> callback =
        new FunctionCallback<>(functionDefinition, catalog, unrelatedERModel, null);

    final JsonFunctionReturn result =
        (JsonFunctionReturn)
            callback.execute("{\"relationship_name\":\"BOOKAUTHORS\"}", connectionSource);
    final JsonNode relationships = result.getResult().get("relationships");

    assertThat(relationships.size(), is(1));
    assertThat(relationships.get(0).get("name").asString(), is("BOOKAUTHORS"));
  }

  @Test
  public void describeRelationship(final TestContext testContext) throws Exception {
    final DescribeErRelationshipsFunctionParameters args =
        new DescribeErRelationshipsFunctionParameters("FK_PREVIOUSEDITION", null);
    describeRelationship(testContext, args, true);
  }

  @Test
  public void describeRelationshipType(final TestContext testContext) throws Exception {
    final DescribeErRelationshipsFunctionParameters args =
        new DescribeErRelationshipsFunctionParameters(null, Cardinality.MANY_MANY);
    describeRelationship(testContext, args, true);
  }

  @Test
  public void describeUnknownRelationship(final TestContext testContext) throws Exception {
    final DescribeErRelationshipsFunctionParameters args =
        new DescribeErRelationshipsFunctionParameters("NOT_A_REL", null);
    describeRelationship(testContext, args, true);
  }

  private void describeRelationship(
      final TestContext testContext,
      final DescribeErRelationshipsFunctionParameters args,
      final boolean hasResults)
      throws Exception {

    final DescribeErRelationshipsFunctionDefinition functionDefinition =
        new DescribeErRelationshipsFunctionDefinition();
    FunctionExecutionTestUtility.assertFunctionExecution(
        testContext, functionDefinition, args, catalog, erModel, null, hasResults);
  }
}
