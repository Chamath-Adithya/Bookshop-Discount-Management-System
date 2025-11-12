package bookshop.util;

// Team Member E: Implement utility methods for input validation.
public final class InputValidator {
    private InputValidator() {
        // Private constructor to prevent instantiation of this utility class.
    }

    /**
     * Checks if the input string represents a positive integer.
     *
     * @param input The string to validate.
     * @return {@code true} if the input is a non-null, non-empty string that parses to an integer greater than 0; {@code false} otherwise.
     */
    public static boolean isQuantityValid(String input) {
        if (!isStringNonEmpty(input)) {
            return false;
        }
        try {
            int quantity = Integer.parseInt(input);
            return quantity > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Checks if the input string is not null and not empty (after trimming whitespace).
     * @param input The string to validate.
     * @return {@code true} if the string has content; {@code false} otherwise.
     */
    public static boolean isStringNonEmpty(String input) {
        return input != null && !input.trim().isEmpty();
    }
}