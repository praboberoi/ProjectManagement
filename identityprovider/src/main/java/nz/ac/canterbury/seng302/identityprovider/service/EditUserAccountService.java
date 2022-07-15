package nz.ac.canterbury.seng302.identityprovider.service;

import nz.ac.canterbury.seng302.identityprovider.util.ValidationUtilities;
import nz.ac.canterbury.seng302.shared.identityprovider.EditUserRequest;
import nz.ac.canterbury.seng302.shared.util.ValidationError;

import java.util.ArrayList;
import java.util.List;

public class EditUserAccountService {


    /**
     * Checks each value in the request for a user to be edited is valid
      * @param request the request to edit a user
     * @return A list of validation errors
     */
    public List<ValidationError> validateUserDetails(EditUserRequest request) {
        String firstName = request.getFirstName();
        String lastName = request.getLastName();
        String nickname = request.getNickname();
        String bio = request.getBio();
        String personalPronouns = request.getPersonalPronouns();
        String email = request.getEmail();

        ValidationError.Builder errorBuilder = ValidationError.newBuilder();
        List<ValidationError> result = new ArrayList<>();

        // First name validation
        if (firstName.isBlank()) {
            errorBuilder.setFieldName("firstNameError");
            errorBuilder.setErrorText("First name cannot be blank.");
            result.add(errorBuilder.build());
        } else if (firstName.length() < 2 || firstName.length() > 32) {
            errorBuilder.setFieldName("firstNameError");
            errorBuilder.setErrorText("First name must be between 2 and 32 characters.");
            result.add(errorBuilder.build());
        } else if (ValidationUtilities.hasNameSpecial(firstName) || ValidationUtilities.hasDigit(firstName)
                || !ValidationUtilities.hasDashSpecial(firstName) || !ValidationUtilities.hasSpaceSpecial(firstName)) {
            errorBuilder.setFieldName("firstNameError");
            errorBuilder.setErrorText("First name cannot contain special characters or digits.");
            result.add(errorBuilder.build());
        }

        //Last name validation
        if (lastName.isBlank()) {
            errorBuilder.setFieldName("lastNameError");
            errorBuilder.setErrorText("Last name cannot be blank.");
            result.add(errorBuilder.build());
        } else if (lastName.length() < 2 || lastName.length() > 32) {
            errorBuilder.setFieldName("lastNameError");
            errorBuilder.setErrorText("Last name must be between 2 and 32 characters.");
            result.add(errorBuilder.build());
        } else if (ValidationUtilities.hasNameSpecial(lastName) || ValidationUtilities.hasDigit(lastName) ||
                !ValidationUtilities.hasDashSpecial(lastName) || !ValidationUtilities.hasSpaceSpecial(lastName) ) {
            errorBuilder.setFieldName("lastNameError");
            errorBuilder.setErrorText("Last name cannot contain special characters or digits.");
            result.add(errorBuilder.build());
        }

        //Nickname validation
        if (nickname.length() > 32) {
            errorBuilder.setFieldName("nicknameError");
            errorBuilder.setErrorText("Nickname cannot be more than 32 characters.");
            result.add(errorBuilder.build());
        }

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

        //Bio validation
        if (bio.length() > 250) {
            errorBuilder.setFieldName("bioError");
            errorBuilder.setErrorText("Your bio is too long");
            result.add(errorBuilder.build());
        }

        //Personal Pronoun validation
        if (personalPronouns.length() > 32) {
            errorBuilder.setFieldName("personalPronounsError");
            errorBuilder.setErrorText("Personal pronouns must be less than 32 characters.");
            result.add(errorBuilder.build());
        }else if (ValidationUtilities.hasPronounSpecial(personalPronouns) || ValidationUtilities.hasDigit(personalPronouns)) {
            errorBuilder.setFieldName("personalPronounsError");
            errorBuilder.setErrorText("Personal pronouns can only contain special characters + & - , and no digits.");
            result.add(errorBuilder.build());
        }
        return result;
    };
}
