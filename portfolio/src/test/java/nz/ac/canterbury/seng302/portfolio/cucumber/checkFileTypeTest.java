package nz.ac.canterbury.seng302.portfolio.cucumber;


import com.google.protobuf.Timestamp;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.portfolio.controller.AccountController;
import nz.ac.canterbury.seng302.portfolio.model.User;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.EditUserResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class checkFileTypeTest {

    private MockMultipartFile testFile;
    private RedirectAttributes ra;
    private Model mockModel;
    private AuthState principal;
    private final ArrayList<String> acceptedFileTypes = new ArrayList<>(Arrays.asList("jpg", "jpeg", "png"));
    UserResponse.Builder reply = UserResponse.newBuilder();
    User user;

    @Before
    public void startUp() {
        user = new User.Builder()
                .userId(0)
                .username("TimeTester")
                .firstName("Time")
                .lastName("Tester")
                .email("Test@tester.nz")
                .creationDate(new Date())
                .build();

        reply.setUsername(user.getUsername());
        reply.setFirstName(user.getFirstName());
        reply.setLastName(user.getLastName());
        reply.setEmail(user.getEmail());
        reply.setCreated(Timestamp.newBuilder()
                .setSeconds(user.getDateCreated().getTime())
                .build());
    }
    @Given("User selects a {string}")
    public void givenUserSelectsImage(String imageType){
        testFile = new MockMultipartFile("image." + imageType, "image." + imageType, "multipart/form-data", "some image".getBytes());
        ra = Mockito.mock(RedirectAttributes.class);
        mockModel = Mockito.mock(Model.class);
        principal = AuthState.newBuilder().build();
    }

    @When("The file is an accepted type")
    public void whenTheFileIsAnAcceptedType() {
        String extension = testFile.getName().substring(testFile.getName().lastIndexOf(".") + 1);
        assertTrue(acceptedFileTypes.contains(extension));
    }

    @Then("Image is uploaded successfully")
    public void imageIsUploadedSuccessfully() {
        UserAccountClientService mockUserAccountClientService = Mockito.mock(UserAccountClientService.class);
        AccountController controller = new AccountController(mockUserAccountClientService);
        EditUserResponse editUserResponse = EditUserResponse.newBuilder().setIsSuccess(true).build();
        when(mockUserAccountClientService.edit(-1, "", "", "", "", "", "")).thenReturn(editUserResponse);
        when(mockUserAccountClientService.getUser(any())).thenReturn(reply.build());

        try {
            String testString = "";
            assertEquals("redirect:account", controller.editUser(principal, testFile, testString,
                    testString, testString, testString, testString, testString, mockModel, ra));
        } catch (IOException e) {
            assertFalse(false);
        }
    }

    @When("The file is not an accepted type")
    public void the_file_is_not_an_accepted_type() {
        String extension = testFile.getName().substring(testFile.getName().lastIndexOf(".") + 1);
        assertFalse(acceptedFileTypes.contains(extension));
    }
    @Then("Image is not uploaded successfully")
    public void image_is_not_uploaded_successfully() {
        UserAccountClientService mockUserAccountClientService = Mockito.mock(UserAccountClientService.class);
        AccountController controller = new AccountController(mockUserAccountClientService);
        EditUserResponse editUserResponse = EditUserResponse.newBuilder().setIsSuccess(true).build();
        when(mockUserAccountClientService.edit(-1, "", "", "", "", "", "")).thenReturn(editUserResponse);
        when(mockUserAccountClientService.getUser(any())).thenReturn(reply.build());

        try {
            String testString = "";
            assertEquals("redirect:editAccount", controller.editUser(principal, testFile, testString,
                    testString, testString, testString, testString, testString, mockModel, ra));
        } catch (IOException e) {
            assertFalse(false);
        }
    }



}
