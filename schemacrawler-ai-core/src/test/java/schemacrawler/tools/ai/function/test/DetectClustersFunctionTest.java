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
import schemacrawler.importance.model.DatabaseObjectNodeId;
import schemacrawler.importance.model.SchemaGraphModel;
import schemacrawler.importance.model.TableCluster;
import schemacrawler.importance.model.implementation.SchemaGraphModelBuilder;
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
    final SchemaGraphModel schemaGraphModel = SchemaGraphModelBuilder.builder(catalog).build();
    final JsonNode result =
        execute(new DetectClustersFunctionParameters(), schemaGraphModel).getResult();
    final JsonNode communities = result.get("communities");

    assertThat(communities.size(), greaterThan(0));
    assertThat(communities.size(), is(Math.min(5, schemaGraphModel.getTableClusters().size())));
    assertThat(result.size(), is(1));
    for (final JsonNode community : communities) {
      assertThat(community.has("id"), is(true));
      assertThat(community.has("anchorNodeId"), is(true));
      assertThat(community.has("anchorTableFullName"), is(true));
      assertThat(community.has("totalCommunitySize"), is(true));
      assertThat(community.get("memberNodeIds").size(), lessThanOrEqualTo(5));
      assertThat(community.get("memberTableFullNames").size(), lessThanOrEqualTo(5));
    }
  }

  @Test
  public void filtersOnAnyFullMemberName() throws Exception {
    final SchemaGraphModel schemaGraphModel = SchemaGraphModelBuilder.builder(catalog).build();
    final TableCluster expectedTableCluster =
        schemaGraphModel.getTableClusters().stream()
            .filter(tableCluster -> tableCluster.memberNodes().size() > 1)
            .findFirst()
            .orElse(schemaGraphModel.getTableClusters().getFirst());
    final DatabaseObjectNodeId matchingMember = expectedTableCluster.memberNodes().getLast();
    final String matchingFullName =
        schemaGraphModel.lookupByVertexNodeId(matchingMember).orElseThrow().getFullName();

    final JsonNode communities =
        execute(
                new DetectClustersFunctionParameters(Pattern.quote(matchingFullName), 5, 1),
                schemaGraphModel)
            .getResult()
            .get("communities");

    assertThat(communities.size(), is(1));
    assertThat(communities.get(0).get("id").asString(), is(expectedTableCluster.id().toString()));
    assertThat(
        communities.get(0).get("totalCommunitySize").asInt(),
        is(expectedTableCluster.memberNodes().size()));
    assertThat(communities.get(0).get("memberNodeIds").size(), is(1));
  }

  @Test
  public void appliesIndependentLimitsAndUnlimitedValues() throws Exception {
    final SchemaGraphModel schemaGraphModel = SchemaGraphModelBuilder.builder(catalog).build();

    final JsonNode limited =
        execute(new DetectClustersFunctionParameters("", 1, 1), schemaGraphModel)
            .getResult()
            .get("communities");
    assertThat(limited.size(), is(1));
    assertThat(limited.get(0).get("memberNodeIds").size(), is(1));
    assertThat(limited.get(0).get("memberTableFullNames").size(), is(1));
    assertThat(limited.get(0).get("totalCommunitySize").asInt(), greaterThan(0));

    final JsonNode unlimited =
        execute(new DetectClustersFunctionParameters("", 0, -1), schemaGraphModel)
            .getResult()
            .get("communities");
    assertThat(unlimited.size(), is(schemaGraphModel.getTableClusters().size()));
    for (final JsonNode community : unlimited) {
      assertThat(
          community.get("memberNodeIds").size(), is(community.get("totalCommunitySize").asInt()));
      assertThat(
          community.get("memberTableFullNames").size(),
          is(community.get("totalCommunitySize").asInt()));
    }
  }

  private JsonFunctionReturn execute(
      final DetectClustersFunctionParameters parameters, final SchemaGraphModel schemaGraphModel)
      throws Exception {
    final FunctionExecutor<DetectClustersFunctionParameters> executor =
        new DetectClustersFunctionDefinition().newExecutor();
    executor.configure(parameters);
    executor.setCatalog(catalog);
    executor.setERModel(erModel);
    executor.setSchemaGraphModel(schemaGraphModel);
    return (JsonFunctionReturn) executor.call();
  }
}
