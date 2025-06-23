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
