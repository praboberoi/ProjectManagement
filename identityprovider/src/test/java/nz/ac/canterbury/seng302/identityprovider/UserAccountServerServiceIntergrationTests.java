package nz.ac.canterbury.seng302.identityprovider;

import io.grpc.internal.testing.StreamRecorder;
import nz.ac.canterbury.seng302.identityprovider.model.User;
import nz.ac.canterbury.seng302.identityprovider.model.UserRepository;
import nz.ac.canterbury.seng302.identityprovider.service.UserAccountServerService;
import nz.ac.canterbury.seng302.identityprovider.util.EncryptionUtilities;
import nz.ac.canterbury.seng302.identityprovider.util.ResponseUtils;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for methods in the UserAccountServerService class
 */
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext
class UserAccountServerServiceIntergrationTests {
    
    @Autowired
    private UserRepository userRepository;

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

    @BeforeEach
    void initUAServerService() {
        userAccountServerService = new UserAccountServerService(userRepository);
    }

    /**
     * Tests blue sky data for registering a user
     * @throws Exception thrown during awaitCompletion method
     */
    @Test
    @Transactional
    void testUserCreation() throws Exception {
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
        assertTrue(response.getIsSuccess(), response.getMessage());
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
    @Transactional
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
    @Transactional
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
        assertEquals(44, response.getUsersCount());
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
    void givenPaginatedUsersRequest_when5UserLimit_andLastPage_then4UsersReturned() {
        GetPaginatedUsersRequest request = GetPaginatedUsersRequest.newBuilder().setOffset(8).setLimit(5).build();
        StreamRecorder<PaginatedUsersResponse> responseObserver = StreamRecorder.create();

        userAccountServerService.getPaginatedUsers(request, responseObserver);
        PaginatedUsersResponse response = responseObserver.getValues().get(0);
        assertEquals(4, response.getUsersCount());
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

        UserResponse testUserResponse = ResponseUtils.prepareUserResponse(testUser, null);

        userAccountServerService.getPaginatedUsers(request, responseObserver);
        PaginatedUsersResponse response = responseObserver.getValues().get(0);
        assertEquals(testUserResponse, response.getUsersList().get(0));
    }

    /**
     * Tests that the users are returned in the correct order when there is a sort constraint
     * specified in the paginated users request
     */
    @Test
    void givenPaginatedUsersRequest_whenFirstNameSortAsc_thenCorrectUsersReturned() {
        User testUser = userRepository.getUserByUsername("Alex");

        GetPaginatedUsersRequest request = GetPaginatedUsersRequest.newBuilder().setOrderBy("firstName").setIsAscendingOrder(true).build();
        StreamRecorder<PaginatedUsersResponse> responseObserver = StreamRecorder.create();

        UserResponse testUserResponse = ResponseUtils.prepareUserResponse(testUser, null);

        userAccountServerService.getPaginatedUsers(request, responseObserver);
        PaginatedUsersResponse response = responseObserver.getValues().get(0);
        assertEquals(testUserResponse, response.getUsersList().get(0));
    }

    /**
     * Tests that the users are returned in the correct order when there is a sort constraint
     * specified in the paginated users request
     */
    @Test
    void givenPaginatedUsersRequest_whenFirstNameSortDesc_and3UserLimit_andPage2__thenCorrectUsersReturned() {
        User testUser = userRepository.getUserByUsername("Sebastian");

        GetPaginatedUsersRequest request = GetPaginatedUsersRequest.newBuilder().setOffset(1).setLimit(3).setOrderBy("firstName").setIsAscendingOrder(false).build();
        StreamRecorder<PaginatedUsersResponse> responseObserver = StreamRecorder.create();

        UserResponse testUserResponse = ResponseUtils.prepareUserResponse(testUser, null);

        userAccountServerService.getPaginatedUsers(request, responseObserver);
        PaginatedUsersResponse response = responseObserver.getValues().get(0);
        assertEquals(testUserResponse, response.getUsersList().get(0));
    }
}
