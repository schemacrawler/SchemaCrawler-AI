/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.utility.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

import java.util.Set;
import org.junit.jupiter.api.Test;
import schemacrawler.tools.ai.utility.SqlTableExtractor;

public class SqlTableExtractorTest {

  @Test
  public void extractFromFragment() {
    final String sql = "FROM table_1";
    final Set<String> tables = SqlTableExtractor.extractTables(sql);
    assertThat(tables, containsInAnyOrder("table_1"));
  }

  @Test
  public void extractFromInsert() {
    final String sql = "INSERT INTO schema_2.table_4 VALUES (1, 'test')";
    final Set<String> tables = SqlTableExtractor.extractTables(sql);
    assertThat(tables, containsInAnyOrder("table_4"));
  }

  @Test
  public void extractFromInvalidSql() {
    final String sql = "use from table_5 table_6";
    final Set<String> tables = SqlTableExtractor.extractTables(sql);
    assertThat(tables, containsInAnyOrder("table_5"));
  }

  @Test
  public void extractFromNonSql() {
    final String sql = "invalid sql table_5 table_6";
    final Set<String> tables = SqlTableExtractor.extractTables(sql);
    assertThat(tables, is(empty()));
  }

  @Test
  public void extractFromSelect() {
    final String sql = "SELECT * FROM schema_1.table_1 t1 JOIN table_2 t2 ON t1.id = t2.ref_id";
    final Set<String> tables = SqlTableExtractor.extractTables(sql);
    assertThat(tables, containsInAnyOrder("table_1", "table_2"));
  }

  @Test
  public void extractFromUpdate() {
    final String sql = "UPDATE table_3 SET column_1 = 0 WHERE id = 1";
    final Set<String> tables = SqlTableExtractor.extractTables(sql);
    assertThat(tables, containsInAnyOrder("table_3"));
  }

  @Test
  public void extractWithAlias() {
    final String sql = "SELECT * FROM table_1 AS t";
    final Set<String> tables = SqlTableExtractor.extractTables(sql);
    assertThat(tables, containsInAnyOrder("table_1"));
  }

  @Test
  public void extractWithNullOrEmpty() {
    assertThat(SqlTableExtractor.extractTables(null), is(empty()));
    assertThat(SqlTableExtractor.extractTables(""), is(empty()));
    assertThat(SqlTableExtractor.extractTables("   "), is(empty()));
  }
}
