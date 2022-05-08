package nz.ac.canterbury.seng302.portfolio;

import com.google.protobuf.Timestamp;
import nz.ac.canterbury.seng302.portfolio.controller.AccountController;
import nz.ac.canterbury.seng302.portfolio.model.User;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.EditUserResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


@WebMvcTest(controllers = AccountController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AccountControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserAccountClientService userAccountClientService;

    User user;

    UserResponse.Builder reply;


    @BeforeEach
    public void init() {
        user = new User.Builder()
        .userId(0)
        .username("TimeTester")
        .firstName("Time")
        .lastName("Tester")
        .email("Test@tester.nz")
        .creationDate(new Date())
        .build();

        reply = UserResponse.newBuilder();
        reply.setUsername(user.getUsername());
        reply.setFirstName(user.getFirstName());
        reply.setLastName(user.getLastName());
        reply.setEmail(user.getEmail());
        reply.setCreated(Timestamp.newBuilder()
                .setSeconds(user.getDateCreated().getTime())
                .build());
    }

    /**
     * Test's the getTimePassed function of Account Controller, in this we are testing the blue sky that everything
     * works after 20 days pass
     * @throws Exception Expection thrown during checking time
     */
    @Test
    public void getTimePassed_20Days() throws Exception{
        Calendar cal = new GregorianCalendar();
        cal.add(Calendar.DAY_OF_MONTH, -7);

        User user = new User.Builder()
                .userId(0)
                .username("TimeTester")
                .firstName("Time")
                .lastName("Tester")
                .email("Test@tester.nz")
                .creationDate(cal.getTime())
                .build();

        UserResponse.Builder reply = UserResponse.newBuilder();
        reply.setUsername(user.getUsername());
        reply.setFirstName(user.getFirstName());
        reply.setLastName(user.getLastName());
        reply.setEmail(user.getEmail());
        reply.setCreated(Timestamp.newBuilder()
                .setSeconds(user.getDateCreated().getTime())
                .build());

        when(userAccountClientService.getUser(any())).thenReturn(reply.build());

        this.mockMvc
                .perform(get("/account"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("timePassed", "(7 Days)"));
    }

    /**
     * Test of getTimPassed method as if one month had passed
     * @throws Exception That may occur during checking of time
     */
    @Test
    public void getTimePassed_1Month() throws Exception{
        Calendar cal = new GregorianCalendar();
        cal.add(Calendar.MONTH, -1);

        User user = new User.Builder()
                .userId(0)
                .username("TimeTester")
                .firstName("Time")
                .lastName("Tester")
                .email("Test@tester.nz")
                .creationDate(cal.getTime())
                .build();

        UserResponse.Builder reply = UserResponse.newBuilder();
        reply.setUsername(user.getUsername());
        reply.setFirstName(user.getFirstName());
        reply.setLastName(user.getLastName());
        reply.setEmail(user.getEmail());
        reply.setCreated(Timestamp.newBuilder()
                .setSeconds(user.getDateCreated().getTime())
                .build());

        when(userAccountClientService.getUser(any())).thenReturn(reply.build());

        this.mockMvc
                .perform(get("/account"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("timePassed", "(1 Month)"));

    }

    /**
     * Testing the getTimePassedSince method of account controller, after 2 years 3 months have passed
     * @throws Exception An exception can occur during the parsing and checking of time
     */
    @Test
    public void getTimePassed_2Year3Month() throws Exception{
        Calendar cal = new GregorianCalendar();
        cal.add(Calendar.MONTH, -3);
        cal.add(Calendar.YEAR, -2);

        User user = new User.Builder()
                .userId(0)
                .username("TimeTester")
                .firstName("Time")
                .lastName("Tester")
                .email("Test@tester.nz")
                .creationDate(cal.getTime())
                .build();

        UserResponse.Builder reply = UserResponse.newBuilder();
        reply.setUsername(user.getUsername());
        reply.setFirstName(user.getFirstName());
        reply.setLastName(user.getLastName());
        reply.setEmail(user.getEmail());
        reply.setCreated(Timestamp.newBuilder()
                .setSeconds(user.getDateCreated().getTime())
                .build());

        when(userAccountClientService.getUser(any())).thenReturn(reply.build());

        this.mockMvc
                .perform(get("/account"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("timePassed", "(2 Years, 3 Months)"));

    }

    /**
     * Testing that when the account controller is asked to make an edit request for a non-existent user, and it
     * receives the correct response that it redirects the Html to the right page
     */
    @Test
    public void GivenNonExistentUser_WhenEditRequestMade_ThenEditAccountReturned() throws IOException {
        UserAccountClientService mockUserAccountClientService = Mockito.mock(UserAccountClientService.class);
        EditUserResponse editUserResponse = EditUserResponse.newBuilder().setIsSuccess(false).build();
        when(mockUserAccountClientService.edit(-1, "", "", "", "", "", "")).thenReturn(editUserResponse);
        when(mockUserAccountClientService.getUser(any())).thenReturn(reply.build());

        AccountController accountController = new AccountController(mockUserAccountClientService);
        AuthState principal = AuthState.newBuilder().build();
        String testString = "";

        MockMultipartFile file0 = new MockMultipartFile("file", "image.png", "image", "image.png".getBytes(StandardCharsets.UTF_8));
        Model mockModel = Mockito.mock(Model.class);
        RedirectAttributes ra = Mockito.mock(RedirectAttributes.class);
        assertEquals( "editAccount", accountController.editUser(principal, file0, testString,
                testString, testString, testString, testString, testString, mockModel, ra));

    }

    /**
     * Testing that when the account controller is asked to edit an existing user and the operation is a success it
     * redirects back to the account page.
     */
    @Test
    public void GivenExistingUser_WhenEditRequestMade_ThenRedirectAccountReturned() throws IOException {
        UserAccountClientService mockUserAccountClientService = Mockito.mock(UserAccountClientService.class);
        EditUserResponse editUserResponse = EditUserResponse.newBuilder().setIsSuccess(true).build();
        when(mockUserAccountClientService.edit(-1, "", "", "", "", "", "")).thenReturn(editUserResponse);
        when(mockUserAccountClientService.getUser(any())).thenReturn(reply.build());
        AccountController accountController = new AccountController(mockUserAccountClientService);
        AuthState principal = AuthState.newBuilder().build();
        String testString = "";
        MockMultipartFile testFile = new MockMultipartFile("data", "image.png", "file", "some image".getBytes());
        MockMultipartFile test = null;
        Model mockModel = Mockito.mock(Model.class);
        RedirectAttributes ra = Mockito.mock(RedirectAttributes.class);
        assertEquals( "redirect:account", accountController.editUser(principal, null,testString,
                testString, testString, testString, testString, testString, mockModel, ra ));

    }
}
