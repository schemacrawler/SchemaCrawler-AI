package schemacrawler.tools.command.aichat.mcp;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
public class WeatherService {

  @Tool(name = "get-weather", description = "Get the weather in a city", returnDirect = true)
  public String weather(
      @ToolParam(
              description =
                  """
      Name of the city, including state and country.
      For example, Boston, MA, USA
      """,
              required = true)
          final String city) {
    System.out.printf(">>> get-weather called with %s", city);
    final String message = String.format("`get-weather` called with (`%s`)", city);
    return message;
  }
}
