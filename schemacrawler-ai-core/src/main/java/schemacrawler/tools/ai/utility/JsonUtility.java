/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import schemacrawler.tools.ai.tools.FunctionParameters;
import us.fatehi.utility.UtilityMarker;

@UtilityMarker
public class JsonUtility {

  public static final ObjectMapper mapper = new ObjectMapper();

  public static String parametersToString(final FunctionParameters parameters) {
    if (parameters == null) {
      return "";
    }
    try {
      return mapper.writeValueAsString(parameters);
    } catch (final JsonProcessingException e) {
      return "";
    }
  }

  public static String wrapException(final Exception exception) {
    try {
      return mapper.writeValueAsString(new ExceptionInfo(exception));
    } catch (final JsonProcessingException e) {
      return "";
    }
  }

  private JsonUtility() {
    // Prevent instantiation
  }
}
