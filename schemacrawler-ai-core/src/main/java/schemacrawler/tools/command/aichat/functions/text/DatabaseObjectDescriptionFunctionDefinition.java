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

package schemacrawler.tools.command.aichat.functions.text;

public final class DatabaseObjectDescriptionFunctionDefinition
    extends AbstractFunctionDefinition<DatabaseObjectDescriptionFunctionParameters> {

  @Override
  public String getDescription() {
    return """
        Get the details and description of database objects like
        tables (all types, including views),
        routines (that is, functions and stored procedures),
        sequences, or synonyms.
        """
        .stripIndent()
        .replace("\n", " ")
        .trim();
  }

  @Override
  public Class<DatabaseObjectDescriptionFunctionParameters> getParametersClass() {
    return DatabaseObjectDescriptionFunctionParameters.class;
  }

  @Override
  public DatabaseObjectDescriptionFunctionExecutor newExecutor() {
    return new DatabaseObjectDescriptionFunctionExecutor(getFunctionName());
  }
}
