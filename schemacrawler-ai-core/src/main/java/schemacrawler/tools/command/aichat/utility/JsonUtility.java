package schemacrawler.tools.command.aichat.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import schemacrawler.tools.command.aichat.tools.FunctionParameters;
import us.fatehi.utility.UtilityMarker;

@UtilityMarker
public class JsonUtility {

  public static final ObjectMapper mapper = new ObjectMapper();

  public static String parametersToString(final FunctionParameters parameters) {
    if (parameters == null) {
      return "";
    }
    try {
      return new ObjectMapper().writeValueAsString(parameters);
    } catch (final JsonProcessingException e) {
      return "";
    }
  }

  private JsonUtility() {
    // Prevent instantiation
  }
}
