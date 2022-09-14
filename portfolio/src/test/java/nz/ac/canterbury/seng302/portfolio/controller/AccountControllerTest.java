package nz.ac.canterbury.seng302.portfolio.controller;

import com.google.protobuf.Timestamp;
import nz.ac.canterbury.seng302.portfolio.model.User;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.portfolio.utils.ControllerAdvisor;
import nz.ac.canterbury.seng302.portfolio.utils.PrincipalUtils;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.EditUserResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.core.AbstractMessageReceivingTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static nz.ac.canterbury.seng302.shared.identityprovider.UserRole.STUDENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = AccountController.class)
@AutoConfigureMockMvc(addFilters = false)
class AccountControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserAccountClientService userAccountClientService;

    @InjectMocks
    private ControllerAdvisor controllerAdvisor;

    @MockBean
    private SimpMessagingTemplate template;


    @Autowired
    private AccountController testAccountController;

    User user;

    UserResponse.Builder reply;

    private static MockedStatic<PrincipalUtils> utilities;

    @BeforeAll
    private static void beforeAllInit() {
        utilities = Mockito.mockStatic(PrincipalUtils.class);
        utilities.when(() -> PrincipalUtils.checkUserIsTeacherOrAdmin(any())).thenReturn(true);
        when(PrincipalUtils.getUserId(any(AuthState.class))).thenReturn(-1);
    }

    @AfterAll
    public static void close() {
        utilities.close();
    }


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
    void getTimePassed_20Days() throws Exception{
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
    void getTimePassed_1Month() throws Exception{
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
    void getTimePassed_2Year3Month() throws Exception{
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
    void GivenNonExistentUser_WhenEditRequestMade_ThenEditAccountReturned() throws IOException {
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
                testString, testString, testString, testString, testString, false, mockModel, ra));

    }

    /**
     * Testing that when the account controller is asked to edit an existing user and the operation is a success it
     * redirects back to the account page.
     */
    @Test
    void GivenExistingUser_WhenEditRequestMade_ThenRedirectAccountReturned() throws IOException {
        EditUserResponse editUserResponse = EditUserResponse.newBuilder().setIsSuccess(true).build();

        when(userAccountClientService.edit(-1, "", "", "", "", "", "")).thenReturn(editUserResponse);
        UserResponse mockReply = Mockito.mock(UserResponse.class);
        when(mockReply.getId()).thenReturn(-1);
        when(mockReply.getUsername()).thenReturn("");
        when(mockReply.getFirstName()).thenReturn("");
        when(mockReply.getLastName()).thenReturn("");
        when(mockReply.getNickname()).thenReturn("");
        when(mockReply.getEmail()).thenReturn("");
        when(mockReply.getBio()).thenReturn("");
        when(mockReply.getPersonalPronouns()).thenReturn("");
        when(mockReply.getProfileImagePath()).thenReturn("");
        Timestamp mockTimeStamp = Mockito.mock(Timestamp.class);
        when(mockTimeStamp.getSeconds()).thenReturn(12L);
        when(mockReply.getCreated()).thenReturn(mockTimeStamp);
        List <UserRole> roleList = new ArrayList<UserRole>();
        roleList.add(STUDENT);
        when(mockReply.getRolesList()).thenReturn(roleList);
        when(userAccountClientService.getUser(any())).thenReturn(mockReply);



        AuthState principal = AuthState.newBuilder().build();


        String testString = "";
        MockMultipartFile testFile = new MockMultipartFile("data", "image.png", "image/png", "some image".getBytes());
        Model mockModel = Mockito.mock(Model.class);
        RedirectAttributes ra = Mockito.mock(RedirectAttributes.class);
        assertEquals( "redirect:account", testAccountController.editUser(principal, testFile,testString,
                testString, testString, testString, testString, testString, false, mockModel, ra ));
    }
}
