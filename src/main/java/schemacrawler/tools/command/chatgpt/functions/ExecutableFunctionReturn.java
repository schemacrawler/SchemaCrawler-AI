/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.tools.command.chatgpt.functions;

import static java.util.Objects.requireNonNull;
import java.io.StringWriter;
import java.util.function.Function;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.command.chatgpt.FunctionReturn;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.OutputOptionsBuilder;

public class ExecutableFunctionReturn implements FunctionReturn {

  private final SchemaCrawlerExecutable executable;
  private final Function<Catalog, Boolean> resultsChecker;

  protected ExecutableFunctionReturn(
      final SchemaCrawlerExecutable executable, final Function<Catalog, Boolean> resultsChecker) {
    this.executable = requireNonNull(executable, "SchemaCrawler executable not provided");
    this.resultsChecker = requireNonNull(resultsChecker, "Check for results");
  }

  @Override
  public String get() {

    final StringWriter writer = new StringWriter();
    final OutputOptions outputOptions =
        OutputOptionsBuilder.builder().withOutputWriter(writer).toOptions();

    executable.setOutputOptions(outputOptions);
    executable.execute();

    if (!resultsChecker.apply(executable.getCatalog())) {
      return "There were no matching results for your query.";
    }

    return writer.toString();
  }
}
