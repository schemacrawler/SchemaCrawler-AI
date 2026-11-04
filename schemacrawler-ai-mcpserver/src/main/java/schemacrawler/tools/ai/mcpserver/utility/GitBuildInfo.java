/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.mcpserver.utility;

import static us.fatehi.utility.Utility.isBlank;
import static us.fatehi.utility.Utility.trimToEmpty;

record GitBuildInfo(String commitId, String commitTime) {

  GitBuildInfo {
    commitId = trimToEmpty(commitId);
    commitTime = trimToEmpty(commitTime);
  }

  public boolean isEmpty() {
    return isBlank(commitId);
  }

  @Override
  public String toString() {
    final StringBuilder buffer = new StringBuilder();
    if (!isBlank(commitId)) {
      buffer.append("  Commit SHA: ").append(commitId).append(System.lineSeparator());
    }
    if (!isBlank(commitTime)) {
      buffer.append("  Commit time: ").append(commitTime).append(System.lineSeparator());
    }
    if (buffer.isEmpty()) {
      return "";
    }
    buffer.insert(0, "Git information:\n");
    return buffer.toString();
  }
}
