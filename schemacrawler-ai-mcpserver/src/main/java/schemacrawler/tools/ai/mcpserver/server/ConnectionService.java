/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.mcpserver.server;

import static java.util.Objects.requireNonNull;

import jakarta.annotation.PostConstruct;
import java.sql.Connection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@Service
public class ConnectionService {

  private static ConnectionService instance;

  public static Connection getConnection() {
    requireNonNull(instance, "Connection service not available");
    requireNonNull(instance.databaseConnectionSource, "Database connection source not initialized");
    return instance.databaseConnectionSource.get();
  }

  @Autowired private DatabaseConnectionSource databaseConnectionSource;

  @PostConstruct
  public void initialize() {
    instance = this;
  }
}
