/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.tools.base;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.isBlank;

import java.util.regex.Pattern;
import schemacrawler.inclusionrule.IncludeAll;
import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.inclusionrule.RegularExpressionInclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.ai.tools.FunctionExecutor;
import schemacrawler.tools.ai.tools.FunctionParameters;
import schemacrawler.tools.ai.tools.FunctionReturn;
import schemacrawler.tools.executable.BaseCommand;
import us.fatehi.utility.property.PropertyName;

public abstract class AbstractFunctionExecutor<P extends FunctionParameters>
    extends BaseCommand<P, FunctionReturn> implements FunctionExecutor<P> {

  protected AbstractFunctionExecutor(final PropertyName functionName) {
    super(requireNonNull(functionName, "Function name not provided"));
  }

  @Override
  public final String toString() {
    return command.getName();
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
    final String pattern = ".*%s.*".formatted(name);
    final int flags = Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE;
    return Pattern.compile(pattern, flags);
  }
}
