/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */


package schemacrawler.tools.command.aichat.tools;

import java.util.UUID;
import schemacrawler.tools.executable.Command;

public interface FunctionExecutor<P extends FunctionParameters> extends Command<P, FunctionReturn> {

  String getDescription();

  UUID getExecutorInstanceId();
}
