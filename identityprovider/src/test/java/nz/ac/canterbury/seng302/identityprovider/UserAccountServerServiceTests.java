package nz.ac.canterbury.seng302.identityprovider;

import io.grpc.internal.testing.StreamRecorder;
import nz.ac.canterbury.seng302.identityprovider.model.User;
import nz.ac.canterbury.seng302.identityprovider.model.UserRepository;
import nz.ac.canterbury.seng302.identityprovider.service.UserAccountServerService;
import nz.ac.canterbury.seng302.shared.identityprovider.EditUserRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.EditUserResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Unit tests for methods in the UserAccountServerService class
 */
@SpringBootTest
class UserAccountServerServiceTests {

    private final UserRepository userRepository = Mockito.mock(UserRepository.class);

    private UserAccountServerService userAccountServerService;

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
        user.setPassword(EncryptionUtilities.encryptPassword("", "Password123"));
        user.setSalt("");
        return user;
    }

    @BeforeAll
    void initUserRepository() {
        userRepository.saveAll(Arrays.asList(
            createTestUser(1),
            createTestUser(2),
            createTestUser(3),
            createTestUser(4),
            createTestUser(5)
        ));
    }

    @BeforeEach
    void initUAServerService() {
        userAccountServerService = new UserAccountServerService(userRepository);
    }

    /**
     * Tests blue sky data for registering a user
     * @throws Exception thrown during awaitCompletion method
     */
    @Test
    void givenCorrectUserInfo_whenUserIsSaved_thenSuccessIsReturned() throws Exception {
        UserRegisterRequest request = UserRegisterRequest.newBuilder()
                .setUsername("tu123")
                .setFirstName("Test")
                .setLastName("User")
                .setEmail("TestUser@canterbury.ac.nz")
                .setPassword("Password123")
                .build();
        StreamRecorder<UserRegisterResponse> responseObserver = StreamRecorder.create();
        userAccountServerService.register(request, responseObserver);

        assertNull(responseObserver.getError());
        List<UserRegisterResponse> results = responseObserver.getValues();
        assertEquals(1, results.size());
        UserRegisterResponse response = results.get(0);
        assertTrue(response.getIsSuccess());
    }

    /**
     * Testing that when the editUser function is given a user that cannot be found (has a user id of -1) that the
     * result recorded is that the operation was a failure
     */
    @Test
    void givenFalseUserId_WhenEditUserCalled_ThenResponseRecordedAsFailure()
    {
        EditUserRequest request = EditUserRequest.newBuilder()
                .setUserId(-1).build();
        StreamRecorder<EditUserResponse> responseObserver = StreamRecorder.create();
        userAccountServerService.editUser(request, responseObserver);
        EditUserResponse response = responseObserver.getValues().get(0);
        assertFalse(response.getIsSuccess());
    }

    /**
     * Testing that when the editUser function is given a user that can be found it correctly records the result of
     * the operation
     */
    @Test
    void givenValidUserRequest_WhenEditUserCalled_ThenResponseRecordedAsSuccess()
    {
      EditUserRequest request =
              EditUserRequest.newBuilder().setUserId(1).setBio("Test").setEmail("Test@gmail.com").setLastName("Test").setFirstName("Test").setNickname("Test").setPersonalPronouns("Test").build();
        StreamRecorder<EditUserResponse> responseObserver = StreamRecorder.create();
        userAccountServerService.editUser(request, responseObserver);
        EditUserResponse response = responseObserver.getValues().get(0);
        assertTrue(response.getIsSuccess());
    }

    /**
     * Testing that when the UserAccountServerService is given a valid edit user call, the user is actually edited.
     */
    @Test
    void givenValidEditUserRequest_WhenEditUserCalled_ThenUserIsEdited() {
        EditUserRequest request =
                EditUserRequest.newBuilder().setUserId(1).setBio("Test").setEmail("Test@gmail.com").setLastName("Test").setFirstName("Test").setNickname("Test").setPersonalPronouns("Test").build();
        StreamRecorder<EditUserResponse> responseObserver = StreamRecorder.create();
        User user = userRepository.getUserByUserId(1);
        userAccountServerService.editUser(request, responseObserver);
        assertEquals("Test", user.getFirstName());
        assertEquals("Test", user.getLastName());
        assertEquals("Test", user.getNickname());
        assertEquals("Test", user.getBio());
        assertEquals("Test", user.getPersonalPronouns());
        assertEquals("Test@gmail.com", user.getEmail());
    }

    /**
     * Tests that the correct number of users are returned when no constrainst are
     * specified in the paginated users request
     */
    @Test
    void givenPaginatedUsersRequest_whenNoConstraints_thenAllUsersReturned() {
        GetPaginatedUsersRequest request = GetPaginatedUsersRequest.newBuilder().build();
        StreamRecorder<PaginatedUsersResponse> responseObserver = StreamRecorder.create();

        userAccountServerService.getPaginatedUsers(request, responseObserver);
        PaginatedUsersResponse response = responseObserver.getValues().get(0);
        assertEquals(5, response.getUsersCount());
    }

    /**
     * Tests that the correct number of users are returned when a limit of 3 users is imposed
     * specified in the paginated users request
     */
    @Test
    void givenPaginatedUsersRequest_when3UserLimit_then3UsersReturned() {
        GetPaginatedUsersRequest request = GetPaginatedUsersRequest.newBuilder().setLimit(3).build();
        StreamRecorder<PaginatedUsersResponse> responseObserver = StreamRecorder.create();

        userAccountServerService.getPaginatedUsers(request, responseObserver);
        PaginatedUsersResponse response = responseObserver.getValues().get(0);
        assertEquals(3, response.getUsersCount());
    }

    /**
     * Tests that the correct number of users are returned when there is a limit and page imposed
     * specified in the paginated users request
     */
    @Test
    void givenPaginatedUsersRequest_when3UserLimit_andPage2_then2UsersReturned() {
        GetPaginatedUsersRequest request = GetPaginatedUsersRequest.newBuilder().setOffset(1).setLimit(3).build();
        StreamRecorder<PaginatedUsersResponse> responseObserver = StreamRecorder.create();

        userAccountServerService.getPaginatedUsers(request, responseObserver);
        PaginatedUsersResponse response = responseObserver.getValues().get(0);
        assertEquals(2, response.getUsersCount());
    }

    /**
     * Tests that the correct users are returned when there is a limit and page imposed
     * specified in the paginated users request
     */
    @Test
    void givenPaginatedUsersRequest_when3UserLimit_andPage2_thenCorrectUsersReturned() {
        User testUser = userRepository.getUserByUserId(4);

        GetPaginatedUsersRequest request = GetPaginatedUsersRequest.newBuilder().setOffset(1).setLimit(3).build();
        StreamRecorder<PaginatedUsersResponse> responseObserver = StreamRecorder.create();

        UserResponse testUserResponse = ResponseUtils.prepareUserResponse(testUser);

        userAccountServerService.getPaginatedUsers(request, responseObserver);
        PaginatedUsersResponse response = responseObserver.getValues().get(0);
        assertEquals(testUserResponse, response.getUsersList().get(0));
    }

    /**
     * Tests that the users are returned in the correct order when there is a sort constraint
     * specified in the paginated users request
     */
    @Test
    void givenPaginatedUsersRequest_whenFirstNameSortDesc_thenCorrectUsersReturned() {
        User testUser = userRepository.getUserByUserId(4);

        GetPaginatedUsersRequest request = GetPaginatedUsersRequest.newBuilder().setOrderBy("firstName").setIsAscendingOrder(false).build();
        StreamRecorder<PaginatedUsersResponse> responseObserver = StreamRecorder.create();

        UserResponse testUserResponse = ResponseUtils.prepareUserResponse(testUser);

        userAccountServerService.getPaginatedUsers(request, responseObserver);
        PaginatedUsersResponse response = responseObserver.getValues().get(0);
        assertEquals(testUserResponse, response.getUsersList().get(1));
    }

    /**
     * Tests that the users are returned in the correct order when there is a sort constraint
     * specified in the paginated users request
     */
    @Test
    void givenPaginatedUsersRequest_whenFirstNameSortDesc_and3UserLimit_andPage2__thenCorrectUsersReturned() {
        User testUser = userRepository.getUserByUserId(2);

        GetPaginatedUsersRequest request = GetPaginatedUsersRequest.newBuilder().setOffset(1).setLimit(3).setOrderBy("firstName").setIsAscendingOrder(false).build();
        StreamRecorder<PaginatedUsersResponse> responseObserver = StreamRecorder.create();

        UserResponse testUserResponse = ResponseUtils.prepareUserResponse(testUser);

        userAccountServerService.getPaginatedUsers(request, responseObserver);
        PaginatedUsersResponse response = responseObserver.getValues().get(0);
        assertEquals(testUserResponse, response.getUsersList().get(0));
    }

    /**
     * Testing that when the UserAccountServerService is given a user role, when addRoleToUser is called
     * the role is added to the list of roles for the user
     */
    @Test
    void givenAUserRole_whenAddRoleToUserIsCalled_ThenRoleIsAddedToListOfUserRolesAndSuccessResponseReceived() {
        ModifyRoleOfUserRequest request = ModifyRoleOfUserRequest.newBuilder().setUserId(1).setRole(UserRole.TEACHER).build();
        User user = new User();
        user.setRoles(Collections.singletonList(UserRole.STUDENT));
        when(userRepository.getUserByUserId(any(int.class))).thenReturn(user);
        StreamRecorder<UserRoleChangeResponse> responseObserver = StreamRecorder.create();

        userAccountServerService.addRoleToUser(request, responseObserver);
        List<UserRole> expectedRoles = new ArrayList<>();
        expectedRoles.add(UserRole.STUDENT);
        expectedRoles.add(UserRole.TEACHER);

        UserRoleChangeResponse response = responseObserver.getValues().get(0);
        assertEquals(expectedRoles, user.getRoles());
        assertTrue(response.getIsSuccess());
    }

    /**
     * Testing that when the UserAccountServerService is given a user role,
     * but the user doesn't exist, an error response is returned.
     */
    @Test
    void givenAUserRoleAndAUserThatDoesNotExist_whenAddRoleIsCalled_ThenAnErrorResponseIsReceived() {
        ModifyRoleOfUserRequest request = ModifyRoleOfUserRequest.newBuilder().setUserId(1).setRole(UserRole.TEACHER).build();
        when(userRepository.getUserByUserId(any(int.class))).thenReturn(null);
        StreamRecorder<UserRoleChangeResponse> responseObserver = StreamRecorder.create();

        userAccountServerService.addRoleToUser(request, responseObserver);

        UserRoleChangeResponse response = responseObserver.getValues().get(0);
        assertFalse(response.getIsSuccess());
    }

    /**
     * Testing that when the UserAccountServerService is given a user role,
     * but the user already has that role, an error response is returned.
     */
    @Test
    void givenAUserRoleAndAUserAlreadyWithThatRole_whenAddRoleIsCalled_ThenAnErrorResponseIsReceivedAndRoleIsNotAdded() {
        ModifyRoleOfUserRequest request = ModifyRoleOfUserRequest.newBuilder().setUserId(1).setRole(UserRole.STUDENT).build();

        User user = new User();
        user.setRoles(Collections.singletonList(UserRole.STUDENT));
        when(userRepository.getUserByUserId(any(int.class))).thenReturn(user);

        StreamRecorder<UserRoleChangeResponse> responseObserver = StreamRecorder.create();
        userAccountServerService.addRoleToUser(request, responseObserver);

        UserRoleChangeResponse response = responseObserver.getValues().get(0);
        assertFalse(response.getIsSuccess());
        assertEquals(Collections.singletonList(UserRole.STUDENT), user.getRoles());
    }

    /**
     * Testing that when the UserAccountServerService is given a user role, when removeRoleFromUser is called
     * the role is removed from the list of roles for the user
     */
    @Test
    void givenAUserRole_whenRemoveRoleFromUserIsCalled_ThenRoleIsRemovedFromListOfUserRolesAndSuccessResponseReceived() {
        ModifyRoleOfUserRequest request = ModifyRoleOfUserRequest.newBuilder().setUserId(1).setRole(UserRole.TEACHER).build();
        User user = new User();
        List<UserRole> currentRoles = new ArrayList<>();
        currentRoles.add(UserRole.STUDENT);
        currentRoles.add(UserRole.TEACHER);
        user.setRoles(currentRoles);
        when(userRepository.getUserByUserId(any(int.class))).thenReturn(user);
        StreamRecorder<UserRoleChangeResponse> responseObserver = StreamRecorder.create();

        userAccountServerService.removeRoleFromUser(request, responseObserver);
        List<UserRole> expectedRoles = Collections.singletonList(UserRole.STUDENT);

        UserRoleChangeResponse response = responseObserver.getValues().get(0);
        assertEquals(expectedRoles, user.getRoles());
        assertTrue(response.getIsSuccess());
    }

    /**
     * Testing that when the UserAccountServerService is given a user role,
     * but the user doesn't exist, an error response is returned.
     */
    @Test
    void givenAUserRoleAndAUserThatDoesNotExist_whenRemoveRoleIsCalled_ThenAnErrorResponseIsReceived() {
        ModifyRoleOfUserRequest request = ModifyRoleOfUserRequest.newBuilder().setUserId(1).setRole(UserRole.TEACHER).build();
        when(userRepository.getUserByUserId(any(int.class))).thenReturn(null);
        StreamRecorder<UserRoleChangeResponse> responseObserver = StreamRecorder.create();

        userAccountServerService.removeRoleFromUser(request, responseObserver);

        UserRoleChangeResponse response = responseObserver.getValues().get(0);
        assertFalse(response.getIsSuccess());
    }

    /**
     * Testing that when the UserAccountServerService is given a user role, when removeRoleFromUser is called
     * and the user doesn't have the specified role, the role is not removed from the list of roles for the user and the list remains the same.
     */
    @Test
    void givenAUserRoleAndAUserWithoutThatRole_whenRemoveRoleFromUserIsCalled_ThenAnErrorMessageIsReceivedAndListOfRolesRemainsTheSame() {
        ModifyRoleOfUserRequest request = ModifyRoleOfUserRequest.newBuilder().setUserId(1).setRole(UserRole.TEACHER).build();
        User user = new User();
        List<UserRole> currentRoles = new ArrayList<>();
        currentRoles.add(UserRole.STUDENT);
        user.setRoles(currentRoles);
        when(userRepository.getUserByUserId(any(int.class))).thenReturn(user);
        StreamRecorder<UserRoleChangeResponse> responseObserver = StreamRecorder.create();

        userAccountServerService.removeRoleFromUser(request, responseObserver);
        List<UserRole> expectedRoles = Collections.singletonList(UserRole.STUDENT);

        UserRoleChangeResponse response = responseObserver.getValues().get(0);
        assertEquals(expectedRoles, user.getRoles());
        assertFalse(response.getIsSuccess());
    }

    /**
     * Testing that when the UserAccountServerService is given a user role, when removeRoleFromUser is called
     * and the user only has one role left, the role is not removed from the list of roles for the user and the list remains the same.
     */
    @Test
    void givenAUserRoleAndAUserWithOneRole_whenRemoveRoleIsCalled_ThenAnErrorMessageIsReceivedAndListOfRolesRemainsTheSame() {
        ModifyRoleOfUserRequest request = ModifyRoleOfUserRequest.newBuilder().setUserId(1).setRole(UserRole.STUDENT).build();
        User user = new User();
        List<UserRole> currentRoles = new ArrayList<>();
        currentRoles.add(UserRole.STUDENT);
        user.setRoles(currentRoles);
        when(userRepository.getUserByUserId(any(int.class))).thenReturn(user);
        StreamRecorder<UserRoleChangeResponse> responseObserver = StreamRecorder.create();

        userAccountServerService.removeRoleFromUser(request, responseObserver);
        List<UserRole> expectedRoles = Collections.singletonList(UserRole.STUDENT);

        UserRoleChangeResponse response = responseObserver.getValues().get(0);
        assertEquals(expectedRoles, user.getRoles());
        assertFalse(response.getIsSuccess());
    }
}
