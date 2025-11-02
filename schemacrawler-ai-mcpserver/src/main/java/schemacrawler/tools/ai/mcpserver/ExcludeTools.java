/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.mcpserver;

import java.util.Collection;
import java.util.Collections;
import jakarta.annotation.Nullable;

/** Wrapper for bean that would otherwise have a collection of strings */
public record ExcludeTools(@Nullable Collection<String> excludeTools) {
  public ExcludeTools {
    if (excludeTools == null) {
      excludeTools = Collections.emptySet();
    }
  }
}
