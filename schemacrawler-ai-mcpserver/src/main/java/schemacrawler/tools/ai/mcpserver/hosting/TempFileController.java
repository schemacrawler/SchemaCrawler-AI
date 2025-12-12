/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.mcpserver.hosting;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import org.apache.tika.Tika;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TempFileController {

  private final Tika tika = new Tika();

  @GetMapping("/temp/{filename:.+}")
  public ResponseEntity<?> serveTempFile(@PathVariable final String filename) {

    writeTempFile(filename);

    final Path filePath = Paths.get(System.getProperty("java.io.tmpdir")).resolve(filename);
    if (!Files.exists(filePath) || !Files.isRegularFile(filePath) || !Files.isReadable(filePath)) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body("File not found: " + filePath.getFileName());
    }

    MediaType mediaType;
    try (final InputStream inputStream = Files.newInputStream(filePath, StandardOpenOption.READ)) {
      final String mimeType = tika.detect(inputStream);
      mediaType = MediaType.parseMediaType(mimeType);
    } catch (final IOException e) {
      mediaType = MediaType.TEXT_PLAIN;
    }

    try {
      final InputStream inputStream = Files.newInputStream(filePath, StandardOpenOption.READ);
      final InputStreamResource resource = new InputStreamResource(inputStream);
      return ResponseEntity.ok()
          .header(
              HttpHeaders.CONTENT_DISPOSITION,
              "inline; filename=\"" + filePath.getFileName() + "\"")
          .contentLength(Files.size(filePath))
          .contentType(mediaType)
          .body(resource);

    } catch (final IOException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Error reading file: " + e.getMessage());
    }
  }

  private void writeTempFile(final String filename) {
    try {
      final Path filePath = Paths.get(System.getProperty("java.io.tmpdir")).resolve(filename);
      Files.writeString(filePath, "HELLO, WORLD!");
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }
}
