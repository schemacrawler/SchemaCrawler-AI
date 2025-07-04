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

import static schemacrawler.tools.command.aichat.utility.JsonUtility.mapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.command.aichat.tools.AbstractSchemaCrawlerFunctionExecutor;
import schemacrawler.tools.command.aichat.tools.FunctionParameters;
import schemacrawler.utility.MetaDataUtility;
import us.fatehi.utility.property.PropertyName;

public abstract class AbstractJsonFunctionExecutor<P extends FunctionParameters>
    extends AbstractSchemaCrawlerFunctionExecutor<P> {

  protected AbstractJsonFunctionExecutor(final PropertyName functionName) {
    super(functionName);
  }

  protected final void refilterCatalog() {
    final SchemaCrawlerOptions options = createSchemaCrawlerOptions();
    MetaDataUtility.reduceCatalog(catalog, options);
  }

  protected final ObjectNode wrapList(final ArrayNode list) {
    final ObjectNode objectNode = mapper.createObjectNode();
    objectNode.put("db", catalog.getDatabaseInfo().getDatabaseProductName());
    if (list != null) {
      objectNode.set("list", list);
    }
    return objectNode;
  }
}
