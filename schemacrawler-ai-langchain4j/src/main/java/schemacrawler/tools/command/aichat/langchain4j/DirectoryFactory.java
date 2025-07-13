/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.command.aichat.langchain4j;

import static dev.langchain4j.internal.ValidationUtils.ensureNotNull;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import us.fatehi.utility.UtilityMarker;

@UtilityMarker
public class DirectoryFactory {

  /**
   * Create a memory mapped file-system based directory.
   *
   * @param directoryPath Path for the directory.
   * @return Lucene directory
   */
  public static final Directory fsDirectory(final Path directoryPath) {
    ensureNotNull(directoryPath, "directoryPath");
    try {
      final Directory directory = new MMapDirectory(directoryPath);
      return directory;
    } catch (final Exception e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  /**
   * Create a memory mapped file-system based directory, in a temporary directory.
   *
   * @return Lucene directory
   */
  public static final Directory tempDirectory() {
    try {
      final Path directoryPath = Files.createTempDirectory(Directory.class.getCanonicalName());
      final Path newSubDirectory =
          Paths.get(directoryPath.toString(), Directory.class.getCanonicalName());
      return fsDirectory(newSubDirectory);
    } catch (final Exception e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  private DirectoryFactory() {
    // Prevent instantiation
  }
}
