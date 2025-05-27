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

import java.util.Collection;
import java.util.regex.Pattern;
import static us.fatehi.utility.Utility.isBlank;
import schemacrawler.schema.Schema;
import schemacrawler.tools.command.aichat.functions.text.AbstractFunctionExecutor;
import schemacrawler.tools.command.aichat.tools.FunctionParameters;
import us.fatehi.utility.property.PropertyName;

public abstract class AbstractJsonFunctionExecutor<P extends FunctionParameters>
    extends AbstractFunctionExecutor<P> {

  protected AbstractJsonFunctionExecutor(final PropertyName functionName) {
    super(functionName);
  }

  protected Pattern makeNameInclusionPattern(final String name) {
    if (isBlank(name)) {
      return Pattern.compile(".*");
    }
    final boolean hasDefaultSchema = hasDefaultSchema();
    return Pattern.compile(String.format(".*%s(?i)%s(?-i)", hasDefaultSchema ? "" : "\\.", name));
  }

  private boolean hasDefaultSchema() {
    final Collection<Schema> schemas = catalog.getSchemas();
    final int schemaCount = schemas.size();
    for (final Schema schema : schemas) {
      if (isBlank(schema.getFullName()) && schemaCount == 1) {
        return true;
      }
    }
    return false;
  }
}
