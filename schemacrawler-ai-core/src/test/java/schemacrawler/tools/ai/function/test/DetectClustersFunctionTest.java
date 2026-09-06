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
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import schemacrawler.importance.model.DatabaseObjectVertexId;
import schemacrawler.importance.model.ImportanceModel;
import schemacrawler.importance.model.TableCluster;
import schemacrawler.importance.model.implementation.ImportanceModelBuilder;
import schemacrawler.tools.ai.functions.DetectClustersFunctionDefinition;
import schemacrawler.tools.ai.functions.DetectClustersFunctionParameters;
import schemacrawler.tools.ai.tools.FunctionExecutor;
import schemacrawler.tools.ai.tools.JsonFunctionReturn;
import tools.jackson.databind.JsonNode;

public class DetectClustersFunctionTest extends AbstractFunctionTest {

  @Test
  public void parameterDefaults() {
    final DetectClustersFunctionParameters defaultParameters =
        new DetectClustersFunctionParameters();
    final DetectClustersFunctionParameters nullParameters =
        new DetectClustersFunctionParameters(null, null, null);

    assertThat(defaultParameters.tableName(), is(""));
    assertThat(defaultParameters.maxCommunities(), is(5));
    assertThat(defaultParameters.maxCommunitySize(), is(5));
    assertThat(nullParameters.tableName(), is(""));
    assertThat(nullParameters.maxCommunities(), is(5));
    assertThat(nullParameters.maxCommunitySize(), is(5));
  }

  @Test
  public void defaultLimits() throws Exception {
    final ImportanceModel importanceModel = ImportanceModelBuilder.builder(catalog).build();
    final JsonNode result =
        execute(new DetectClustersFunctionParameters(), importanceModel).getResult();
    final JsonNode communities = result.get("communities");

    assertThat(communities.size(), greaterThan(0));
    assertThat(communities.size(), is(Math.min(5, importanceModel.getTableClusters().size())));
    assertThat(result.size(), is(1));
    for (final JsonNode community : communities) {
      assertThat(community.has("id"), is(true));
      assertThat(community.has("anchorVertexId"), is(false));
      assertThat(community.has("anchorTableFullName"), is(true));
      assertThat(community.has("totalClusterSize"), is(true));
      assertThat(community.has("memberVertexIds"), is(false));
      assertThat(community.get("memberTableFullNames").size(), lessThanOrEqualTo(5));
    }
  }

  @Test
  public void filtersOnAnyFullMemberName() throws Exception {
    final ImportanceModel importanceModel = ImportanceModelBuilder.builder(catalog).build();
    final TableCluster expectedTableCluster =
        importanceModel.getTableClusters().stream()
            .filter(tableCluster -> tableCluster.memberVertexIds().size() > 1)
            .findFirst()
            .orElse(importanceModel.getTableClusters().getFirst());
    final DatabaseObjectVertexId matchingMember = expectedTableCluster.memberVertexIds().getLast();
    final String matchingFullName =
        importanceModel.lookupByVertexId(matchingMember).orElseThrow().getFullName();

    final JsonNode communities =
        execute(
                new DetectClustersFunctionParameters(Pattern.quote(matchingFullName), 5, 1),
                importanceModel)
            .getResult()
            .get("communities");

    assertThat(communities.size(), is(1));
    assertThat(communities.get(0).get("id").asString(), is(expectedTableCluster.id().toString()));
    assertThat(
        communities.get(0).get("totalClusterSize").asInt(),
        is(expectedTableCluster.memberVertexIds().size()));
    assertThat(communities.get(0).has("memberVertexIds"), is(false));
  }

  @Test
  public void appliesIndependentLimitsAndUnlimitedValues() throws Exception {
    final ImportanceModel importanceModel = ImportanceModelBuilder.builder(catalog).build();

    final JsonNode limited =
        execute(new DetectClustersFunctionParameters("", 1, 1), importanceModel)
            .getResult()
            .get("communities");
    assertThat(limited.size(), is(1));
    assertThat(limited.get(0).has("memberVertexIds"), is(false));
    assertThat(limited.get(0).get("memberTableFullNames").size(), is(1));
    assertThat(limited.get(0).get("totalClusterSize").asInt(), greaterThan(0));

    final JsonNode unlimited =
        execute(new DetectClustersFunctionParameters("", 0, -1), importanceModel)
            .getResult()
            .get("communities");
    assertThat(unlimited.size(), is(importanceModel.getTableClusters().size()));
    for (final JsonNode community : unlimited) {
      assertThat(community.has("memberVertexIds"), is(false));
      assertThat(
          community.get("memberTableFullNames").size(),
          is(community.get("totalClusterSize").asInt()));
    }
  }

  private JsonFunctionReturn execute(
      final DetectClustersFunctionParameters parameters, final ImportanceModel importanceModel)
      throws Exception {
    final FunctionExecutor<DetectClustersFunctionParameters> executor =
        new DetectClustersFunctionDefinition().newExecutor();
    executor.configure(parameters);
    executor.setCatalog(catalog);
    executor.setERModel(erModel);
    executor.setImportanceModel(importanceModel);
    return (JsonFunctionReturn) executor.call();
  }
}
