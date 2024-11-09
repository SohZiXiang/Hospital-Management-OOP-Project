package utils;

/**
 * Utility class for string formatting operations.
 * This class provides methods to format strings in various cases.
 */
public class StringFormatUtil {
    /**
     * Converts a given string to CamelCase.
     * The first character is capitalized, and the remaining characters are converted to lowercase.
     *
     * @param input The string to convert to CamelCase.
     * @return The CamelCase formatted string, or the original string if null or empty.
     */
    public static String toCamelCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

}
