/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.mcpserver.utility;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.BeanFactory;
import schemacrawler.ermodel.model.ERModel;
import schemacrawler.loader.catalog.summary.CatalogSummaryUtility;
import schemacrawler.loader.ermodel.summary.ERModelSummaryUtility;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.Version;
import schemacrawler.tools.ai.mcpserver.McpServerTransportType;
import schemacrawler.tools.ai.utility.SchemaCrawlerAiVersion;
import us.fatehi.utility.UtilityMarker;

@UtilityMarker
public final class LoggingUtility {

  private static final Logger LOGGER = Logger.getLogger(LoggingUtility.class.getCanonicalName());

  public static void logStartup(final BeanFactory beanFactory) {
    if (beanFactory == null || !LOGGER.isLoggable(Level.INFO)) {
      return;
    }

    final Catalog catalog = beanFactory.getBean("catalog", Catalog.class);
    final ERModel erModel = beanFactory.getBean("erModel", ERModel.class);

    try (final StringWriter stringWriter = new StringWriter();
        final PrintWriter writer = new PrintWriter(stringWriter)) {

      if (catalog != null) {
        writer.println("Catalog summary:%n%s".formatted(CatalogSummaryUtility.summarize(catalog)));
        writer.println();
      }

      if (erModel != null) {
        writer.println("ER Model summary:%n%s".formatted(ERModelSummaryUtility.summarize(erModel)));
        writer.println();
      }

      writer.close();

      LOGGER.log(Level.INFO, stringWriter.toString());
    } catch (final Exception e) {
      // Ignore exception
    }
  }

  public static void logStartup(final McpServerTransportType mcpTransport) {
    if (mcpTransport == null || !LOGGER.isLoggable(Level.INFO)) {
      return;
    }

    try (final StringWriter stringWriter = new StringWriter();
        final PrintWriter writer = new PrintWriter(stringWriter)) {

      writer.println();
      writer.println("-".repeat(80));

      writer.println(new SchemaCrawlerAiVersion());
      writer.println(Version.version());
      writer.println(new SpringAiVersion());
      writer.println(new SpringBootFrameworkVersion());
      writer.println(new SpringFrameworkVersion());

      writer.println();
      writer.println(
          "SchemaCrawler AI MCP Server is running with %s transport"
              .formatted(mcpTransport.getDescription()));

      writer.println("-".repeat(80));
      writer.println();

      writer.close();

      LOGGER.log(Level.INFO, stringWriter.toString());
    } catch (final Exception e) {
      // Ignore exception
    }
  }

  private LoggingUtility() {
    // Prevent instantiation
  }
}
