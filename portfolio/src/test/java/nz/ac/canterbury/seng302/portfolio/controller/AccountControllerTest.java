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
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static nz.ac.canterbury.seng302.portfolio.controller.AccountController.formatRoleName;
import static nz.ac.canterbury.seng302.shared.identityprovider.UserRole.STUDENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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

    User user;

    UserResponse.Builder reply;

    private static MockedStatic<PrincipalUtils> utilities;

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

    @BeforeAll
    private static void beforeAllInit() {
        utilities = Mockito.mockStatic(PrincipalUtils.class);
        utilities.when(() -> PrincipalUtils.checkUserIsTeacherOrAdmin(any())).thenReturn(true);
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

        AccountController accountController = new AccountController(mockUserAccountClientService, template);
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
    void GivenExistingUser_WhenEditRequestMade_ThenRedirectAccountReturned() throws Exception {
        UserAccountClientService mockUserAccountClientService = Mockito.mock(UserAccountClientService.class);
        EditUserResponse editUserResponse = EditUserResponse.newBuilder().setIsSuccess(true).build();

        when(userAccountClientService.edit(-1, "", "", "", "", "", "")).thenReturn(editUserResponse);
        when(userAccountClientService.getUser(any())).thenReturn(reply.build());
        when(mockUserAccountClientService.edit(anyInt(), any(), any(), any(), any(), any(), any())).thenReturn(editUserResponse);
        when(mockUserAccountClientService.getUser(any())).thenReturn(reply.build());

        when(PrincipalUtils.getUserId(any())).thenReturn(-1);

        AccountController accountController = new AccountController(mockUserAccountClientService, template);

        AuthState principal = AuthState.newBuilder().build();
        String testString = "";
        MockMultipartFile testFile = new MockMultipartFile("data", "image.png", "image/png", "some image".getBytes());
        Model mockModel = Mockito.mock(Model.class);
        RedirectAttributes ra = Mockito.mock(RedirectAttributes.class);
        assertEquals( "redirect:account", accountController.editUser(principal, testFile,testString,
                testString, testString, testString, testString, testString, false, mockModel, ra ));
    }


    /**
     * Tests that the role fragment returned from the controller contains the right values.
     * @throws Exception An exception can occur.
     */
    @Test
    void GivenUserExists_WhenRolesRequested_ThenRoleFragmentReturned() throws Exception {
        UserAccountClientService mockUserAccountClientService = Mockito.mock(UserAccountClientService.class);
        Calendar cal = new GregorianCalendar();
        cal.add(Calendar.MONTH, -3);
        cal.add(Calendar.YEAR, -2);

        List<UserRole> rolesList = new ArrayList<UserRole>();

        StringBuilder roles = new StringBuilder();



        rolesList.add(STUDENT);

        User user = new User.Builder()
                .userId(0)
                .username("TimeTester")
                .firstName("Time")
                .lastName("Tester")
                .nickname("Testy")
                .email("Test@tester.nz")
                .bio("Testsss")
                .pronouns("Tester")
                .roles(rolesList)
                .creationDate(cal.getTime())
                .build();

        user.getRoles().forEach(role -> roles.append(formatRoleName(role.toString() + ", ")));


        UserResponse.Builder reply = UserResponse.newBuilder();
        reply.setUsername(user.getUsername());
        reply.setFirstName(user.getFirstName());
        reply.setLastName(user.getLastName());
        reply.setEmail(user.getEmail());
        reply.setNickname(user.getNickname());
        reply.setPersonalPronouns(user.getPronouns());
        reply.setId(user.getUserId());
        reply.setCreated(Timestamp.newBuilder()
                .setSeconds(user.getDateCreated().getTime())
                .build());
        reply.addRoles(STUDENT);

        when(userAccountClientService.getUser(any())).thenReturn(reply.build());

        this.mockMvc.perform(get("/account/roles"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("roles", roles.substring(0, roles.length() - 2)));

    }

    @AfterAll
    public static void afterAll() {
        utilities.close();
    }

}
