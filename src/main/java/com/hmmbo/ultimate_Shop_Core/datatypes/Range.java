package com.hmmbo.ultimate_Shop_Core.datatypes;

import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Range {
    private final int start;
    private final int end;

    public Range(int single) {
        this.start = single;
        this.end = single;
    }

    public Range(int start, int end) {
        this.start = Math.min(start, end);
        this.end = Math.max(start, end);
    }

    public boolean isSingle() {
        return start == end;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    @Override
    public String toString() {
        return isSingle() ? String.valueOf(start) : start + "-" + end;
    }

    // ✅ Generic parser from YAML path (no hardcoding key/attr)
    public static List<Range> parseFromYaml(YamlConfiguration yml, String path) {
        Object value = yml.get(path);
        List<Range> ranges = new ArrayList<>();

        if (value == null) return ranges;

        if (value instanceof Integer) {
            ranges.add(new Range((Integer) value));
        } else if (value instanceof String) {
            ranges.addAll(parseRangeString((String) value));
        } else if (value instanceof List) {
            for (Object item : (List<?>) value) {
                if (item instanceof Integer) {
                    ranges.add(new Range((Integer) item));
                } else if (item instanceof String) {
                    ranges.addAll(parseRangeString((String) item));
                }
            }
        }

        return ranges;
    }

    // ✅ Parser from string: e.g. "1,2-5,7,10-12"
    public static List<Range> parseRangeString(String str) {
        List<Range> result = new ArrayList<>();
        if (str == null || str.trim().isEmpty()) return result;

        str = str.replace("[", "").replace("]", "").trim();
        String[] parts = str.split(",");

        for (String part : parts) {
            part = part.trim().replaceAll("\\s+", "");
            if (part.isEmpty()) continue;

            if (part.matches("-?\\d+")) {
                result.add(new Range(Integer.parseInt(part)));
            } else if (part.matches("-?\\d+-\\d+")) {
                String[] bounds = part.split("-");
                if (bounds.length == 2) {
                    int start = Integer.parseInt(bounds[0]);
                    int end = Integer.parseInt(bounds[1]);
                    result.add(new Range(start, end));
                }
            }
        }

        return result;
    }

    // ✅ Get a random int from a list of ranges
    public static int getRandomFromRanges(List<Range> ranges) {
        if (ranges == null || ranges.isEmpty()) {
            throw new IllegalArgumentException("No ranges provided");
        }

        Random random = new Random();
        int totalSize = 0;
        for (Range range : ranges) {
            totalSize += range.getEnd() - range.getStart() + 1;
        }

        int randomIndex = random.nextInt(totalSize);

        for (Range range : ranges) {
            int size = range.getEnd() - range.getStart() + 1;
            if (randomIndex < size) {
                return range.getStart() + randomIndex;
            }
            randomIndex -= size;
        }

        throw new IllegalStateException("Random index out of bounds");
    }
}
