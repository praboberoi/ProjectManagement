package nz.ac.canterbury.seng302.identityprovider.service;

import nz.ac.canterbury.seng302.identityprovider.model.User;
import nz.ac.canterbury.seng302.identityprovider.model.UserRepository;
import nz.ac.canterbury.seng302.identityprovider.util.AccountUtilities;
import nz.ac.canterbury.seng302.identityprovider.util.EncryptionUtilities;
import nz.ac.canterbury.seng302.identityprovider.util.ValidationUtilities;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterRequest;
import nz.ac.canterbury.seng302.shared.util.ValidationError;

import java.util.ArrayList;
import java.util.List;

/**
 * Service class containing functionality to validate user account details upon registration
 */
public class RegistrationService {

    private final UserRepository userRepository;

    public RegistrationService(UserRepository userRepository) {
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

        AccountUtilities.validateUsername(username, errorBuilder, result);

        AccountUtilities.validateFirstName(firstName, errorBuilder, result);

        AccountUtilities.validateLastName(lastName, errorBuilder, result);

        AccountUtilities.validateNickname(nickname, errorBuilder, result);

        AccountUtilities.validateEmail(email, errorBuilder, result);

        AccountUtilities.validateBio(bio, errorBuilder, result);

        AccountUtilities.validatePronoun(personalPronouns, errorBuilder, result);

        AccountUtilities.validatePassword(password, errorBuilder, result);

        return result;
    }

    public User createUser(UserRegisterRequest request) {
        String userSalt = EncryptionUtilities.createSalt();
        String userPassword = EncryptionUtilities.encryptPassword(userSalt, request.getPassword());
        return userRepository.save(new User(request, userPassword, userSalt));
    }
}
