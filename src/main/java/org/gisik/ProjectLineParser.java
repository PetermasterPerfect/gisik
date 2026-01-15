package org.gisik;

import java.util.ArrayList;
import java.util.List;

public final class ProjectLineParser {

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
