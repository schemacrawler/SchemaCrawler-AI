/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.utility;

import static java.util.Objects.requireNonNull;
import static tools.jackson.core.StreamReadFeature.IGNORE_UNDEFINED;
import static tools.jackson.core.StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION;
import static tools.jackson.core.StreamWriteFeature.IGNORE_UNKNOWN;
import static tools.jackson.databind.DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES;
import static tools.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static tools.jackson.databind.MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS;
import static tools.jackson.databind.MapperFeature.SORT_PROPERTIES_ALPHABETICALLY;
import static tools.jackson.databind.SerializationFeature.INDENT_OUTPUT;
import static tools.jackson.databind.SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS;
import static tools.jackson.databind.SerializationFeature.USE_EQUALITY_FOR_OBJECT_ID;

import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;
import tools.jackson.databind.cfg.MapperBuilder;
import tools.jackson.databind.json.JsonMapper;
import us.fatehi.utility.UtilityMarker;

@UtilityMarker
public final class JsonUtility {

  public static final ObjectMapper mapper = newConfiguredObjectMapper(JsonMapper.builder());

  private static ObjectMapper newConfiguredObjectMapper(
      final MapperBuilder<? extends ObjectMapper, ?> builder) {

    requireNonNull(builder, "No mapper builder provided");
    // De-serialization
    builder.enable(INCLUDE_SOURCE_IN_LOCATION, IGNORE_UNDEFINED);
    builder.enable(FAIL_ON_UNKNOWN_PROPERTIES);
    builder.disable(FAIL_ON_NULL_FOR_PRIMITIVES);
    // Serialization
    builder.enable(IGNORE_UNKNOWN);
    builder.enable(ORDER_MAP_ENTRIES_BY_KEYS, INDENT_OUTPUT, USE_EQUALITY_FOR_OBJECT_ID);
    builder.enable(SORT_PROPERTIES_ALPHABETICALLY, ACCEPT_CASE_INSENSITIVE_ENUMS);
    // Omit null and empty values in output
    builder.changeDefaultPropertyInclusion(
        incl -> incl.withValueInclusion(JsonInclude.Include.NON_EMPTY));

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    abstract class JacksonAnnotationMixIn {}
    builder.addMixIn(Object.class, JacksonAnnotationMixIn.class);

    final ObjectMapper objectMapper = builder.build();
    return objectMapper;
  }

  private JsonUtility() {
    // Prevent instantiation
  }
}
