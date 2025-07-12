/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */


package schemacrawler.tools.command.aichat.functions.text;

public final class ExitFunctionDefinition extends AbstractTextFunctionDefinition<NoParameters> {

  @Override
  public String getDescription() {
    return """
        Indicate when the user is done with their research,
        and wants to end the chat session.
        """
        .stripIndent()
        .replace("\n", " ")
        .trim();
  }

  @Override
  public Class<NoParameters> getParametersClass() {
    return NoParameters.class;
  }

  @Override
  public ExitFunctionExecutor newExecutor() {
    return new ExitFunctionExecutor(getFunctionName());
  }
}
