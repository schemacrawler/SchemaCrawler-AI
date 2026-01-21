/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.mcpserver.server;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** Simple controller to check if the server is running. */
@RestController
public class HealthController {

  @Autowired private ServerHealth serverHealth;

  @GetMapping(
      value = {"/", "/health"},
      produces = APPLICATION_JSON_VALUE)
  public Map<String, Object> healthCheck() {
    return serverHealth.currentState();
  }
}
