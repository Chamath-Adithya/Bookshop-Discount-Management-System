package bookshop.util;

import java.util.HashMap;
import java.util.Map;

// Team Member A: Implement utility methods for file I/O and discount string parsing.
public final class FileHandler {
    private FileHandler() {}

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