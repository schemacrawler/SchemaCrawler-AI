package schemacrawler.tools.command.aichat.mcp;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;
import schemacrawler.Version;

@Service
public class CommonService {

  @Tool(
      name = "get-schemacrawler-version",
      description = "Gets the version of SchemaCrawler",
      returnDirect = true)
  public String getSchemaCrawlerVersion(
      @ToolParam(
              description =
                  """
      Current date, as an ISO 8601 local date.
      """,
              required = false)
          final String date) {
    System.out.printf("get-schemacrawler-version called with %s", date);
    return Version.about();
  }
}
