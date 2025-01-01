/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.tools.command.aichat.test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaReference;
import schemacrawler.test.utility.crawl.LightTable;
import schemacrawler.tools.command.aichat.embeddings.EmbeddingService;
import schemacrawler.tools.command.aichat.embeddings.QueryService;
import schemacrawler.tools.command.aichat.embeddings.TextEmbedding;

public class QueryServiceTest {

  private QueryService queryService;
  private Table table;

  @BeforeEach
  public void setUp() {

    final LightTable table = new LightTable(new SchemaReference("schema_name", ""), "table_name");
    table.addColumn("column_name");
    this.table = table;

    final EmbeddingService embeddingService = text -> new TextEmbedding(text);

    queryService = new QueryService(embeddingService);
  }

  @Test
  public void testQuery() {
    final String prompt = "test prompt";
    queryService.addTables(Arrays.asList(table));

    final List<String> messages = new ArrayList<>(queryService.query(prompt));

    assertThat(messages, hasSize(2));
    assertThat(messages.get(0), startsWith("You are a helpful assistant"));
    assertThat(messages.get(1), containsString("table_name"));
  }
}
