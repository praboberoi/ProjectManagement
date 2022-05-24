package nz.ac.canterbury.seng302.identityprovider;

import io.grpc.internal.testing.StreamRecorder;
import nz.ac.canterbury.seng302.identityprovider.model.User;
import nz.ac.canterbury.seng302.identityprovider.model.UserRepository;
import nz.ac.canterbury.seng302.identityprovider.service.UserAccountServerService;
import nz.ac.canterbury.seng302.identityprovider.util.EncryptionUtilities;
import nz.ac.canterbury.seng302.identityprovider.util.ResponseUtils;
import nz.ac.canterbury.seng302.shared.identityprovider.EditUserRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.EditUserResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.GetPaginatedUsersRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.PaginatedUsersResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for methods in the UserAccountServerService class
 */
@DataJpaTest
@TestInstance(Lifecycle.PER_CLASS)
class UserAccountServerServiceTests {

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
        assertEquals(5, response.getResultSetSize());
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
        assertEquals(3, response.getResultSetSize());
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
        assertEquals(2, response.getResultSetSize());
    }

    /**
     * Tests that the correct users are returned when there is a limit and page imposed 
     * specified in the paginated users request
     */
    @Test
    void givenPaginatedUsersRequest_when3UserLimit_andPage2_thenCorrectUsersReturned() {
        User testUser = createTestUser(4);
        userRepository.saveAll(Arrays.asList(
            createTestUser(1),
            createTestUser(2),
            createTestUser(3),
            testUser
        ));

        GetPaginatedUsersRequest request = GetPaginatedUsersRequest.newBuilder().setOffset(1).setLimit(3).build();
        StreamRecorder<PaginatedUsersResponse> responseObserver = StreamRecorder.create();

        UserResponse testUserResponse = ResponseUtils.prepareUserResponse(testUser);

        userAccountServerService.getPaginatedUsers(request, responseObserver);
        PaginatedUsersResponse response = responseObserver.getValues().get(0);
        assertEquals(testUserResponse, response.getUsersList().get(0));
    }
}
