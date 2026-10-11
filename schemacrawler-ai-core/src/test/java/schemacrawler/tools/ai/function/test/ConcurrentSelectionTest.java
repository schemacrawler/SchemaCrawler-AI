/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.function.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static schemacrawler.tools.ai.model.DatabaseObjectType.ROUTINES;
import static schemacrawler.tools.ai.model.DatabaseObjectType.TABLES;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import schemacrawler.tools.ai.functions.ListFunctionDefinition;
import schemacrawler.tools.ai.functions.ListFunctionParameters;
import schemacrawler.tools.ai.tools.FunctionExecutor;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ConcurrentSelectionTest extends AbstractFunctionTest {

  private static final int ROUNDS = 5;

  @Test
  public void concurrentSelectionsMatchIsolatedExecution() throws Exception {
    final List<ListFunctionParameters> selections =
        List.of(
            new ListFunctionParameters(TABLES, "AUTHORS"),
            new ListFunctionParameters(TABLES, "BOOKS"),
            new ListFunctionParameters(ROUTINES, null),
            new ListFunctionParameters(TABLES, "NOT_A_TABLE"));

    final List<String> expected = new ArrayList<>();
    for (final ListFunctionParameters selection : selections) {
      expected.add(run(selection));
    }
    assertThat(expected.get(0), not(equalTo(expected.get(1))));

    final int tableCount = catalog.getTables().size();
    final int workers = selections.size();
    final ExecutorService executor = Executors.newFixedThreadPool(workers);
    try {
      for (int round = 0; round < ROUNDS; round++) {
        final CyclicBarrier barrier = new CyclicBarrier(workers + 1);
        final List<Future<String>> futures = new ArrayList<>();
        for (final ListFunctionParameters selection : selections) {
          final Callable<String> task =
              () -> {
                barrier.await(30, TimeUnit.SECONDS);
                return run(selection);
              };
          futures.add(executor.submit(task));
        }
        barrier.await(30, TimeUnit.SECONDS);
        // Baseline read overlaps the narrow selections
        assertThat(catalog.getTables().size(), equalTo(tableCount));

        for (int i = 0; i < workers; i++) {
          assertThat(futures.get(i).get(60, TimeUnit.SECONDS), equalTo(expected.get(i)));
        }
      }
    } finally {
      executor.shutdownNow();
      assertThat(executor.awaitTermination(30, TimeUnit.SECONDS), equalTo(true));
    }
    assertThat(catalog.getTables().size(), equalTo(tableCount));
  }

  @Test
  public void failureDoesNotAffectOtherSelections() throws Exception {
    final ListFunctionParameters narrow = new ListFunctionParameters(TABLES, "AUTHORS");
    final String expected = run(narrow);

    final FunctionExecutor<ListFunctionParameters> failing =
        new ListFunctionDefinition().newExecutor();
    failing.configure(narrow);
    // No catalog set, so the call fails
    try {
      failing.call();
    } catch (final Exception e) {
      // expected
    }

    assertThat(run(narrow), equalTo(expected));
  }

  private String run(final ListFunctionParameters args) throws Exception {
    final FunctionExecutor<ListFunctionParameters> executor =
        new ListFunctionDefinition().newExecutor();
    executor.configure(args);
    executor.setCatalog(catalog);
    executor.setERModel(erModel);
    return executor.call().get();
  }
}
