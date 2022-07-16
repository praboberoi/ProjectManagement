package nz.ac.canterbury.seng302.identityprovider;

import io.grpc.internal.testing.StreamRecorder;
import nz.ac.canterbury.seng302.identityprovider.model.User;
import nz.ac.canterbury.seng302.identityprovider.model.UserRepository;
import nz.ac.canterbury.seng302.identityprovider.service.UserAccountServerService;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import nz.ac.canterbury.seng302.identityprovider.util.EncryptionUtilities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

/**
 * Unit tests for methods in the UserAccountServerService class
 */
@DataJpaTest
class UserAccountServerServiceUnitTests {

    @MockBean
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
     * Testing that when the UserAccountServerService is given a user role, when addRoleToUser is called
     * the role is added to the list of roles for the user
     */
    @Test
    void givenAUserRole_whenAddRoleToUserIsCalled_ThenRoleIsAddedToListOfUserRolesAndSuccessResponseReceived() {
        ModifyRoleOfUserRequest request = ModifyRoleOfUserRequest.newBuilder().setUserId(1).setRole(UserRole.TEACHER).build();
        User user = new User();
        user.setRoles(Collections.singletonList(UserRole.STUDENT));
        when(userRepository.getUserByUserId(anyInt())).thenReturn(user);
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
        when(userRepository.getUserByUserId(anyInt())).thenReturn(null);
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
        when(userRepository.getUserByUserId(anyInt())).thenReturn(user);

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
        when(userRepository.getUserByUserId(anyInt())).thenReturn(user);
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
        when(userRepository.getUserByUserId(anyInt())).thenReturn(null);
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
        when(userRepository.getUserByUserId(anyInt())).thenReturn(user);
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
        when(userRepository.getUserByUserId(anyInt())).thenReturn(user);
        StreamRecorder<UserRoleChangeResponse> responseObserver = StreamRecorder.create();

        userAccountServerService.removeRoleFromUser(request, responseObserver);
        List<UserRole> expectedRoles = Collections.singletonList(UserRole.STUDENT);

        UserRoleChangeResponse response = responseObserver.getValues().get(0);
        assertEquals(expectedRoles, user.getRoles());
        assertFalse(response.getIsSuccess());
    }

}
