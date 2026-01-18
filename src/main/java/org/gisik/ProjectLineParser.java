package org.gisik;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class responsible for parsing single lines from a project file.
 *
 * This parser interprets a structured text line where:
 * - The first token represents the entry type (e.g. SHAPE, CSV, OSM)
 * - All following arguments must be enclosed in double quotes
 *
 * The method extracts all tokens into a list of strings, preserving
 * their original order.
 *
 * Example input:
 * <pre>
 * CSV "C:/data/points.csv" "," "EPSG2180" "lon" "lat" "true"
 * </pre>
 *
 * Example output:
 * <pre>
 * ["CSV", "C:/data/points.csv", ",", "EPSG2180", "lon", "lat", "true"]
 * </pre>
 *
 * This class is stateless and cannot be instantiated.
 */
public final class ProjectLineParser {

    /**
     * Parses a single project file line into a list of tokens.
     *
     * The first token is parsed as an unquoted type identifier.
     * All subsequent tokens must be enclosed in double quotes.
     *
     * Whitespace between tokens is ignored. The method validates
     * proper quotation and throws an exception if the format is invalid.
     *
     * @param line the raw line from the project file
     * @return a list of parsed tokens, where index 0 is the entry type
     *         and subsequent indices are quoted arguments
     *
     * @throws IllegalArgumentException if the line is null, empty,
     *         contains malformed quoted arguments, or does not follow
     *         the expected format
     */
    public static List<String> parseLine(String line) {
        if (line == null) {
            throw new IllegalArgumentException("Line is null");
        }

        line = line.trim();
        if (line.isEmpty()) {
            throw new IllegalArgumentException("Empty line");
        }

        List<String> result = new ArrayList<>();

        int i = 0;
        int n = line.length();

        int start = i;
        while (i < n && !Character.isWhitespace(line.charAt(i))) {
            i++;
        }
        String type = line.substring(start, i);
        result.add(type);

        while (i < n) {
            while (i < n && Character.isWhitespace(line.charAt(i))) {
                i++;
            }
            if (i >= n) {
                break;
            }

            if (line.charAt(i) != '"') {
                throw new IllegalArgumentException(
                        "Expected '\"' at position " + i + ": " + line
                );
            }

            i++;
            start = i;

            while (i < n && line.charAt(i) != '"') {
                i++;
            }

            if (i >= n) {
                throw new IllegalArgumentException(
                        "Unterminated string literal: " + line
                );
            }

            String arg = line.substring(start, i);
            result.add(arg);
            i++;
        }
        return result;
    }
}
