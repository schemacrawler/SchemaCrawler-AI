/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.mcpserver.server;

import static java.util.Objects.requireNonNull;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@Service
public class DatabaseConnectionService {

  private static DatabaseConnectionService instance;

  public static DatabaseConnectionSource getDatabaseConnectionSource() {
    requireNonNull(instance, "Connection service not available");
    requireNonNull(instance.databaseConnectionSource, "Database connection source not initialized");
    return instance.databaseConnectionSource;
  }

  @Autowired private DatabaseConnectionSource databaseConnectionSource;

  @PostConstruct
  public void initialize() {
    instance = this;
  }
}
