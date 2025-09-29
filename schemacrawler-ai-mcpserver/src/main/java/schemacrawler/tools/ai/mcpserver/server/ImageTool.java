package schemacrawler.tools.ai.mcpserver.server;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

import io.modelcontextprotocol.spec.McpSchema.Annotations;
import io.modelcontextprotocol.spec.McpSchema.BlobResourceContents;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.EmbeddedResource;
import io.modelcontextprotocol.spec.McpSchema.ImageContent;
import io.modelcontextprotocol.spec.McpSchema.ReadResourceResult;
import io.modelcontextprotocol.spec.McpSchema.ResourceLink;
import io.modelcontextprotocol.spec.McpSchema.Role;
import io.modelcontextprotocol.spec.McpSchema.TextContent;
import io.modelcontextprotocol.spec.McpSchema.TextResourceContents;
import java.util.List;
import org.springaicommunity.mcp.annotation.McpResource;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springframework.stereotype.Service;

@Service
public class ImageTool {

  // Example base64-encoded PNG image (small red dot)
  final String base64Image =
      "iVBORw0KGgoAAAANSUhEUgAAAAUAAAAFCAYAAACNbyblAAAAHElEQVQI12P4//8/w38GIAXDIBKE0DHxgljNBAAO9TXL0Y4OHwAAAABJRU5ErkJggg==";
  final Annotations annotations = new Annotations(List.of(Role.ASSISTANT), 1.0);

  @McpTool(name = "embedded-image", description = "Gets an image")
  public CallToolResult getEmbeddedImage() {
    final BlobResourceContents content =
        new BlobResourceContents("results://red-ball.png", IMAGE_PNG_VALUE, base64Image);
    return CallToolResult.builder()
        .content(List.of(new EmbeddedResource(annotations, content)))
        .isError(false)
        .build();
  }

  @McpTool(name = "embedded-text", description = "Gets some text")
  public CallToolResult getEmbeddedText() {
    final TextResourceContents content =
        new TextResourceContents(
            "results://hello-world.json", APPLICATION_JSON_VALUE, "{\"hello\": \"world\"}");
    return CallToolResult.builder()
        .content(List.of(new EmbeddedResource(annotations, content)))
        .isError(false)
        .build();
  }

  @McpTool(name = "direct-image", description = "Gets an image")
  public CallToolResult getDirectImage() {
    final ImageContent content = new ImageContent(annotations, base64Image, IMAGE_PNG_VALUE);
    return CallToolResult.builder().content(List.of(content)).isError(false).build();
  }

  @McpTool(name = "direct-text", description = "Gets text")
  public CallToolResult getDirectText() {
    final TextContent content = new TextContent(annotations, "Hello, World!");
    return CallToolResult.builder().content(List.of(content)).isError(false).build();
  }

  @McpTool(name = "resource-link", description = "Gets some resource")
  public CallToolResult getResourceLink() {
    try {
      final ResourceLink content =
          ResourceLink.builder()
              .annotations(annotations)
              .mimeType(APPLICATION_JSON_VALUE)
              .title("Hello, world!")
              .name("hello-world.json")
              .uri("resources://hello-world")
              .build();
      return CallToolResult.builder().content(List.of(content)).isError(false).build();
    } catch (Exception e) {
      e.printStackTrace();
      return CallToolResult.builder()
          .content(List.of(new TextContent(e.getMessage())))
          .isError(true)
          .build();
    }
  }

  @McpResource(
      uri = "resources://hello-world",
      name = "hello-world",
      description = "Returns hello world.")
  public ReadResourceResult resourceHelloWorld() {
    final TextResourceContents content =
        new TextResourceContents(
            "results://hello-world.json", APPLICATION_JSON_VALUE, "{\"hello\": \"world\"}");
    return new ReadResourceResult(List.of(content));
  }

  @McpResource(
      uri = "resources://red-ball",
      name = "red-ball",
      description = "Returns red ball image.")
  public ReadResourceResult resourceRedBall() {
    final BlobResourceContents content =
        new BlobResourceContents("results://red-ball.png", IMAGE_PNG_VALUE, base64Image);
    return new ReadResourceResult(List.of(content));
  }
}
