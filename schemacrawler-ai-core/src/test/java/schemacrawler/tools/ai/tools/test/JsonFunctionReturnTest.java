/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.tools.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static schemacrawler.tools.ai.utility.JsonUtility.mapper;

import org.junit.jupiter.api.Test;
import schemacrawler.tools.ai.tools.FunctionReturnMetadata;
import schemacrawler.tools.ai.tools.JsonFunctionReturn;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

public class JsonFunctionReturnTest {

  @Test
  public void objectResult() {
    final ObjectNode objectNode = mapper.createObjectNode();
    objectNode.put("key", "value");

    final JsonFunctionReturn functionReturn = new JsonFunctionReturn(objectNode);

    assertThat(functionReturn.getResult().isObject(), is(true));
    assertThat(functionReturn.get(), is("{\"key\":\"value\"}"));
    assertThat(functionReturn.getMetadata(), is(FunctionReturnMetadata.JSON));
  }

  @Test
  public void namedListResult() {
    final ArrayNode list = mapper.createArrayNode();
    list.add("item");

    final JsonFunctionReturn functionReturn = new JsonFunctionReturn("items", list);

    assertThat(functionReturn.getResult().isObject(), is(true));
    assertThat(functionReturn.get(), is("{\"items\":[\"item\"]}"));
  }

  @Test
  public void nullObjectResult() {
    assertThrows(NullPointerException.class, () -> new JsonFunctionReturn((ObjectNode) null));
  }
}
