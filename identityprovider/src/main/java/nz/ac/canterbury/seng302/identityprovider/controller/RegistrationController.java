package nz.ac.canterbury.seng302.identityprovider.controller;

import nz.ac.canterbury.seng302.identityprovider.model.User;
import nz.ac.canterbury.seng302.identityprovider.model.UserRepository;
import nz.ac.canterbury.seng302.identityprovider.util.EncryptionUtilities;
import nz.ac.canterbury.seng302.identityprovider.util.ValidationUtilities;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterRequest;
import nz.ac.canterbury.seng302.shared.util.ValidationError;

import java.util.ArrayList;
import java.util.List;

public class RegistrationController {

    private final UserRepository userRepository;

    public RegistrationController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    /**
     * Checks each value in the request required for a user to be created is valid
     * @param request The user registration request
     * @return A list of validation errors
     */
    public List<ValidationError> validateUserDetails(UserRegisterRequest request) {
        String username = request.getUsername();
        String firstName = request.getFirstName();
        String lastName = request.getLastName();
        String nickname = request.getNickname();
        String bio = request.getBio();
        String personalPronouns = request.getPersonalPronouns();
        String email = request.getEmail();
        String password = request.getPassword();

        ValidationError.Builder errorBuilder = ValidationError.newBuilder();
        List<ValidationError> result = new ArrayList<>();

        // Username validation
        if (username.length() < 3 || username.length() > 16) {
            errorBuilder.setFieldName("username");
            errorBuilder.setErrorText("Username must be between 3 and 16 characters.");
            result.add(errorBuilder.build());
        }

        // First name validation
        if (firstName.isBlank()) {
            errorBuilder.setFieldName("firstName");
            errorBuilder.setErrorText("First name cannot be blank.");
            result.add(errorBuilder.build());
        } else if (firstName.length() > 32) {
            errorBuilder.setFieldName("firstName");
            errorBuilder.setErrorText("First name cannot be more than 32 characters.");
            result.add(errorBuilder.build());
        } else if (ValidationUtilities.hasSpecial(firstName) || ValidationUtilities.hasDigit(firstName)) {
            errorBuilder.setFieldName("firstName");
            errorBuilder.setErrorText("First name cannot contain special characters or digits.");
            result.add(errorBuilder.build());
        }

        //Last name validation
        if (lastName.isBlank()) {
            errorBuilder.setFieldName("lastName");
            errorBuilder.setErrorText("Last name cannot be blank.");
            result.add(errorBuilder.build());
        } else if (lastName.length() > 32) {
            errorBuilder.setFieldName("lastName");
            errorBuilder.setErrorText("Last name cannot be more than 32 characters.");
            result.add(errorBuilder.build());
        } else if (ValidationUtilities.hasSpecial(lastName) || ValidationUtilities.hasDigit(lastName)) {
            errorBuilder.setFieldName("lastName");
            errorBuilder.setErrorText("Last name cannot contain special characters or digits.");
            result.add(errorBuilder.build());
        }

        //Nickname validation
        if (nickname.length() > 32) {
            errorBuilder.setFieldName("nickname");
            errorBuilder.setErrorText("Nickname cannot be more than 32 characters.");
            result.add(errorBuilder.build());
        }

        //Email validation
        if (email.isBlank()) {
            errorBuilder.setFieldName("email");
            errorBuilder.setErrorText("Email cannot be blank.");
            result.add(errorBuilder.build());
        } else if (!ValidationUtilities.isEmail(email)) {
            errorBuilder.setFieldName("email");
            errorBuilder.setErrorText("Email must be in the form username@domainName.domain.");
            result.add(errorBuilder.build());
        }

        //Bio validation
        if (bio.length() > Integer.MAX_VALUE - 3) {
            errorBuilder.setFieldName("bio");
            errorBuilder.setErrorText("Your bio is too long");
            result.add(errorBuilder.build());
        }

        //Personal Pronoun validation
        if (personalPronouns.length() > 32) {
            errorBuilder.setFieldName("personalPronouns");
            errorBuilder.setErrorText("Personal pronouns cannot be more than 32 characters.");
            result.add(errorBuilder.build());
        }

        //Password validation
        if (password.isBlank() || password.length() < 8 || password.length() > 16 || !ValidationUtilities.hasUpper(password) || !ValidationUtilities.hasDigit(password)) {
            errorBuilder.setFieldName("password");
            errorBuilder.setErrorText("Password must be between 8 and 16 characters and contain a digit and uppercase character.");
            result.add(errorBuilder.build());
        }

        return result;
    }

    public User createUser(UserRegisterRequest request) {
        String userSalt = EncryptionUtilities.createSalt();
        String userPassword = EncryptionUtilities.encryptPassword(userSalt, request.getPassword());
        return userRepository.save(new User(request, userPassword, userSalt));
    }
}
