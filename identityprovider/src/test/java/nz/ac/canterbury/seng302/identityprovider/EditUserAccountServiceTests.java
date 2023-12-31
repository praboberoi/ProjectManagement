package nz.ac.canterbury.seng302.identityprovider;

import nz.ac.canterbury.seng302.identityprovider.service.EditUserAccountService;
import nz.ac.canterbury.seng302.shared.identityprovider.EditUserRequest;
import nz.ac.canterbury.seng302.shared.util.ValidationError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for methods in the EditUserAccountService class
 */
public class EditUserAccountServiceTests {
    private EditUserRequest.Builder requestBuilder;
    private static final EditUserAccountService controller = new EditUserAccountService();

    @BeforeEach
    public void init() {
        requestBuilder = EditUserRequest.newBuilder()
                .setUserId(1)
                .setFirstName("Test")
                .setLastName("User")
                .setEmail("TestUser@canterbury.ac.nz")
                .setBio("Test")
                .setNickname("Test");
    };

    /**
     * Tests blue sky data for editing a user
     */
    @Test
    void givenValidUserRequest_whenUserValidation_thenNoValidationErrors() {

        List<ValidationError> result = controller.validateUserDetails(requestBuilder.build());

        assertEquals(0, result.size(), "Valid user's "
                + result.stream().map(ValidationError::getFieldName).collect(Collectors.joining(", "))
                + " are invalid");
    }

    /**
     * Tests user validation with a short First Name
     */
    @Test
    void givenShortFirstName_whenUserValidation_thenNoValidationErrors(){
        requestBuilder.setFirstName("sm");
        List<ValidationError> result = controller.validateUserDetails(requestBuilder.build());

        assertEquals(0, result.size(), "Small First Name not invalid\n"
                + result.stream().map(ValidationError:: getFieldName).collect(Collectors.joining(", ")));
    }

    /**
     * Tests user validation with no values in request
     */
    @Test
    void givenNoValues_whenUserValidation_then3ValidationErrors(){
        List<ValidationError> result = controller.validateUserDetails(EditUserRequest.newBuilder().build());

        assertEquals(3, result.size(), "Incorrect number of validation errors\n"
                + result.stream().map(ValidationError:: getFieldName).collect(Collectors.joining(", ")));
    }

    /**
     *  Tests personal pronoun with special characters and digits
     */
    @Test
    void givenSpecialCharAndDigit_whenPersonalPronounValidation_thenShowValidationError(){
        requestBuilder.setPersonalPronouns("they#0");
        List<ValidationError> result = controller.validateUserDetails(requestBuilder.build());

        assertEquals(1, result.size(), "Special character # and digits not allowed\n"
                + result.stream().map(ValidationError:: getFieldName).collect(Collectors.joining(", ")));
    }

    /**
     *  Tests personal pronoun with allowed special character '/'
     */
    @Test
    void givenSpecialChar_whenPersonalPronounValidation_thenNoValidationError(){
        requestBuilder.setPersonalPronouns("she/her");
        List<ValidationError> result = controller.validateUserDetails(requestBuilder.build());

        assertEquals(0, result.size(), "/ special character is allowed\n"
                + result.stream().map(ValidationError:: getFieldName).collect(Collectors.joining(", ")));
    }

    /**
     * Tests user validation with a multiple invalid entries
     */
    @Test
    void givenInvalidRequest_whenUserValidation_then2ValidationErrors(){
        requestBuilder
                .setLastName("sm")
                .setFirstName("123")
                .setEmail("Not#Valid")
                .setBio("notvalid");
        List<ValidationError> result = controller.validateUserDetails(requestBuilder.build());
        assertEquals(2, result.size(), "Incorrect number of validation errors\n"
                + result.stream().map(ValidationError:: getFieldName).collect(Collectors.joining(", ")));
    }

    /**
     * Tests user validation with a multiple long entries
     */
    @Test
    void givenLongValues_whenUserValidation_thenMultipleValidationErrors(){
        String longVar = "This string should be too long for the attributes to store";
        requestBuilder
                .setFirstName(longVar)
                .setLastName(longVar)
                .setNickname(longVar)
                .setEmail(longVar)
                .setBio(longVar)
                .setPersonalPronouns(longVar);
        List<ValidationError> result = controller.validateUserDetails(requestBuilder.build());

        assertEquals(5, result.size(), "Incorrect number of validation errors\n"
                + result.stream().map(ValidationError:: getFieldName).collect(Collectors.joining(", ")));
    }
}
