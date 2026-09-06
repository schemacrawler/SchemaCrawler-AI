/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.function.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;
import schemacrawler.importance.model.SchemaGraphModel;
import schemacrawler.importance.model.implementation.SchemaGraphModelBuilder;
import schemacrawler.tools.ai.functions.TablePathFunctionDefinition;
import schemacrawler.tools.ai.functions.TablePathFunctionParameters;
import schemacrawler.tools.ai.tools.FunctionExecutor;
import schemacrawler.tools.ai.tools.JsonFunctionReturn;
import tools.jackson.databind.JsonNode;

public class TablePathFunctionTest extends AbstractFunctionTest {

  @Test
  public void parameterDefaults() {
    assertThat(new TablePathFunctionParameters().maxPathDepth(), is(5));
    assertThat(new TablePathFunctionParameters("", "", null).maxPathDepth(), is(5));
  }

  @Test
  public void findForwardDependencyPath() throws Exception {
    final JsonNode path =
        execute(
                new TablePathFunctionParameters(
                    "PUBLIC\\.BOOKS\\.BOOKAUTHORS", "PUBLIC\\.BOOKS\\.AUTHORS"))
            .getResult();

    assertThat(path.get("path").size(), is(2));
    assertThat(path.get("path").get(0).asString(), is("PUBLIC.BOOKS.BOOKAUTHORS"));
    assertThat(path.get("path").get(1).asString(), is("PUBLIC.BOOKS.AUTHORS"));
    assertThat(path.get("usesImpliedAssociations").asBoolean(), is(false));
  }

  private JsonFunctionReturn execute(final TablePathFunctionParameters parameters)
      throws Exception {
    return execute(parameters, SchemaGraphModelBuilder.builder(catalog).build());
  }

  private JsonFunctionReturn execute(
      final TablePathFunctionParameters parameters, final SchemaGraphModel schemaGraphModel)
      throws Exception {
    final FunctionExecutor<TablePathFunctionParameters> executor =
        new TablePathFunctionDefinition().newExecutor();
    executor.configure(parameters);
    executor.setCatalog(catalog);
    executor.setERModel(erModel);
    executor.setSchemaGraphModel(schemaGraphModel);
    return (JsonFunctionReturn) executor.call();
  }
}
