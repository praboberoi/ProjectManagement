package nz.ac.canterbury.seng302.identityprovider;

import nz.ac.canterbury.seng302.identityprovider.model.UserRepository;
import nz.ac.canterbury.seng302.identityprovider.service.RegistrationService;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterRequest;
import nz.ac.canterbury.seng302.shared.util.ValidationError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for methods in the UserAccountServerService class
 */
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext
class RegistrationServiceTests {

    private UserRegisterRequest.Builder requestBuilder;
    private static final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private static final RegistrationService controller = new RegistrationService(userRepository);

    @BeforeEach
    public void init() {
         requestBuilder = UserRegisterRequest.newBuilder()
                .setUsername("tu123")
                .setFirstName("Test")
                .setLastName("User")
                .setEmail("TestUser@canterbury.ac.nz")
                .setPassword("Password123");
    }

    /**
     * Tests blue sky data for registering a user
     */
    @Test
    void givenValidUserRequest_whenUserValidation_thenNoValidationErrors() {

        List<ValidationError> result = controller.validateUserDetails(requestBuilder.build());

        assertEquals(0, result.size(), "Valid user's "
                + result.stream().map(ValidationError::getFieldName).collect(Collectors.joining(", "))
                + " are invalid");
    }

    /**
     * Tests user validation with a short username
     */
    @Test
    void givenShortUsername_whenUserValidation_thenNoValidationErrors(){
        requestBuilder.setUsername("sm");
        List<ValidationError> result = controller.validateUserDetails(requestBuilder.build());

        assertEquals(1, result.size(), "Small username not invalid\n"
                + result.stream().map(ValidationError:: getFieldName).collect(Collectors.joining(", ")));
    }

    /**
     * Tests user validation with no values in request
     */
    @Test
    void givenNoValues_whenUserValidation_thenNoValidationErrors(){
        List<ValidationError> result = controller.validateUserDetails(UserRegisterRequest.newBuilder().build());

        assertEquals(5, result.size(), "Incorrect number of validation errors\n"
                + result.stream().map(ValidationError:: getFieldName).collect(Collectors.joining(", ")));
    }

    /**
     * Tests user validation with a multiple invalid entries
     */
    @Test
    void givenInvalidRequest_whenUserValidation_thenNoValidationErrors(){
        requestBuilder
                .setUsername("sm")
                .setFirstName("123")
                .setEmail("Not#Valid")
                .setPassword("notvalid");
        List<ValidationError> result = controller.validateUserDetails(requestBuilder.build());

        assertEquals(4, result.size(), "Incorrect number of validation errors\n"
                + result.stream().map(ValidationError:: getFieldName).collect(Collectors.joining(", ")));
    }

    /**
     * Tests user validation with a multiple long entries
     */
    @Test
    void givenLongValues_whenUserValidation_thenMultipleValidationErrors(){
        String longVar = "This string should be too long for the attributes to store";
        requestBuilder
                .setUsername(longVar)
                .setFirstName(longVar)
                .setLastName(longVar)
                .setNickname(longVar)
                .setEmail(longVar)
                .setBio(longVar)
                .setPersonalPronouns(longVar)
                .setPassword("Th1s Password should be too long");
        List<ValidationError> result = controller.validateUserDetails(requestBuilder.build());

        assertEquals(7, result.size(), "Incorrect number of validation errors\n"
                + result.stream().map(ValidationError:: getFieldName).collect(Collectors.joining(", ")));
    }

    /**
     * Tests a user can have spaces in their first and last names.
     */
    @Test
    void givenValidUserRequestWithSpaceInNames_whenUserValidation_thenNoValidationErrors() {
        requestBuilder.setFirstName("test first name");
        requestBuilder.setLastName("test last name");
        List<ValidationError> result = controller.validateUserDetails(requestBuilder.build());

        assertEquals(0, result.size(), "Valid user's "
                + result.stream().map(ValidationError::getFieldName).collect(Collectors.joining(", "))
                + " are invalid");
    }

    /**
     * Tests that a user can have hyphens '-' in their first and last names.
     */
    @Test
    void givenValidUserRequestWithHyphensInNames_whenUserValidation_thenNoValidationErrors() {
        requestBuilder.setFirstName("test-first-name");
        requestBuilder.setLastName("test-last-name");
        List<ValidationError> result = controller.validateUserDetails(requestBuilder.build());

        assertEquals(0, result.size(), "Valid user's "
                + result.stream().map(ValidationError::getFieldName).collect(Collectors.joining(", "))
                + " are invalid");
    }

    /**
     * Tests that a bio with too long a description will not be valid
     */
    @Test
    public void givenInvalidBio_whenUserValidated_thenFailsValidation() {
        requestBuilder.setBio("0123456789".repeat(26)); //260 characters
        List<ValidationError> result = controller.validateUserDetails(requestBuilder.build());
        assertEquals(1, result.size(), "Valid user's "
        + result.stream().map(ValidationError::getFieldName).collect(Collectors.joining(", "))
        + " are invalid");
    }

    /**
     * Tests that a bio with the maximum character count will be valid
     */
    @Test
    public void givenValidBio_whenUserValidated_thenSucceedsValidation() {
        requestBuilder.setBio("0123456789".repeat(25)); //250 characters
        List<ValidationError> result = controller.validateUserDetails(requestBuilder.build());
        assertEquals(0, result.size(), "Valid user's "
        + result.stream().map(ValidationError::getFieldName).collect(Collectors.joining(", "))
        + " are invalid");
    }
}
