/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.tools;

import schemacrawler.ermodel.model.ERModel;
import schemacrawler.tools.executable.BaseCommand;

public interface FunctionExecutor<P extends FunctionParameters> extends BaseCommand<P> {

  /**
   * Executes command, after configuration and pre-checks. May throw runtime exceptions on errors.
   *
   * <p>R Return value
   */
  <R extends FunctionReturn> R call() throws Exception;

  void setERModel(ERModel erModel);
}
