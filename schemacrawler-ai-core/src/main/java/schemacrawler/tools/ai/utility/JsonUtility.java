/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.utility;

import static java.util.Objects.requireNonNull;
import static tools.jackson.core.StreamReadFeature.IGNORE_UNDEFINED;
import static tools.jackson.core.StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION;
import static tools.jackson.core.StreamWriteFeature.IGNORE_UNKNOWN;
import static tools.jackson.databind.DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES;
import static tools.jackson.databind.SerializationFeature.INDENT_OUTPUT;
import static tools.jackson.databind.SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS;
import static tools.jackson.databind.SerializationFeature.USE_EQUALITY_FOR_OBJECT_ID;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.MapperBuilder;
import tools.jackson.databind.json.JsonMapper;
import us.fatehi.utility.UtilityMarker;

@UtilityMarker
public class JsonUtility {

  public static final ObjectMapper mapper = newConfiguredObjectMapper(JsonMapper.builder());

  private static ObjectMapper newConfiguredObjectMapper(
      final MapperBuilder<? extends ObjectMapper, ?> mapperBuilder) {

    requireNonNull(mapperBuilder, "No mapper builder provided");
    mapperBuilder.enable(ORDER_MAP_ENTRIES_BY_KEYS, INDENT_OUTPUT, USE_EQUALITY_FOR_OBJECT_ID);
    mapperBuilder.disable(FAIL_ON_NULL_FOR_PRIMITIVES);
    mapperBuilder.enable(INCLUDE_SOURCE_IN_LOCATION, IGNORE_UNDEFINED);
    mapperBuilder.enable(IGNORE_UNKNOWN);

    final ObjectMapper objectMapper = mapperBuilder.build();
    return objectMapper;
  }

  private JsonUtility() {
    // Prevent instantiation
  }
}
