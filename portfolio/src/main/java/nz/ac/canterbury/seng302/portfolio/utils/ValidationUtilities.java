package nz.ac.canterbury.seng302.portfolio.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A utility class containing common regular expressions that are required.
 */
public class ValidationUtilities {

    private ValidationUtilities() {}

    /**
     * Checks if a string has consecutive " " e.g "   "
     * @param str String to be checked.
     * @return true if str contains consecutive spaces.
     */
    public static boolean hasMultipleSpaces(String str) {
        Pattern pattern = Pattern.compile("(.* {2,}.*)");
        Matcher matcher = pattern.matcher(str);
        return matcher.find();
    }
}
