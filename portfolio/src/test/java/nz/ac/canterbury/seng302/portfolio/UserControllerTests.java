package nz.ac.canterbury.seng302.portfolio;

import nz.ac.canterbury.seng302.portfolio.controller.UserController;
import nz.ac.canterbury.seng302.portfolio.model.PersistentSort;
import nz.ac.canterbury.seng302.portfolio.model.PersistentSortRepository;
import nz.ac.canterbury.seng302.portfolio.model.User;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.portfolio.testingUtils.ResponseTestUtils;
import nz.ac.canterbury.seng302.portfolio.utils.PrincipalUtils;
import nz.ac.canterbury.seng302.portfolio.utils.UserField;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.PaginatedUsersResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PersistentSortRepository persistentSortRepository;

	@MockBean
	UserAccountClientService userAccountClientService;

    @Mock
    User user;

    UserResponse.Builder reply;

	/**
     * Helper function which creates a new user for testing with
     * @param userId The user id to set the user, this affects nearly all of the user's attributes
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
        user.setProfileImagePath(null);
        user.setPersonalPronouns("Pronoun " + userId);
        user.setEmail("test" + userId + "@gmail.com");
		user.setDateCreated(new Date(System.currentTimeMillis()));
        return user;
    }

    /**
     * Test's the getTimePassed function of Account Controller, in this we are testing the blue sky that everything
     * works after 20 days pass
     * @throws Exception Expection thrown during checking time
     */
    @Test
    public void givenUserLoggedIn_whenUserListIsRequested_thenUsersAreReturned() throws Exception{
        PersistentSort sort = new PersistentSort(1);

		List<User> users = Arrays.asList(createTestUser(1), createTestUser(2));

		List<UserResponse> preparedUsers = users.stream().map(user -> ResponseTestUtils.prepareUserResponse(user)).collect(Collectors.toList());
        
        PaginatedUsersResponse reply = ResponseTestUtils.preparePaginatedUsersResponse(preparedUsers, 10);
		try (MockedStatic<PrincipalUtils> mocked = mockStatic(PrincipalUtils.class)) {
			when(PrincipalUtils.getUserId(any())).thenReturn(0);
			mocked.when(() -> PrincipalUtils.getUserId(any(AuthState.class))).thenReturn(1);
            when(userAccountClientService.getUser(any())).thenReturn(ResponseTestUtils.prepareUserResponse(createTestUser(0)));
			when(persistentSortRepository.findById(anyInt())).thenReturn(Optional.of(sort));
			when(userAccountClientService.getUsers(anyInt(), anyInt(), any(UserField.class), anyBoolean())).thenReturn(reply);
			this.mockMvc
				.perform(get("/users"))
				.andExpect(status().isOk())
                .andExpect(model().attribute("usersList", users));
		}
    }
}
