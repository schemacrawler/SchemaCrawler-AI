/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.mcpserver;

import jakarta.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

/** Wrapper for bean that would otherwise have a collection of strings */
public record ExcludeTools(@Nullable Collection<String> excludeTools) {
  public ExcludeTools {
    if (excludeTools == null) {
      excludeTools = Collections.emptySet();
    } else {
      excludeTools = new HashSet<>(excludeTools);
    }
  }

  public ExcludeTools() {
    this(Collections.emptySet());
  }
}
