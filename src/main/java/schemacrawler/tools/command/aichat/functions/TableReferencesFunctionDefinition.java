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

package schemacrawler.tools.command.aichat.functions;

public final class TableReferencesFunctionDefinition
    extends AbstractFunctionDefinition<TableReferencesFunctionParameters> {

  @Override
  public String getDescription() {
    return "Gets the relationships of a database table, either child tables or parent tables. "
        + "Child tables are also known as dependent tables or foreign key tables. "
        + "Parent tables are also known as referenced tables, or primary key tables.";
  }

  @Override
  public Class<TableReferencesFunctionParameters> getParametersClass() {
    return TableReferencesFunctionParameters.class;
  }

  @Override
  public TableReferencesFunctionExecutor newExecutor() {
    return new TableReferencesFunctionExecutor(getFunctionName());
  }
}
