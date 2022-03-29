package nz.ac.canterbury.seng302.identityprovider;

import nz.ac.canterbury.seng302.identityprovider.util.ValidationUtilities;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ValidationUtilitiesTests {

    @Test
    public void givenSpecialCharacters_hasSpecialCharacterReturnsTrue() {
        Assertions.assertTrue(ValidationUtilities.hasSpecial("Test!"));
    }

    @Test
    public void givenLetters_hasSpecialCharacterReturnsFalse() {
        Assertions.assertFalse(ValidationUtilities.hasSpecial("Test"));
    }

    @Test
    public void givenNumbers_hasSpecialCharacterReturnsFalse() {
        Assertions.assertFalse(ValidationUtilities.hasSpecial("Test3"));
    }

    @Test
    public void givenNames_hasSpecialCharacterHyphenReturnsFalse() {
        Assertions.assertFalse(ValidationUtilities.hasNameSpecial("test-name"));
    }

    @Test
    public void givenNames_hasSpecialCharacterSpaceReturnsFalse() {
        Assertions.assertFalse(ValidationUtilities.hasNameSpecial("test name"));
    }

    @Test
    public void givenNames_hasSpecialCharacterApostropheReturnsFalse() {
        Assertions.assertFalse(ValidationUtilities.hasNameSpecial("test'name"));
    }

    @Test
    public void givenSpecialCharacters_hasDigitsReturnsFalse() {
        Assertions.assertFalse(ValidationUtilities.hasDigit("Test!"));
    }

    @Test
    public void givenLetters_hasDigitsReturnsFalse() {
        Assertions.assertFalse(ValidationUtilities.hasDigit("Test"));
    }

    @Test
    public void givenNumbers_hasDigitsReturnsTrue() {
        Assertions.assertTrue(ValidationUtilities.hasDigit("Test3"));
    }

    @Test
    public void givenValidEmail_returnsTrue() {
        Assertions.assertTrue(ValidationUtilities.isEmail("tester@canterbury.ac.nz"));
    }

    @Test
    public void givenInvalidSymbolsEmail_returnsFalse() {
        Assertions.assertFalse(ValidationUtilities.isEmail("testercanterbury.ac.nz"));
    }

    @Test
    public void givenNoDomainEmail_returnsFalse() {
        Assertions.assertFalse(ValidationUtilities.isEmail("tester@canterbury"));
    }
}
