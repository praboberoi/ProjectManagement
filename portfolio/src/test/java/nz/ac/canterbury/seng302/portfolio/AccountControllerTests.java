package nz.ac.canterbury.seng302.portfolio;

import com.google.protobuf.Timestamp;
import nz.ac.canterbury.seng302.portfolio.controller.AccountController;
import nz.ac.canterbury.seng302.portfolio.model.User;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.EditUserResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.Model;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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

    @Test
    public void GivenNonExistentUser_WhenEditRequestMade_ThenEditAccountReturned()
    {
        UserAccountClientService mockUserAccountClientService = Mockito.mock(UserAccountClientService.class);
        EditUserResponse editUserResponse = EditUserResponse.newBuilder().setIsSuccess(false).build();
        when(mockUserAccountClientService.edit(-1, "", "", "", "", "",
                "")).thenReturn(editUserResponse);
        AccountController accountController = new AccountController(mockUserAccountClientService);
        AuthState principal = AuthState.newBuilder().build();
        String testString = "";
        Model mockModel = Mockito.mock(Model.class);
        assertEquals( "editAccount", accountController.editUser(principal, testString,
                testString, testString, testString, testString, testString, mockModel));

    }

    @Test
    public void GivenExistingUser_WhenEditRequestMade_ThenRedirectAccountReturned()
    {
        UserAccountClientService mockUserAccountClientService = Mockito.mock(UserAccountClientService.class);
        EditUserResponse editUserResponse = EditUserResponse.newBuilder().setIsSuccess(true).build();
        when(mockUserAccountClientService.edit(-1, "", "", "", "", "",
                "")).thenReturn(editUserResponse);
        AccountController accountController = new AccountController(mockUserAccountClientService);
        AuthState principal = AuthState.newBuilder().build();
        String testString = "";
        Model mockModel = Mockito.mock(Model.class);
        assertEquals( "redirect:account", accountController.editUser(principal, testString,
                testString, testString, testString, testString, testString, mockModel));

    }
}
