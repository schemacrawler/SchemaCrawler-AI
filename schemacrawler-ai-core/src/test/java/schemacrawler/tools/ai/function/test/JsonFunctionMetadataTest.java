/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.function.test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static schemacrawler.tools.ai.functions.DescribeEntitiesFunctionParameters.EntityKind.ALL;
import static schemacrawler.tools.ai.functions.DescribeRoutinesFunctionParameters.RoutineDescriptionScope.DEFAULT;
import static schemacrawler.tools.ai.functions.ListAcrossTablesFunctionParameters.DependantObjectType.FOREIGN_KEYS;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import schemacrawler.tools.ai.functions.DatabaseServerInformationFunctionDefinition;
import schemacrawler.tools.ai.functions.DescribeEntitiesFunctionDefinition;
import schemacrawler.tools.ai.functions.DescribeEntitiesFunctionParameters;
import schemacrawler.tools.ai.functions.DescribeRelationshipsFunctionDefinition;
import schemacrawler.tools.ai.functions.DescribeRelationshipsFunctionParameters;
import schemacrawler.tools.ai.functions.DescribeRoutinesFunctionDefinition;
import schemacrawler.tools.ai.functions.DescribeRoutinesFunctionParameters;
import schemacrawler.tools.ai.functions.DescribeTablesFunctionDefinition;
import schemacrawler.tools.ai.functions.DescribeTablesFunctionParameters;
import schemacrawler.tools.ai.functions.ListAcrossTablesFunctionDefinition;
import schemacrawler.tools.ai.functions.ListAcrossTablesFunctionParameters;
import schemacrawler.tools.ai.functions.ListFunctionDefinition;
import schemacrawler.tools.ai.functions.ListFunctionParameters;
import schemacrawler.tools.ai.model.DatabaseObjectType;
import schemacrawler.tools.ai.tools.FunctionDefinition;
import schemacrawler.tools.ai.tools.FunctionExecutor;
import schemacrawler.tools.ai.tools.FunctionParameters;
import schemacrawler.tools.ai.tools.FunctionReturn;
import schemacrawler.tools.ai.tools.JsonFunctionReturn;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JsonFunctionMetadataTest extends AbstractFunctionTest {

  @Test
  public void testDescribeTablesSummaryAndNextSteps() throws Exception {
    final JsonFunctionReturn functionReturn =
        execute(
            new DescribeTablesFunctionDefinition(),
            new DescribeTablesFunctionParameters(
                "AUTHORS",
                List.of(
                    schemacrawler.tools.ai.functions.DescribeTablesFunctionParameters
                        .TableDescriptionScope.DEFAULT)));

    assertThat(functionReturn.getSummary(), containsString("Returned"));
    assertThat(
        functionReturn.getMetadata().toMetadataMap("schemacrawler-ai/"),
        hasEntry(
            "schemacrawler-ai/next_steps",
            "Inspect table indexes next, because the table description output does not include"
                + " index details."));
  }

  @Test
  public void testDescribeEntitiesSummaryAndNextSteps() throws Exception {
    final JsonFunctionReturn functionReturn =
        execute(
            new DescribeEntitiesFunctionDefinition(),
            new DescribeEntitiesFunctionParameters("AUTHORS", ALL));

    assertThat(functionReturn.getSummary(), containsString("Returned"));
    assertThat(
        functionReturn.getMetadata().toMetadataMap("schemacrawler-ai/"),
        hasEntry(
            "schemacrawler-ai/next_steps",
            "Inspect relationships for the same scope next, because the current result does not"
                + " include how entities connect."));
  }

  @Test
  public void testDescribeRoutinesSummaryAndNextSteps() throws Exception {
    final JsonFunctionReturn functionReturn =
        execute(
            new DescribeRoutinesFunctionDefinition(),
            new DescribeRoutinesFunctionParameters("GetBooksCount", List.of(DEFAULT)));

    assertThat(functionReturn.getSummary(), containsString("Returned"));
    assertThat(
        functionReturn.getMetadata().toMetadataMap("schemacrawler-ai/"),
        hasEntry(
            "schemacrawler-ai/next_steps",
            "Inspect routine attributes or referenced objects next, because the routine summary"
                + " does not include execution details."));
  }

  @Test
  public void testDescribeRelationshipsSummaryAndNextSteps() throws Exception {
    final JsonFunctionReturn functionReturn =
        execute(
            new DescribeRelationshipsFunctionDefinition(),
            new DescribeRelationshipsFunctionParameters("BOOKS", null));

    assertThat(functionReturn.getSummary(), containsString("Returned"));
    assertThat(
        functionReturn.getMetadata().toMetadataMap("schemacrawler-ai/"),
        hasEntry(
            "schemacrawler-ai/next_steps",
            "Inspect the related tables or entities next, because the current result only shows"
                + " relationship metadata."));
  }

  @Test
  public void testDatabaseServerInformationSummaryAndNextSteps() throws Exception {
    final JsonFunctionReturn functionReturn =
        execute(
            new DatabaseServerInformationFunctionDefinition(),
            new schemacrawler.tools.ai.tools.NoParameters());

    assertThat(functionReturn.getSummary(), containsString("Returned database information"));
    assertThat(
        functionReturn.getMetadata().toMetadataMap("schemacrawler-ai/"),
        hasEntry(
            "schemacrawler-ai/next_steps",
            "Inspect tables or routines in the schema next, because server information alone does"
                + " not reveal database objects."));
  }

  @Test
  public void testListSummaryAndNextSteps() throws Exception {
    final JsonFunctionReturn functionReturn =
        execute(
            new ListFunctionDefinition(),
            new ListFunctionParameters(DatabaseObjectType.TABLES, null));

    assertThat(functionReturn.getSummary(), containsString("Returned"));
    assertThat(
        functionReturn.getMetadata().toMetadataMap("schemacrawler-ai/"),
        hasEntry(
            "schemacrawler-ai/next_steps",
            "Inspect table details, indexes, or relationships next, because the table list does not"
                + " include those details."));
  }

  @Test
  public void testListAcrossTablesSummaryAndNextSteps() throws Exception {
    final JsonFunctionReturn functionReturn =
        execute(
            new ListAcrossTablesFunctionDefinition(),
            new ListAcrossTablesFunctionParameters(
                schemacrawler.tools.ai.functions.ListAcrossTablesFunctionParameters
                    .DependantObjectType.INDEXES,
                null,
                null));

    assertThat(functionReturn.getSummary(), containsString("Returned"));
    assertThat(
        functionReturn.getMetadata().toMetadataMap("schemacrawler-ai/"),
        hasEntry(
            "schemacrawler-ai/next_steps",
            "Inspect table details or referenced tables next, because the index list does not"
                + " include full table context."));
  }

  @Test
  public void testListAcrossTablesForeignKeysSummaryAndNextSteps() throws Exception {
    final JsonFunctionReturn functionReturn =
        execute(
            new ListAcrossTablesFunctionDefinition(),
            new ListAcrossTablesFunctionParameters(FOREIGN_KEYS, null, null));

    assertThat(functionReturn.getSummary(), containsString("Returned"));
    assertThat(
        functionReturn.getMetadata().toMetadataMap("schemacrawler-ai/"),
        hasEntry(
            "schemacrawler-ai/next_steps",
            "Inspect referenced tables or objects that use these tables next, because foreign keys"
                + " only show one side of the relationship."));
  }

  private <P extends FunctionParameters> JsonFunctionReturn execute(
      final FunctionDefinition<P> functionDefinition, final P args) throws Exception {
    final FunctionExecutor<P> executor = functionDefinition.newExecutor();
    executor.configure(args);
    executor.setCatalog(catalog);
    executor.setERModel(erModel);
    if (connectionSource != null && executor.usesConnection()) {
      executor.setConnectionSource(connectionSource);
    }
    final FunctionReturn functionReturn = executor.call();
    return (JsonFunctionReturn) functionReturn;
  }
}
