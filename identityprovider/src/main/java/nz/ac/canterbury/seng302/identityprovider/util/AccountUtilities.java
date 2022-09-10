package nz.ac.canterbury.seng302.identityprovider.util;

import nz.ac.canterbury.seng302.shared.util.ValidationError;

import java.util.List;

/**
 * Account utilities class that contains functionality to verify user account fields details.
 */
public class AccountUtilities {

    private static final String FIRST_NAME_ERROR = "firstNameError";
    private static final String LAST_NAME_ERROR = "lastNameError";

    private AccountUtilities(){}

    /**
     * Check the given account username is valid
     * @param username The account username string
     * @param errorBuilder Add to error builder object when error in validation
     * @param result Returned errors list once all validation completed
     */
    public static void validateUsername(String username, ValidationError.Builder errorBuilder, List<ValidationError> result) {
        // Username validation
        if (username.length() < 3 || username.length() > 16 || ValidationUtilities.isUsername(username))  {
            errorBuilder.setFieldName("usernameError");
            errorBuilder.setErrorText("Username must be between 3 and 16 characters with no space or special characters.");
            result.add(errorBuilder.build());
        }
    }

    /**
     * Check the given account firstName is valid
     * @param firstName The account firstName string
     * @param errorBuilder Add to error builder object when error in validation
     * @param result Returned errors list once all validation completed
     */
    public static void validateFirstName(String firstName, ValidationError.Builder errorBuilder, List<ValidationError> result) {
        // First name validation
        if (firstName.isBlank()) {
            errorBuilder.setFieldName(FIRST_NAME_ERROR);
            errorBuilder.setErrorText("First name cannot be blank.");
            result.add(errorBuilder.build());
        } else if (firstName.length() < 2 || firstName.length() > 32) {
            errorBuilder.setFieldName(FIRST_NAME_ERROR);
            errorBuilder.setErrorText("First name must be between 2 and 32 characters.");
            result.add(errorBuilder.build());
        } else if (ValidationUtilities.hasNameSpecial(firstName) || ValidationUtilities.hasDigit(firstName)
                || ValidationUtilities.hasMultipleDashes(firstName) || ValidationUtilities.hasMultipleSpaces(firstName)) {
            errorBuilder.setFieldName(FIRST_NAME_ERROR);
            errorBuilder.setErrorText("First name cannot contain special characters or digits.");
            result.add(errorBuilder.build());
        }
    }

    /**
     * Check the given account lastName is valid
     * @param lastName The account lastName string
     * @param errorBuilder Add to error builder object when error in validation
     * @param result Returned errors list once all validation completed
     */
    public static void validateLastName(String lastName, ValidationError.Builder errorBuilder, List<ValidationError> result) {
        //Last name validation
        if (lastName.isBlank()) {
            errorBuilder.setFieldName(LAST_NAME_ERROR);
            errorBuilder.setErrorText("Last name cannot be blank.");
            result.add(errorBuilder.build());
        } else if (lastName.length() < 2 || lastName.length() > 32) {
            errorBuilder.setFieldName(LAST_NAME_ERROR);
            errorBuilder.setErrorText("Last name must be between 2 and 32 characters.");
            result.add(errorBuilder.build());
        } else if (ValidationUtilities.hasNameSpecial(lastName) || ValidationUtilities.hasDigit(lastName) ||
                ValidationUtilities.hasMultipleDashes(lastName) || ValidationUtilities.hasMultipleSpaces(lastName) ) {
            errorBuilder.setFieldName(LAST_NAME_ERROR);
            errorBuilder.setErrorText("Last name cannot contain special characters or digits.");
            result.add(errorBuilder.build());
        }
    }

    /**
     * Check the given account nickname is valid
     * @param nickname The account nickname string
     * @param errorBuilder Add to error builder object when error in validation
     * @param result Returned errors list once all validation completed
     */
    public static void validateNickname(String nickname, ValidationError.Builder errorBuilder, List<ValidationError> result) {
        //Nickname validation
        if (nickname.length() > 32 || ValidationUtilities.isUsername(nickname)) {
            errorBuilder.setFieldName("nicknameError");
            errorBuilder.setErrorText("Nickname cannot be more than 32 characters.");
            result.add(errorBuilder.build());
        }
    }

    /**
     * Check the given account email is valid
     * @param email The account email string
     * @param errorBuilder Add to error builder object when error in validation
     * @param result Returned errors list once all validation completed
     */
    public static void validateEmail(String email, ValidationError.Builder errorBuilder, List<ValidationError> result) {
        //Email validation
        if (email.isBlank()) {
            errorBuilder.setFieldName("emailError");
            errorBuilder.setErrorText("Email cannot be blank.");
            result.add(errorBuilder.build());
        } else if (!ValidationUtilities.isEmail(email)) {
            errorBuilder.setFieldName("emailError");
            errorBuilder.setErrorText("Email must be in the form username@domainName.domain.");
            result.add(errorBuilder.build());
        }
    }

    /**
     * Check the given account bio is valid
     * @param bio The account bio string
     * @param errorBuilder Add to error builder object when error in validation
     * @param result Returned errors list once all validation completed
     */
    public static void validateBio(String bio, ValidationError.Builder errorBuilder, List<ValidationError> result) {
        //Bio validation
        if (bio.length() > 250) {
            errorBuilder.setFieldName("bioError");
            errorBuilder.setErrorText("Your bio is too long");
            result.add(errorBuilder.build());
        }
    }

    /**
     * Check the given account personal pronouns is valid
     * @param personalPronouns The account personal pronouns string
     * @param errorBuilder Add to error builder object when error in validation
     * @param result Returned errors list once all validation completed
     */
    public static void validatePronoun(String personalPronouns, ValidationError.Builder errorBuilder, List<ValidationError> result) {
        //Personal Pronoun validation
        if (personalPronouns.length() > 32) {
            errorBuilder.setFieldName("personalPronounsError");
            errorBuilder.setErrorText("Personal pronouns must be less than 32 characters.");
            result.add(errorBuilder.build());
        }else if (ValidationUtilities.hasPronounSpecial(personalPronouns) || ValidationUtilities.hasDigit(personalPronouns)) {
            errorBuilder.setFieldName("personalPronounsError");
            errorBuilder.setErrorText("Personal pronouns can only contain special character / and no digits.");
            result.add(errorBuilder.build());
        }
    }

    /**
     * Check the given account password is valid
     * @param password The account password string
     * @param errorBuilder Add to error builder object when error in validation
     * @param result Returned errors list once all validation completed
     */
    public static void validatePassword(String password, ValidationError.Builder errorBuilder, List<ValidationError> result) {
        //Password validation
        if (password.isBlank() || password.length() < 8 || password.length() > 16 || !ValidationUtilities.hasUpper(password) || !ValidationUtilities.hasDigit(password)) {
            errorBuilder.setFieldName("passwordError");
            errorBuilder.setErrorText("Password must contain 8-16 characters, a digit and uppercase character.");
            result.add(errorBuilder.build());
        }
    }
}
