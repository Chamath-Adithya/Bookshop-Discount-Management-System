package bookshop.util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Team Member A: Implement utility methods for file I/O and discount string parsing.
public final class FileHandler {
    private FileHandler() {}

    /**
     * Reads all lines from a CSV file.
     * @param filePath The path to the CSV file.
     * @return A list of strings, each representing a line in the file.
     * @throws IOException If an I/O error occurs.
     */
    public static List<String> readCsv(String filePath) throws IOException {
        Path p = Paths.get(filePath);
        if (!Files.exists(p)) {
            return new ArrayList<>();
        }
        return Files.readAllLines(p, StandardCharsets.UTF_8);
    }

    public static void appendLine(String filePath, String line) throws IOException {
        Path p = Paths.get(filePath);
        if (p.getParent() != null && !Files.exists(p.getParent())) {
            Files.createDirectories(p.getParent());
        }
        try (FileChannel channel = FileChannel.open(p, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
             FileLock lock = channel.lock()) {
            channel.write(ByteBuffer.wrap((line + System.lineSeparator()).getBytes(StandardCharsets.UTF_8)));
        }
    }

    public static void writeCsv(String filePath, List<String> lines) throws IOException {
        Path p = Paths.get(filePath);
        if (p.getParent() != null && !Files.exists(p.getParent())) {
            Files.createDirectories(p.getParent());
        }
        try (FileChannel channel = FileChannel.open(p, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
             FileLock lock = channel.lock()) {
            for (String line : lines) {
                channel.write(ByteBuffer.wrap((line + System.lineSeparator()).getBytes(StandardCharsets.UTF_8)));
            }
        }
    }

    /**
     * Sanitizes a CSV field to prevent CSV injection.
     * Prepends a single quote if the field starts with =, +, -, or @.
     * @param field The field to sanitize.
     * @return The sanitized field.
     */
    public static String escapeCsvField(String field) {
        if (field == null) return "";
        // Prevent CSV Injection
        if (field.startsWith("=") || field.startsWith("+") || field.startsWith("-") || field.startsWith("@")) {
            return "'" + field;
        }
        return field;
    }

    public static Map<Integer, Double> parseDiscountString(String discounts) {
        Map<Integer, Double> discountMap = new HashMap<>();
        if (discounts == null || discounts.trim().isEmpty()) {
            return discountMap;
        }
        String[] rules = discounts.split(";");
        for (String rule : rules) {
            String[] parts = rule.split(":");
            if (parts.length == 2) {
                try {
                    int quantity = Integer.parseInt(parts[0].trim());
                    double price = Double.parseDouble(parts[1].trim());
                    discountMap.put(quantity, price);
                } catch (NumberFormatException e) {
                    System.err.println("Warning: Could not parse discount rule part: " + rule + " - " + e.getMessage());
                }
            }
        }
        return discountMap;
    }

    public static String serializeDiscountMap(Map<Integer, Double> discountRules) {
        if (discountRules == null || discountRules.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Integer, Double> entry : discountRules.entrySet()) {
            sb.append(entry.getKey()).append(":").append(entry.getValue()).append(";");
        }
        // Remove the trailing semicolon if it exists
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }
}
