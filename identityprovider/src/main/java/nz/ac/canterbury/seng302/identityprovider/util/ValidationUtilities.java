package nz.ac.canterbury.seng302.identityprovider.util;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A utility class containing common regular expressions that are required.
 */
public class ValidationUtilities {

    /**
     * Checks if a string has a special character e.g !?@,.
     * @param str String to be checked.
     * @return true if str contains special characters.
     */
    public static boolean hasSpecial(String str) {
        Pattern pattern = Pattern.compile("[^a-zA-Z0-9]");
        Matcher matcher = pattern.matcher(str);
        return matcher.find();
    }

    /**
     * Checks if a string has a special character e.g -
     * @param str String to be checked.
     * @return true if str contains special characters.
     */
    public static boolean hasDashSpecial(String str) {
        Pattern pattern = Pattern.compile("[-[-*]]");
        Matcher matcher = pattern.matcher(str);
        return matcher.find();
    }

    /**
     * Checks if a string has a special character that aren't allowed in names e.g !?@, allowing - and " ".
     * @param str String to be checked.
     * @return true if str contains disallowed characters.
     */
    public static boolean hasNameSpecial(String str) {
        Pattern pattern = Pattern.compile("[^a-zA-Z0-9 '-]");
        Matcher matcher = pattern.matcher(str);
        return matcher.find();
    }

    /**
     * Checks if a string has a special character that aren't allowed in pronouns e.g !?@
     * allows ',',  ' ',  '-'  '+',  '&'.
     * @param str String to be checked.
     * @return true if str contains disallowed characters.
     */
    public static boolean hasPronounSpecial(String str) {
        Pattern pattern = Pattern.compile("[^a-zA-Z0-9 ,+&-]");
        Matcher matcher = pattern.matcher(str);
        return matcher.find();
    }

    /**
     * Checks if a string has a digit (0-9).
     * @param str String to be checked.
     * @return true if str contains a digit.
     */
    public static boolean hasDigit(String str) {
        Pattern pattern = Pattern.compile("[0-9]");
        Matcher matcher = pattern.matcher(str);
        return matcher.find();
    }

    /**
     * Checks if a string has an uppercase character.
     * @param str String to be checked.
     * @return true if str contains a digit.
     */
    public static boolean hasUpper(String str) {
        Pattern pattern = Pattern.compile("[A-Z]");
        Matcher matcher = pattern.matcher(str);
        return matcher.find();
    }

    /**
     * Checks a string to ensure it contains a username, @, domain name, a dot, and a domain.
     * @param str String to be checked
     * @return true if string follows valid email standards
     */
    public static boolean isEmail(String str) {
        return str.matches(
                "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$"
        );
    }

}
