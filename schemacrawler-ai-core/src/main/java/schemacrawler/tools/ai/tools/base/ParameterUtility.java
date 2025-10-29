/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.tools.base;

import com.fasterxml.jackson.core.JsonProcessingException;
import schemacrawler.tools.ai.tools.FunctionParameters;
import schemacrawler.tools.ai.utility.JsonUtility;
import us.fatehi.utility.UtilityMarker;

@UtilityMarker
public class ParameterUtility {

  public static String parametersToString(final FunctionParameters parameters) {
    if (parameters == null) {
      return "";
    }
    try {
      return JsonUtility.mapper.writeValueAsString(parameters);
    } catch (final JsonProcessingException e) {
      return "";
    }
  }

  private ParameterUtility() {
    // Prevent instantiation
  }
}
