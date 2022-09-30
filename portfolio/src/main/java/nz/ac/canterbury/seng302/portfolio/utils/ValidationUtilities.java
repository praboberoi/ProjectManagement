package nz.ac.canterbury.seng302.portfolio.utils;

import java.util.List;
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

    /**
     * Checks if a string has an emoji in it
     * @param str String to be checked
     * @return true if str has emoji else false
     */
    public static boolean hasEmoji(String str) {
        String emojiRex = "[^\\p{L}\\p{M}\\p{N}\\p{P}\\p{Z}\\p{Cf}\\p{Cs}\\p{Punct}]";
        String expectedStr = String.join("", List.of(str.strip().split(emojiRex)));
        return ! expectedStr.equals(str);
    }
}
