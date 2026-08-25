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
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static schemacrawler.tools.ai.utility.JsonUtility.mapper;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import schemacrawler.tools.ai.functions.TableSampleFunctionDefinition;
import schemacrawler.tools.ai.functions.TableSampleFunctionParameters;
import schemacrawler.tools.ai.tools.FunctionExecutor;
import schemacrawler.tools.ai.tools.FunctionReturn;
import tools.jackson.databind.JsonNode;
import us.fatehi.test.utility.TestWriter;
import us.fatehi.test.utility.extensions.ResolveTestContext;
import us.fatehi.test.utility.extensions.TestContext;
import us.fatehi.test.utility.extensions.WithSystemProperty;

@ResolveTestContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TableSampleFunctionTest extends AbstractFunctionTest {

  @Test
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "hsqldb")
  public void sampleAllTables(final TestContext testContext) throws Exception {
    final TableSampleFunctionParameters args = new TableSampleFunctionParameters(null);
    final JsonNode root = sampleTable(testContext, args, true);

    assertAll(
        "top-level JSON",
        () -> assertEquals(16, root.size(), "Root field count"),
        () -> assertEquals("HSQL Database Engine", root.get("db").asString()),
        () -> assertEquals("tablesample", root.get("operation").asString()));

    assertAll(
        () -> assertRowCount(root, "AUTHORS", 10),
        () -> assertRowCount(root, "AUTHORSLIST", 10),
        () -> assertRowCount(root, "BOOKAUTHORS", 10),
        () -> assertRowCount(root, "BOOKS", 10),
        () -> assertRowCount(root, "Celebrities", 5),
        () -> assertRowCount(root, "Celebrity Updates", 5),
        () -> assertRowCount(root, "COUPONS", 10),
        () -> assertRowCount(root, "CUSTOMERDATA", 0),
        () -> assertRowCount(root, "CUSTOMERS", 0),
        () -> assertRowCount(root, "PUBLISHERS", 10),
        () -> assertRowCount(root, "ΒΙΒΛΊΑ", 0),
        () -> assertRowCount(root, "REGIONS", 10),
        () -> assertRowCount(root, "SALES", 10),
        () -> assertRowCount(root, "SALESDATA", 0));
  }

  @Test
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "hsqldb")
  public void sampleTable(final TestContext testContext) throws Exception {
    final TableSampleFunctionParameters args = new TableSampleFunctionParameters("AUTHORS");
    final JsonNode root = sampleTable(testContext, args, true);

    assertAll(
        "top-level JSON",
        () -> assertEquals(5, root.size(), "Root field count"),
        () -> assertEquals("HSQL Database Engine", root.get("db").asString()),
        () -> assertEquals("tablesample", root.get("operation").asString()));

    assertAll(
        () -> assertRowCount(root, "AUTHORS", 10),
        () -> assertRowCount(root, "AUTHORSLIST", 10),
        () -> assertRowCount(root, "BOOKAUTHORS", 10));
  }

  @Test
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "hsqldb")
  public void sampleUnknownTable(final TestContext testContext) throws Exception {
    final TableSampleFunctionParameters args = new TableSampleFunctionParameters("NOT_A_TABLE");
    JsonNode root = sampleTable(testContext, args, true);
    assertThat(root.asString(), containsString("No results returned"));
  }

  private void assertRowCount(
      final JsonNode root, final String tableName, final int expectedCount) {

    JsonNode table = null;

    for (final JsonNode node : root) {
      if (tableName.equals(node.path("table").asString())
          || tableName.equals(node.path("view").asString())) {
        table = node;
        break;
      }
    }

    assertNotNull(table, "Table not found: " + tableName);

    final JsonNode data = table.path("data");

    assertTrue(data.isArray(), "data is not an array: " + tableName);
    assertEquals(expectedCount, data.size(), tableName);
  }

  private JsonNode sampleTable(
      final TestContext testContext,
      final TableSampleFunctionParameters args,
      final boolean hasResults)
      throws Exception {

    final TableSampleFunctionDefinition functionDefinition = new TableSampleFunctionDefinition();
    final FunctionExecutor<TableSampleFunctionParameters> executor =
        functionDefinition.newExecutor();
    executor.configure(args);
    executor.setCatalog(catalog);
    executor.setERModel(erModel);
    executor.setConnectionSource(connectionSource);

    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      final FunctionReturn functionReturn = executor.call();
      final String results = functionReturn.get();
      if (!hasResults && results.isBlank()) {
        return mapper.nullNode();
      }
      out.write(results);
    }
    final Path outputPath = testout.getFilePath();

    final String text = Files.readAllLines(outputPath).get(0);
    if (text.startsWith("No results returned")) {
      return mapper.stringNode(text);
    }

    final JsonNode node = mapper.readTree(outputPath);
    return node;
  }
}
