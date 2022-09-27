package nz.ac.canterbury.seng302.portfolio.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
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
     * Asserts whether a given string is a valid URL
     * @param urlTest A string to be validated
     * @return True if the given string is a valid URL, False otherwise
     */
    public static boolean isValidHttpUrl(String urlTest) {
        URL url;
        try {
            url = new URL(urlTest);
        } catch (MalformedURLException e) {
            return false;
        }
        return Objects.equals(url.getProtocol(), "http") || Objects.equals(url.getProtocol(), "https");
    }
}
