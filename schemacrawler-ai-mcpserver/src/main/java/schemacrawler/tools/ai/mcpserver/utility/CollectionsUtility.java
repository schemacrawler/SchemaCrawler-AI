package schemacrawler.tools.ai.mcpserver.utility;

import static us.fatehi.utility.Utility.trimToEmpty;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import us.fatehi.utility.UtilityMarker;

@UtilityMarker
public class CollectionsUtility {

  public static Collection<String> setOfStrings(final String input) {
    final Set<String> setOfStrings =
        Arrays.stream(trimToEmpty(input).split(","))
            .filter(Objects::nonNull)
            .map(String::strip)
            .filter(s -> !s.isEmpty())
            .collect(Collectors.toSet());

    return setOfStrings;
  }

  private CollectionsUtility() {
    // Prevent instantiation
  }
}
