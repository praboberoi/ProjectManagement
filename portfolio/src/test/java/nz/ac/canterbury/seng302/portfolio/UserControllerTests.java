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
import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRoleChangeResponse;

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
        user.setRoles(Arrays.asList(UserRole.STUDENT));
		user.setDateCreated(new Date(System.currentTimeMillis()));
        return user;
    }

    /**
     * Helper function which creates a new user for testing with
     * @param userId The user id to set the user, this affects nearly all of the user's attributes
     * @return A new User object
     */
    UserResponse.Builder createTestUserResponse(int userId) {
        UserResponse.Builder userResponse = UserResponse.newBuilder()
        .setId(userId)
        .setFirstName("First" + userId)
        .setLastName("Last" + userId)
        .setNickname("Nick" + userId)
        .setUsername("User" + userId)
        .setBio("Bio " + userId)
        .setPersonalPronouns("Pronoun " + userId)
        .setEmail("test" + userId + "@gmail.com")
        .addAllRoles(Arrays.asList(UserRole.STUDENT));
        return userResponse;
    }

   /**
    * Test's the getTimePassed function of Account Controller, in this we are testing the blue sky that everything
    * works after 20 days pass
    * @throws Exception Exception thrown during mockMVC run
    */
   @Test
   void givenUserLoggedIn_whenUserListIsRequested_thenUsersAreReturned() throws Exception{
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

   /**
    * Tests that the user will get a successful result when deleting a role
    * @throws Exception Exception thrown during mockMVC run
    */
    @Test
    void givenUserHasRole_whenRemoveRoleCalled_thenRoleDeletedSuccessfully() throws Exception {
        UserResponse user = createTestUserResponse(1).addRoles(UserRole.COURSE_ADMINISTRATOR).build();
        when(userAccountClientService.getUser(null)).thenReturn(user);
        when(userAccountClientService.getUser(1)).thenReturn(user);
        when(userAccountClientService.removeUserRole(1, UserRole.STUDENT)).thenReturn(UserRoleChangeResponse.newBuilder().setIsSuccess(true).build());
        this.mockMvc
            .perform(delete("/usersList/removeRole?userId=1&role=STUDENT"))
            .andExpect(status().isOk())
            .andExpect(content().string("Role deleted successfully"));
   }

   /**
    * Tests that a student user will get an access denied result when deleting a role
    * @throws Exception Exception thrown during mockMVC run
    */
    @Test
    void givenStudentUserAndUserHasRole_whenRemoveRoleCalled_thenPermissionDenied() throws Exception {
        UserResponse user = createTestUserResponse(2).build();
        UserResponse testUser = createTestUserResponse(1).addRoles(UserRole.COURSE_ADMINISTRATOR).build();
        when(userAccountClientService.getUser(null)).thenReturn(user);
        when(userAccountClientService.getUser(1)).thenReturn(testUser);
        when(userAccountClientService.removeUserRole(1, UserRole.STUDENT)).thenReturn(UserRoleChangeResponse.newBuilder().setIsSuccess(true).build());
        this.mockMvc
            .perform(delete("/usersList/removeRole?userId=1&role=STUDENT"))
            .andExpect(status().isForbidden())
            .andExpect(content().string("You do not have these permissions"));
   }

   /**
    * Tests that the user will get an access denied result when deleting a role
    * @throws Exception Exception thrown during mockMVC run
    */
    @Test
    void givenTeacherUserAndUserDoesntHaveRole_whenRemoveRoleCalled_thenPermissionDenied() throws Exception {
        UserResponse user = createTestUserResponse(2).addRoles(UserRole.COURSE_ADMINISTRATOR).build();
        UserResponse testUser = createTestUserResponse(1).addRoles(UserRole.COURSE_ADMINISTRATOR).build();
        when(userAccountClientService.getUser(null)).thenReturn(user);
        when(userAccountClientService.getUser(1)).thenReturn(testUser);
        when(userAccountClientService.removeUserRole(1, UserRole.TEACHER)).thenReturn(UserRoleChangeResponse.newBuilder().setIsSuccess(false).build());
        this.mockMvc
            .perform(delete("/usersList/removeRole?userId=1&role=TEACHER"))
            .andExpect(status().isNotFound())
            .andExpect(content().string("User does not have this role."));
   }

   /**
    * Tests that the user will not be able to delete the last role on a user
    * @throws Exception Exception thrown during mockMVC run
    */
    @Test
    void givenTeacherUserAndUserHasStudentRole_whenRemoveRoleCalled_thenUserMustHaveRole() throws Exception {
        UserResponse user = createTestUserResponse(2).addRoles(UserRole.COURSE_ADMINISTRATOR).build();
        UserResponse testUser = createTestUserResponse(1).build();
        when(userAccountClientService.getUser(null)).thenReturn(user);
        when(userAccountClientService.getUser(1)).thenReturn(testUser);
        when(userAccountClientService.removeUserRole(1, UserRole.STUDENT)).thenReturn(UserRoleChangeResponse.newBuilder().setIsSuccess(false).build());
        this.mockMvc
            .perform(delete("/usersList/removeRole?userId=1&role=STUDENT"))
            .andExpect(status().isMethodNotAllowed())
            .andExpect(content().string("User must have a role."));
   }

   /**
    * Tests that the user will not be able to delete a role of higher permissions
    * @throws Exception Exception thrown during mockMVC run
    */
    @Test
    void givenTeacherUserAndUserHasCourseAdminRole_whenRemoveRoleCalled_thenPermsTooLow() throws Exception {
        UserResponse user = createTestUserResponse(2).addRoles(UserRole.TEACHER).build();
        UserResponse testUser = createTestUserResponse(1).addRoles(UserRole.COURSE_ADMINISTRATOR).build();
        when(userAccountClientService.getUser(null)).thenReturn(user);
        when(userAccountClientService.getUser(1)).thenReturn(testUser);
        when(userAccountClientService.removeUserRole(1, UserRole.COURSE_ADMINISTRATOR)).thenReturn(UserRoleChangeResponse.newBuilder().setIsSuccess(false).build());
        this.mockMvc
            .perform(delete("/usersList/removeRole?userId=1&role=COURSE_ADMINISTRATOR"))
            .andExpect(status().isForbidden())
            .andExpect(content().string("User cannot delete this COURSE_ADMINISTRATOR role"));
   }

   /**
    * Tests that the user cannot delete a role of a user that doesn't exist
    * @throws Exception Exception thrown during mockMVC run
    */
    @Test
    void givenTeacherUser_whenRemoveRoleCalled_thenNoUserError() throws Exception {
        UserResponse user = createTestUserResponse(2).addRoles(UserRole.TEACHER).build();
        when(userAccountClientService.getUser(null)).thenReturn(user);
        when(userAccountClientService.getUser(1)).thenReturn(null);
        this.mockMvc
            .perform(delete("/usersList/removeRole?userId=1&role=STUDENT"))
            .andExpect(status().isNotFound())
            .andExpect(content().string("User cannot be found in database"));
   }

   /**
    * Tests that the user will not be able to add a role of higher permissions
    * @throws Exception Exception thrown during mockMVC run
    */
    @Test
    void givenTeacherUser_whenAddCourseAdminRoleCalled_thenPermsTooLow() throws Exception {
        UserResponse user = createTestUserResponse(2).addRoles(UserRole.TEACHER).build();
        when(userAccountClientService.checkUserIsTeacherOrAdmin(null)).thenReturn(true);
        when(userAccountClientService.getUser(null)).thenReturn(user);
        this.mockMvc
            .perform(post("/user/1/addRole?role=COURSE_ADMINISTRATOR"))
            .andExpect(status().isForbidden())
            .andExpect(content().string("Insufficient Permissions"));
   }

   /**
    * Tests that the user will not be able to add a role as a student
    * @throws Exception Exception thrown during mockMVC run
    */
    @Test
    void givenStudentUser_whenAddStudentRoleCalled_thenPermsTooLow() throws Exception {
        UserResponse user = createTestUserResponse(2).build();
        when(userAccountClientService.getUser(null)).thenReturn(user);
        this.mockMvc
            .perform(post("/user/1/addRole?role=STUDENT"))
            .andExpect(status().isForbidden())
            .andExpect(content().string("Insufficient Permissions"));
   }

   /**
    * Tests that the user will not be able to add an existing role to a user
    * @throws Exception Exception thrown during mockMVC run
    */
    @Test
    void givenAdminUserAndUserWithStudentRole_whenAddStudentRoleCalled_thenConflict() throws Exception {
        UserResponse user = createTestUserResponse(2).addRoles(UserRole.COURSE_ADMINISTRATOR).build();
        when(userAccountClientService.getUser(null)).thenReturn(user);
        when(userAccountClientService.checkUserIsTeacherOrAdmin(null)).thenReturn(true);
        when(userAccountClientService.addRoleToUser(1, UserRole.STUDENT)).thenReturn(
            UserRoleChangeResponse.newBuilder().setIsSuccess(false).setMessage("User already has this role.").build()
        );
        this.mockMvc
            .perform(post("/user/1/addRole?role=STUDENT"))
            .andExpect(status().isConflict())
            .andExpect(content().string("User already has this role."));
   }

   /**
    * Tests that a role will be added to a user in correct conditions
    * @throws Exception Exception thrown during mockMVC run
    */
    @Test
    void givenAdminUserAndUserWithStudentRole_whenAddTeacherRoleCalled_thenRoleAddedSuccessfully() throws Exception {
        UserResponse user = createTestUserResponse(2).addRoles(UserRole.COURSE_ADMINISTRATOR).build();
        when(userAccountClientService.getUser(null)).thenReturn(user);
        when(userAccountClientService.checkUserIsTeacherOrAdmin(null)).thenReturn(true);
        when(userAccountClientService.addRoleToUser(1, UserRole.TEACHER)).thenReturn(
            UserRoleChangeResponse.newBuilder().setIsSuccess(true).build()
        );
        this.mockMvc
            .perform(post("/user/1/addRole?role=TEACHER"))
            .andExpect(status().isOk())
            .andExpect(content().string("Successfully added TEACHER"));
   }

   /**
    * Tests that an error will be thrown if a user doesn't exist
    * @throws Exception Exception thrown during mockMVC run
    */
    @Test
    void givenAdminUser_whenAddTeacherRoleCalled_thenUserNotExist() throws Exception {
        UserResponse user = createTestUserResponse(2).addRoles(UserRole.COURSE_ADMINISTRATOR).build();
        when(userAccountClientService.getUser(null)).thenReturn(user);
        when(userAccountClientService.checkUserIsTeacherOrAdmin(null)).thenReturn(true);
        when(userAccountClientService.addRoleToUser(1, UserRole.TEACHER)).thenReturn(
            UserRoleChangeResponse.newBuilder().setIsSuccess(false).setMessage("User could not be found.").build()
        );
        this.mockMvc
            .perform(post("/user/1/addRole?role=TEACHER"))
            .andExpect(status().isNotFound())
            .andExpect(content().string("User could not be found."));
   }
}
