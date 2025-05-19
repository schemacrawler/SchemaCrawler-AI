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

package schemacrawler.tools.command.aichat.tools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.validation.constraints.NotNull;

public record ToolSpecification(
    @NotNull String name, @NotNull String description, @NotNull ObjectNode parameters) {

  public String getParametersAsString() {
    return parameters.toPrettyString();
  }

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  public JsonNode getToolSpecification() {
    final ObjectNode toolSpecification = OBJECT_MAPPER.createObjectNode();
    toolSpecification.put("name", name);
    toolSpecification.put("description", description);
    toolSpecification.put("parameters", parameters);
    return toolSpecification;
  }

  public String toString() {
    return getToolSpecification().toPrettyString();
  }
}
