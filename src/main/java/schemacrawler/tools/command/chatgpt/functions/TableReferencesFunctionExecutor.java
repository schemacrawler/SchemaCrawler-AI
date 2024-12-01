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

import java.util.Optional;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.tools.command.chatgpt.FunctionReturn;
import schemacrawler.utility.MetaDataUtility;
import us.fatehi.utility.property.PropertyName;

public final class TableReferencesFunctionExecutor
    extends AbstractFunctionExecutor<TableReferencesFunctionParameters> {

  protected TableReferencesFunctionExecutor(final PropertyName functionName) {
    super(functionName);
  }

  @Override
  public FunctionReturn execute() {
    // Re-filter catalog
    MetaDataUtility.reduceCatalog(catalog, SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions());

    final Optional<Table> firstMatchedTable =
        catalog.getTables().stream()
            .filter(table -> table.getName().matches("(?i)" + args.getTableName()))
            .findFirst();

    if (firstMatchedTable.isPresent()) {
      final Table table = firstMatchedTable.get();
      return new TableReferencesFunctionReturn(table, args.getTableReferenceType());
    }
    return new NoResultsReturn();
  }
}
