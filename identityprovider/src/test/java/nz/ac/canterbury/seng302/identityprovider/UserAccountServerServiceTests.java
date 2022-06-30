package nz.ac.canterbury.seng302.identityprovider;

import io.grpc.internal.testing.StreamRecorder;
import nz.ac.canterbury.seng302.identityprovider.model.User;
import nz.ac.canterbury.seng302.identityprovider.model.UserRepository;
import nz.ac.canterbury.seng302.identityprovider.service.UserAccountServerService;
import nz.ac.canterbury.seng302.shared.identityprovider.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Collections;
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
                .setPassword("Paassword123")
                .build();
        StreamRecorder<UserRegisterResponse> responseObserver = StreamRecorder.create();
        when(userRepository.save(any(User.class))).then(returnsFirstArg());
        userAccountServerService.register(request, responseObserver);

        if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
            fail("The call did not terminate in time");
        }

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
        when(userRepository.getUserByUserId(eq(-1))).thenReturn(null);
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
        User user = new User();
        user.setFirstName("Replace");
        user.setLastName("Replace");
        user.setNickname("Replace");
        user.setBio("Replace");
        user.setPersonalPronouns("Replace");
        user.setEmail("test@gmail.com");
        when(userRepository.getUserByUserId(any(int.class))).thenReturn(user);
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
        User user = new User();
        user.setFirstName("Replace");
        user.setLastName("Replace");
        user.setNickname("Replace");
        user.setBio("Replace");
        user.setPersonalPronouns("Replace");
        user.setEmail("Replace@gmail.com");
        when(userRepository.getUserByUserId(any(int.class))).thenReturn(user);
        userAccountServerService.editUser(request, responseObserver);
        assertEquals("Test", user.getFirstName());
        assertEquals("Test", user.getLastName());
        assertEquals("Test", user.getNickname());
        assertEquals("Test", user.getBio());
        assertEquals("Test", user.getPersonalPronouns());
        assertEquals("Test@gmail.com", user.getEmail());
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
    void givenAUserRole_whenAddRoleToAUserThatDoesNotExistIsCalled_ThenAnErrorResponseIsReceived() {
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
    void givenAUserRole_whenAddRoleToAUserThatAlreadyHasThatRole_ThenAnErrorResponseIsReceivedAndRoleIsNotAdded() {
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
    void givenAUserRole_whenRemoveRoleFromAUserThatDoesNotExistIsCalled_ThenAnErrorResponseIsReceived() {
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
    void givenAUserRole_whenRemoveRoleFromUserIsCalled_AndTheUserDoesntHaveTheRole_ThenAnErrorMessageIsReceivedAndListOfRolesRemainsTheSame() {
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
}
