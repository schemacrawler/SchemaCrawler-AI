/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.function.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static schemacrawler.tools.ai.model.DatabaseObjectType.ROUTINES;
import static schemacrawler.tools.ai.model.DatabaseObjectType.TABLES;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import schemacrawler.tools.ai.functions.LintFunctionDefinition;
import schemacrawler.tools.ai.functions.LintFunctionParameters;
import schemacrawler.tools.ai.functions.ListFunctionDefinition;
import schemacrawler.tools.ai.functions.ListFunctionParameters;
import schemacrawler.tools.ai.tools.FunctionDefinition;
import schemacrawler.tools.ai.tools.FunctionExecutor;
import schemacrawler.tools.ai.tools.FunctionParameters;
import us.fatehi.test.utility.extensions.WithSystemProperty;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class InvocationIsolationTest extends AbstractFunctionTest {

  @Test
  public void baselineUnchangedBySelections() throws Exception {
    final int tableCount = catalog.getTables().size();
    final int routineCount = catalog.getRoutines().size();
    final int schemaCount = catalog.getSchemas().size();

    run(new ListFunctionDefinition(), new ListFunctionParameters(TABLES, "AUTHORS"));
    run(new ListFunctionDefinition(), new ListFunctionParameters(ROUTINES, null));
    run(new ListFunctionDefinition(), new ListFunctionParameters(TABLES, "NOT_A_TABLE"));

    assertThat(catalog.getTables().size(), equalTo(tableCount));
    assertThat(catalog.getRoutines().size(), equalTo(routineCount));
    assertThat(catalog.getSchemas().size(), equalTo(schemaCount));
  }

  @Test
  public void earlierPayloadStableAfterLaterSelections() throws Exception {
    final ListFunctionDefinition definition = new ListFunctionDefinition();
    final ListFunctionParameters narrow = new ListFunctionParameters(TABLES, "AUTHORS");

    final String first = run(definition, narrow);
    final String routines = run(definition, new ListFunctionParameters(ROUTINES, null));
    final String second = run(definition, narrow);

    assertThat(first, containsString("AUTHORS"));
    assertThat(routines, not(equalTo(first)));
    assertThat(second, equalTo(first));
  }

  @Test
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "hsqldb")
  public void commandSelectionDoesNotLeak() throws Exception {
    final LintFunctionDefinition definition = new LintFunctionDefinition();

    final String all = run(definition, new LintFunctionParameters(null));
    final String narrow = run(definition, new LintFunctionParameters("AUTHORS"));
    final String empty = run(definition, new LintFunctionParameters("NOT_A_TABLE"));
    final String allAgain = run(definition, new LintFunctionParameters(null));

    assertThat(narrow, not(equalTo(all)));
    assertThat(empty, not(equalTo(all)));
    assertThat(allAgain, equalTo(all));
  }

  private <P extends FunctionParameters> String run(
      final FunctionDefinition<P> definition, final P args) throws Exception {
    final FunctionExecutor<P> executor = definition.newExecutor();
    executor.configure(args);
    executor.setCatalog(catalog);
    executor.setERModel(erModel);
    if (executor.usesConnection()) {
      executor.setConnectionSource(connectionSource);
    }
    // Lint identifiers are random per run
    return executor
        .call()
        .get()
        .replaceAll("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}", "ID");
  }
}
