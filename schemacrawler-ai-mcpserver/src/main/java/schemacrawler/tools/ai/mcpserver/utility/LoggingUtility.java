/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.mcpserver.utility;

import static java.util.Objects.requireNonNullElse;

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
import schemacrawler.tools.ai.mcpserver.server.DatabaseIdentity;
import schemacrawler.tools.ai.utility.JsonUtility;
import schemacrawler.tools.ai.utility.SchemaCrawlerAiVersion;
import tools.jackson.databind.ObjectWriter;
import us.fatehi.utility.UtilityMarker;
import us.fatehi.utility.property.JvmArchitectureInfo;
import us.fatehi.utility.property.OperatingSystemInfo;

@UtilityMarker
public final class LoggingUtility {

  private static final Logger LOGGER = Logger.getLogger(LoggingUtility.class.getCanonicalName());

  public static void logStartup(final BeanFactory beanFactory) {
    if (beanFactory == null || !LOGGER.isLoggable(Level.INFO)) {
      return;
    }

    final Boolean isInErrorState = beanFactory.getBean("isInErrorState", Boolean.class);
    if (Boolean.TRUE.equals(requireNonNullElse(isInErrorState, Boolean.FALSE))) {
      return;
    }

    final DatabaseIdentity databaseIdentity =
        beanFactory.getBean("databaseIdentity", DatabaseIdentity.class);
    final Catalog catalog = beanFactory.getBean("catalog", Catalog.class);
    final ERModel erModel = beanFactory.getBean("erModel", ERModel.class);

    try (final StringWriter stringWriter = new StringWriter();
        final PrintWriter writer = new PrintWriter(stringWriter)) {

      final ObjectWriter jsonPrinter = JsonUtility.mapper.writerWithDefaultPrettyPrinter();

      writer.println("-".repeat(80));

      if (databaseIdentity != null) {
        writer.println(
            "Database server:%n%s".formatted(jsonPrinter.writeValueAsString(databaseIdentity)));
        writer.println();
      }

      if (catalog != null) {
        writer.println("Catalog summary:%n%s".formatted(CatalogSummaryUtility.summarize(catalog)));
        writer.println();
      }

      if (erModel != null) {
        writer.println("ER Model summary:%n%s".formatted(ERModelSummaryUtility.summarize(erModel)));
        writer.println();
      }

      writer.println("-".repeat(80));

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
      writer.println(JvmArchitectureInfo.jvmArchitectureInfo());
      writer.println(OperatingSystemInfo.operatingSystemInfo());

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
