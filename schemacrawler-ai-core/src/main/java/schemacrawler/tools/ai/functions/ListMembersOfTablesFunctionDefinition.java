/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.functions;

import schemacrawler.tools.ai.tools.base.AbstractFunctionDefinition;

public final class ListMembersOfTablesFunctionDefinition
    extends AbstractFunctionDefinition<ListMembersOfTablesFunctionParameters> {

  @Override
  public String getDescription() {
    return """
       Lists members belonging to one or more database tables across the schema,
       including columns, indexes, foreign keys, and triggers. Use this tool when
       you need members belonging to one or more tables, not when you need to
       discover tables or other schema objects; use `list` for that. Supports regular
       expression based table and member name filtering. For complete physical
       table details, use `describe_tables`.
       Returns JSON data.
    """
        .stripIndent()
        .replace("\n", " ")
        .trim();
  }

  @Override
  public Class<ListMembersOfTablesFunctionParameters> getParametersClass() {
    return ListMembersOfTablesFunctionParameters.class;
  }

  @Override
  public String getTitle() {
    return "List members of one or more tables";
  }

  @Override
  public ListMembersOfTablesFunctionExecutor newExecutor() {
    return new ListMembersOfTablesFunctionExecutor(getFunctionName());
  }

  @Override
  public ListMembersOfTablesFunctionParameters newParameters() {
    return new ListMembersOfTablesFunctionParameters();
  }
}
