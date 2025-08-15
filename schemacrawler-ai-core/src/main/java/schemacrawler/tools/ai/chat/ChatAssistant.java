/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.chat;

import java.sql.Connection;
import schemacrawler.schema.Catalog;

public interface ChatAssistant extends AutoCloseable {

  String chat(String prompt);

  void configure(ChatOptions aiChatOptions, Catalog catalog, Connection connection);

  boolean shouldExit();
}
