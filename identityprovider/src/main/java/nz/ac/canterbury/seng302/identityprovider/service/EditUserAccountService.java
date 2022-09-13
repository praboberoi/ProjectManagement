package nz.ac.canterbury.seng302.identityprovider.service;

import nz.ac.canterbury.seng302.identityprovider.util.AccountUtilities;
import nz.ac.canterbury.seng302.shared.identityprovider.EditUserRequest;
import nz.ac.canterbury.seng302.shared.util.ValidationError;

import java.util.ArrayList;
import java.util.List;

/**
 * Service class containing functionality to validate user account details upon editing account details
 */
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

        AccountUtilities.validateFirstName(firstName, errorBuilder, result);

        AccountUtilities.validateLastName(lastName, errorBuilder, result);

        AccountUtilities.validateNickname(nickname, errorBuilder, result);

        AccountUtilities.validateEmail(email, errorBuilder, result);

        AccountUtilities.validateBio(bio, errorBuilder, result);

        AccountUtilities.validatePronoun(personalPronouns, errorBuilder, result);

        return result;
    }
}
