package schemacrawler.tools.ai.utility;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import us.fatehi.utility.UtilityMarker;

@UtilityMarker
public class SqlTableExtractor {

	private static final Pattern TABLE_PATTERN = Pattern.compile(
			"\\b(?:FROM|JOIN|INTO|UPDATE)\\s+(?:\\w+\\.)?(\\w+)(?:\\s+AS\\s+\\w+|\\s+\\w+)?",
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

	/**
	 * Extracts all referenced table names from the SQL statement or fragment.
	 * Handles basic cases with FROM, JOIN, INTO, UPDATE keywords, schema prefixes,
	 * and aliases. Robust for fragments and invalid SQL.
	 *
	 * @param sql the SQL statement or fragment
	 * @return set of unique table names (without schema)
	 */
	public static Set<String> extractTables(final String sql) {
		if (sql == null || sql.trim().isEmpty()) {
			return Collections.emptySet();
		}
		final Set<String> tables = new LinkedHashSet<>();
		final Matcher matcher = TABLE_PATTERN.matcher(sql);
		while (matcher.find()) {
			final String tableName = matcher.group(1);
			tables.add(tableName);
		}
		return tables;
	}

	private SqlTableExtractor() {
		// Prevent instantiation
	}
}
