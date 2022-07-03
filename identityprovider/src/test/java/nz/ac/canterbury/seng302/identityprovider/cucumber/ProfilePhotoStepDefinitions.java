package nz.ac.canterbury.seng302.identityprovider.cucumber;

import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import nz.ac.canterbury.seng302.identityprovider.model.User;
import nz.ac.canterbury.seng302.identityprovider.model.UserRepository;
import nz.ac.canterbury.seng302.identityprovider.service.UserProfilePhotoService;
import nz.ac.canterbury.seng302.shared.identityprovider.DeleteUserProfilePhotoResponse;

import static org.mockito.Mockito.when;

import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProfilePhotoStepDefinitions {

    private UserProfilePhotoService userProfilePhotoService;
    
    private static final UserRepository mockUserRepository = Mockito.mock(UserRepository.class);
    
    private User mockUser;
    private User user;
    private DeleteUserProfilePhotoResponse deleteResponse;

    @Before
    public void setup() {
        mockUser = Mockito.mock(User.class);
        when(mockUser.getUserId()).thenReturn(1);
        
        user = new User();
        user.setUserId(1);
        userProfilePhotoService = new UserProfilePhotoService(mockUserRepository);

        when(mockUserRepository.getUserByUserId(1)).thenReturn(user);
    }

    @Given("User exists with profile photo")
    public void user_exists_with_profile_photo() {
        user.setProfileImagePath("testImage1.jpg");
    }

    @Given("User exists with no profile photo")
    public void user_exists_with_no_profile_photo() {
        user.setProfileImagePath(null);
    }

    @When("User deletes their photo")
    public void user_deletes_their_photo() {
        deleteResponse = userProfilePhotoService.deleteUserProfilePhoto(user.getUserId());
    }

    @Then("Photo is deleted")
    public void photo_is_deleted() {
        assertTrue(deleteResponse.getIsSuccess());
    }

    @Then("User has no photo")
    public void user_has_no_photo() {
        assertEquals(null, user.getProfileImagePath());
    }

    @Then("Photo not found error is thrown")
    public void photo_not_found_error_is_thrown() {
        assertFalse(deleteResponse.getIsSuccess());
        assertEquals("Could not find a profile photo to delete", deleteResponse.getMessage());
    }

}
