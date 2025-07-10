package schemacrawler.tools.command.aichat.mcp;

import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.tools.command.aichat.mcp.command.McpTransport;
import us.fatehi.utility.UtilityMarker;
import us.fatehi.utility.string.StringFormat;

@UtilityMarker
public class McpServerUtility {

  private static final Logger LOGGER = Logger.getLogger(McpServerUtility.class.getName());

  public static void startMcpServer(final McpTransport mcpTransport) {
    if (mcpTransport == null) {
      throw new IllegalArgumentException("MCP transport not provided");
    }
    switch (mcpTransport) {
      case sse:
        SseMcpServer.start();
        break;
      case stdio:
      default:
        StdioMcpServer.start();
        break;
    }
    LOGGER.log(Level.INFO,
        new StringFormat("MCP server is running with <%s> transport", mcpTransport));
  }

  private McpServerUtility() {
    // Prevent instantiation
  }
}
