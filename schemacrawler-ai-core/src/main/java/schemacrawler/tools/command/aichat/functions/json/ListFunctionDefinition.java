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

package schemacrawler.tools.command.aichat.functions.json;

public final class ListFunctionDefinition
    extends AbstractJsonFunctionDefinition<ListFunctionParameters> {

  @Override
  public String getDescription() {
    return """
        List names of database objects like tables, routines
        (that is, functions and stored procedures), sequences, or synonyms.
        Returns JSON data.
        """
        .stripIndent()
        .replace("\n", " ")
        .trim();
  }

  @Override
  public Class<ListFunctionParameters> getParametersClass() {
    return ListFunctionParameters.class;
  }

  @Override
  public ListFunctionExecutor newExecutor() {
    return new ListFunctionExecutor(getFunctionName());
  }
}
