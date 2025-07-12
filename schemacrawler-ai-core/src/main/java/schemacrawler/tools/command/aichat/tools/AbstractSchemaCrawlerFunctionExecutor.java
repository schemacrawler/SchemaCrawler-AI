/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */


package schemacrawler.tools.command.aichat.tools;

import java.util.regex.Pattern;
import static us.fatehi.utility.Utility.isBlank;
import schemacrawler.inclusionrule.IncludeAll;
import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.inclusionrule.RegularExpressionInclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import us.fatehi.utility.property.PropertyName;

public abstract class AbstractSchemaCrawlerFunctionExecutor<P extends FunctionParameters>
    extends AbstractFunctionExecutor<P> {

  protected AbstractSchemaCrawlerFunctionExecutor(final PropertyName functionName) {
    super(functionName);
  }

  protected abstract SchemaCrawlerOptions createSchemaCrawlerOptions();

  protected InclusionRule makeInclusionRule(final String objectName) {
    final InclusionRule inclusionRule;
    if (isBlank(objectName)) {
      inclusionRule = new IncludeAll();
    } else {
      final Pattern dependantObjectPattern = makeNameInclusionPattern(objectName);
      inclusionRule = new RegularExpressionInclusionRule(dependantObjectPattern);
    }
    return inclusionRule;
  }

  private Pattern makeNameInclusionPattern(final String name) {
    if (isBlank(name)) {
      throw new IllegalArgumentException("Blank name provided");
    }
    final String pattern = String.format(".*%s.*", name);
    final int flags = Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE;
    return Pattern.compile(pattern, flags);
  }
}
