/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.tools;

import static us.fatehi.utility.Utility.trimToEmpty;

import java.util.Map;

public record FunctionReturnMetadata(String format, String mimeType, String nextSteps) {

  public static final FunctionReturnMetadata TEXT =
      new FunctionReturnMetadata("text", "text/plain", "");

  public FunctionReturnMetadata {
    format = trimToEmpty(format);
    mimeType = trimToEmpty(mimeType);
    nextSteps = trimToEmpty(nextSteps);
  }

  /** Returns a flat map with no key prefix. */
  public Map<String, Object> toMetadataMap() {
    return toMetadataMap(null);
  }

  /** Returns a flat map with the given prefix prepended to each key. */
  public Map<String, Object> toMetadataMap(final String prefix) {
    final String namespace = trimToEmpty(prefix);
    return Map.of(
        namespace + "format",
        format,
        namespace + "mime-type",
        mimeType,
        namespace + "next_steps",
        nextSteps);
  }
}
