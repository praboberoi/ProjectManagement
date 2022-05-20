package nz.ac.canterbury.seng302.portfolio;

import nz.ac.canterbury.seng302.portfolio.controller.UserController;
import nz.ac.canterbury.seng302.portfolio.model.User;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.portfolio.testingUtils.ResponseUtils;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
import nz.ac.canterbury.seng302.shared.identityprovider.PaginatedUsersResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserAccountServiceGrpc;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;

import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

// @WebMvcTest(controllers = UserController.class)
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTests {
    @Autowired
    private MockMvc mockMvc;
    
    @Mock
    private UserAccountServiceGrpc.UserAccountServiceBlockingStub userAccountStub;

    private PaginatedUsersResponse response;

    /**
     * Helper function to create a valid AuthState given an ID
     * @param id - The ID of the user specified by this AuthState
     * @return the valid AuthState
     */
    private AuthState createValidAuthStateWithId(String id) {
        return AuthState.newBuilder()
                .setIsAuthenticated(true)
                .setNameClaimType("name")
                .setRoleClaimType("role")
                .addClaims(ClaimDTO.newBuilder().setType("role").setValue("STUDENT").build())
                .addClaims(ClaimDTO.newBuilder().setType("nameid").setValue(id).build())
                .build();
    }

    /**
     * Helper function which creates a new user for testing with
     * @param userId The user id to set the user, this effects nearly all of the user's attributes
     * @return A new User object
     */
    User createTestUser(int userId) {
        User user = new User();
        user.setUserId(userId);
        user.setFirstName("First" + userId);
        user.setLastName("Last" + userId);
        user.setNickname("Nick" + userId);
        user.setUsername("User" + userId);
        user.setBio("Bio " + userId);
        user.setPersonalPronouns("Pronoun " + userId);
        user.setEmail("test" + userId + "@gmail.com");
        user.setDateCreated(new Date());
        return user;
    }

    @BeforeEach
    public void init() {
        UserResponse testUser = UserResponse.newBuilder()
        .setBio("test")
        .setUsername("username")
        .setFirstName("First")
        .setLastName("Last")
        .setNickname("Tester")
        .build();
        response = PaginatedUsersResponse.newBuilder().addUsers(testUser).build();
    }

    /**
     * Tests that a single user is returned when the user list page is requested
     * @throws Exception
     */
    @Test
    void getUsers_whenStudent_thenUsersDisplayed() throws Exception {

        AuthState authState = createValidAuthStateWithId("1");

        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(mockedSecurityContext.getAuthentication()).thenReturn(new PreAuthenticatedAuthenticationToken(authState,""));
        SecurityContextHolder.setContext(mockedSecurityContext);

        Mockito.doReturn(response).when(userAccountStub).getPaginatedUsers(any());
        Mockito.doReturn(ResponseUtils.prepareUserResponse(createTestUser(1))).when(userAccountStub).getUserAccountById(any());

        // UserResponse test1 = userAccountClientService.getUser(authState);


        Object test = mockMvc.perform(get("/users"));
                // .andExpect(model().attribute("userList", "first"));
        System.out.println(test);
    }
}
