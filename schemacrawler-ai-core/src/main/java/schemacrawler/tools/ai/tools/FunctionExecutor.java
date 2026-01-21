/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.tools;

import schemacrawler.tools.executable.Command;

public interface FunctionExecutor<P extends FunctionParameters>
    extends Command<P, FunctionReturn> {}
