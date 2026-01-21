/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.mcpserver.verification_test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static schemacrawler.tools.ai.utility.JsonUtility.mapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import schemacrawler.tools.ai.mcpserver.McpServerTransportType;
import schemacrawler.tools.ai.utility.SchemaCrawlerAiVersion;
import tools.jackson.databind.JsonNode;
import us.fatehi.utility.readconfig.SystemPropertiesConfig;

/**
 * Integration test to verify the published Docker image is viable by starting it and checking the
 * health endpoint.
 */
@Testcontainers
@Tag("docker")
@DisplayName("Test Docker image build")
public class DockerImageBuildTest {

  private static final Logger LOGGER =
      Logger.getLogger(DockerImageBuildTest.class.getCanonicalName());

  private static final DockerImageName DOCKER_IMAGE_NAME =
      DockerImageName.parse("schemacrawler/schemacrawler-ai")
          .withTag(new SystemPropertiesConfig().getStringValue("docker_image_tag", "latest"));
  private static final int INTERNAL_CONTAINER_PORT = 8181;

  private static final Map<String, String> env =
      Map.of(
          "SCHCRWLR_MCP_SERVER_TRANSPORT",
          McpServerTransportType.http.name(),
          "SCHCRWLR_SERVER",
          "sqlite",
          "SCHCRWLR_DATABASE",
          "sc.db",
          "SERVER_PORT",
          String.valueOf(INTERNAL_CONTAINER_PORT));

  @Container
  private final GenericContainer<?> mcpServerContainer =
      new GenericContainer<>(DOCKER_IMAGE_NAME)
          .withImagePullPolicy(imageName -> false)
          .withExposedPorts(INTERNAL_CONTAINER_PORT)
          .withEnv(env)
          .waitingFor(Wait.forLogMessage(".*SchemaCrawler AI MCP Server is running.*\\R", 1))
          .withStartupTimeout(Duration.ofSeconds(60));

  @Test
  @DisplayName("Docker image starts successfully and health endpoint returns valid JSON")
  public void testDockerImageHealth() throws IOException, InterruptedException {

    LOGGER.log(Level.CONFIG, "Verifying " + DOCKER_IMAGE_NAME);

    // Get the mapped port for the MCP server
    final Integer mappedPort = mcpServerContainer.getMappedPort(INTERNAL_CONTAINER_PORT);
    assertThat("Container port should be mapped", mappedPort, is(notNullValue()));

    // Build the health endpoint URL
    final String healthUrl = String.format("http://localhost:%d/health", mappedPort);

    // Create HTTP client and request
    final HttpClient client = HttpClient.newHttpClient();
    final HttpRequest request = HttpRequest.newBuilder().uri(URI.create(healthUrl)).GET().build();

    // Execute the request
    final HttpResponse<String> response =
        client.send(request, HttpResponse.BodyHandlers.ofString());

    // Verify the response
    assertThat("Health endpoint should return HTTP 200", response.statusCode(), is(200));

    final JsonNode state = mapper.readTree(response.body());
    assertThat(state.get("_server").asString(), is(new SchemaCrawlerAiVersion().toString()));
    assertThat(state.get("in-error-state").asBoolean(), is(false));
    assertThat(state.get("transport").asString(), is("http"));
    assertThat(state.get("exclude-tools").isEmpty(), is(true));
  }
}
