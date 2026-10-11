/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.tools.base;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.isBlank;

import java.util.regex.Pattern;
import schemacrawler.ermodel.model.ERModel;
import schemacrawler.filter.CatalogProjectionBuilder;
import schemacrawler.importance.model.ImportanceModel;
import schemacrawler.inclusionrule.IncludeAll;
import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.inclusionrule.RegularExpressionInclusionRule;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.ai.tools.FunctionExecutor;
import schemacrawler.tools.ai.tools.FunctionParameters;
import schemacrawler.tools.command.AbstractCommand;
import schemacrawler.tools.utility.SchemaCrawlerUtility;
import us.fatehi.utility.property.PropertyName;

public abstract class AbstractFunctionExecutor<P extends FunctionParameters>
    extends AbstractCommand<P> implements FunctionExecutor<P> {

  private ImportanceModel importanceModel;
  private Catalog selectedCatalog;
  private ERModel selectedERModel;

  protected AbstractFunctionExecutor(final PropertyName functionName) {
    super(requireNonNull(functionName, "Function name not provided"));
  }

  @Override
  public final String toString() {
    return command.getName();
  }

  @Override
  public final void setImportanceModel(final ImportanceModel importanceModel) {
    this.importanceModel = importanceModel;
  }

  protected abstract SchemaCrawlerOptions createSchemaCrawlerOptions();

  /** Catalog projection for this invocation only. The shared baseline catalog is not modified. */
  protected final Catalog getSelectedCatalog() {
    if (selectedCatalog == null) {
      selectedCatalog =
          CatalogProjectionBuilder.builder(getCatalog()).withOptions(selectionOptions()).build();
    }
    return selectedCatalog;
  }

  /** Entity-relationship model for the selected catalog of this invocation. */
  protected final ERModel getSelectedERModel() {
    if (selectedERModel == null) {
      selectedERModel = SchemaCrawlerUtility.buildERModel(getSelectedCatalog());
    }
    return selectedERModel;
  }

  protected SchemaCrawlerOptions selectionOptions() {
    return createSchemaCrawlerOptions();
  }

  protected final ImportanceModel getImportanceModel() {
    return importanceModel;
  }

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
