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
import static org.hamcrest.Matchers.hasEntry;

import java.util.Map;
import org.junit.jupiter.api.Test;
import schemacrawler.tools.ai.tools.FunctionReturnMetadata;

public class FunctionReturnMetadataTest {

  @Test
  public void testToMetadataMapUsesNextStepsKey() {
    final FunctionReturnMetadata metadata =
        new FunctionReturnMetadata(
            "json",
            "application/json",
            "Inspect indexes next, because table details do not include index information.");

    final Map<String, Object> metadataMap = metadata.toMetadataMap("schemacrawler-ai/");

    assertThat(
        metadataMap,
        hasEntry(
            "schemacrawler-ai/next_steps",
            "Inspect indexes next, because table details do not include index information."));
    assertThat(metadataMap.containsKey("schemacrawler-ai/render-hint"), is(false));
  }
}
