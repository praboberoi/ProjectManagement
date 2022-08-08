package nz.ac.canterbury.seng302.identityprovider;

import io.grpc.internal.testing.StreamRecorder;
import nz.ac.canterbury.seng302.identityprovider.model.GroupsRepository;
import nz.ac.canterbury.seng302.identityprovider.model.UserRepository;
import nz.ac.canterbury.seng302.identityprovider.service.GroupServerService;
import nz.ac.canterbury.seng302.shared.identityprovider.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import com.google.protobuf.Empty;

import java.util.Arrays;
import java.util.List;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;
/**
 * Unit tests for methods in the GroupServerService class
 */
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext
class GroupServerServiceIntegrationTests {

    @Autowired
    private GroupsRepository groupsRepository;

    @Autowired
    private UserRepository userRepository;

    private GroupServerService groupServerService;

    @BeforeEach
    void initServerService() {
        groupServerService = new GroupServerService(groupsRepository, userRepository);
    }

    /**
     * Tests whether the method getMembersWithoutAGroup returns the correct number of users
     */
    @Test
    void givenSampleData_whenUsersWithNoGroupsCalled_thenCorrectUsersReturned() {
        Empty request = Empty.getDefaultInstance();
        StreamRecorder<GroupDetailsResponse> responseObserver = StreamRecorder.create();
        groupServerService.getMembersWithoutAGroup(request, responseObserver);

        assertNull(responseObserver.getError());
        List<GroupDetailsResponse> results = responseObserver.getValues();
        assertEquals(1, results.size());
        GroupDetailsResponse response = results.get(0);
        assertEquals(29, response.getMembersCount(), "Incorrect number of users returned in 'no group' group");
    }

    /**
     * Tests whether the method getTeachingStaffGroup returns the correct number of users
     */
    @Test
    void givenSampleData_whenTeachingStaffGroupCalled_thenCorrectUsersReturned() {
        Empty request = Empty.getDefaultInstance();
        StreamRecorder<GroupDetailsResponse> responseObserver = StreamRecorder.create();
        groupServerService.getTeachingStaffGroup(request, responseObserver);

        assertNull(responseObserver.getError());
        List<GroupDetailsResponse> results = responseObserver.getValues();
        assertEquals(1, results.size());
        GroupDetailsResponse response = results.get(0);
        assertEquals(3, response.getMembersCount(), "Incorrect number of users returned in 'teacher' group");
    }

    /**
     * Tests whether the method deleteGroup deletes the group
     */
    @Test
    @Transactional
    void givenSampleData_whenDeleteGroupCalled_thenGroupCannotBeFound() {
        DeleteGroupRequest request = DeleteGroupRequest.newBuilder().setGroupId(1).build();
        StreamRecorder<DeleteGroupResponse> responseObserver = StreamRecorder.create();
        groupServerService.deleteGroup(request, responseObserver);

        assertNull(responseObserver.getError());
        List<DeleteGroupResponse> results = responseObserver.getValues();
        assertEquals(1, results.size());
        DeleteGroupResponse response = results.get(0);
        assertTrue(response.getIsSuccess(), "Group was not deleted successfully: " + response.getMessage());
        assertFalse(groupsRepository.findById(1).isPresent());
    }

    /**
     * Tests if users are deleted when a group is deleted
     */
    @Test
    @Transactional
    void givenSampleData_whenDeleteGroupCalled_thenUsersStillExist() {
        DeleteGroupRequest request = DeleteGroupRequest.newBuilder().setGroupId(1).build();
        StreamRecorder<DeleteGroupResponse> responseObserver = StreamRecorder.create();
        groupServerService.deleteGroup(request, responseObserver);

        assertNull(responseObserver.getError());
        List<DeleteGroupResponse> results = responseObserver.getValues();
        assertEquals(1, results.size());

        assertNotNull(userRepository.getUserByUserId(1));
    }

    /**
     * Tests if users are deleted when a group is deleted
     */
    @Test
    @Transactional
    void givenSampleData_whenDeleteGroupCalledOnNoGroup_thenUsersStillExist() {
        DeleteGroupRequest request = DeleteGroupRequest.newBuilder().build();
        StreamRecorder<DeleteGroupResponse> responseObserver = StreamRecorder.create();
        groupServerService.deleteGroup(request, responseObserver);

        assertNull(responseObserver.getError());
        List<DeleteGroupResponse> results = responseObserver.getValues();
        assertEquals(1, results.size());

        DeleteGroupResponse response = results.get(0);
        assertFalse(response.getIsSuccess(), "Group was incorrectly deleted: " + response.getMessage());
    }

    /**
     * Tests that all the groups currently stored in the idp database are returned when a paginated group request is made.
     */
    @Test
    void givenSampleData_whenGetPaginatedGroupsIsCalledWithNoParameters_thenAllGroupsAreReturned() {
        GetPaginatedGroupsRequest request = GetPaginatedGroupsRequest.newBuilder().build();
        StreamRecorder<PaginatedGroupsResponse> responseObserver = StreamRecorder.create();
        groupServerService.getPaginatedGroups(request, responseObserver);

        assertNull(responseObserver.getError());
        List<PaginatedGroupsResponse> results = responseObserver.getValues();

        assertEquals(1, results.size());
        PaginatedGroupsResponse response = results.get(0);
        assertEquals(6, response.getResultSetSize(),  "Incorrect number of groups received.");
        GroupDetailsResponse testGroup = response.getGroups(0);
        assertEquals(1, testGroup.getGroupId());
    }

    /**
     * Tests that all the correct group info is returned to the client.
     */
    @Test
    void givenSampleData_whengetGroupDetailsIsCalled_thenCorrect() {
        GetGroupDetailsRequest request = GetGroupDetailsRequest.newBuilder().setGroupId(1).build();
        StreamRecorder<GroupDetailsResponse> responseObserver = StreamRecorder.create();
        groupServerService.getGroupDetails(request, responseObserver);

        assertNull(responseObserver.getError());
        List<GroupDetailsResponse> results = responseObserver.getValues();

        assertEquals(1, results.size());
        GroupDetailsResponse response = results.get(0);
        assertEquals(1, response.getGroupId());
        assertEquals("Team 400", response.getShortName());
        assertEquals("400: Bad Request", response.getLongName());
    }

    /**
     * Tests that all members are removed from the specified group
     */
    @Test
    @Transactional
    void givenSampleData_whenRemoveGroupMembersIsCalledOnNormalGroup_thenMemberRemoved() {
        RemoveGroupMembersRequest request = RemoveGroupMembersRequest.newBuilder().setGroupId(1).addAllUserIds(Arrays.asList(1)).build();
        StreamRecorder<RemoveGroupMembersResponse> responseObserver = StreamRecorder.create();
        groupServerService.removeGroupMembers(request, responseObserver);

        assertNull(responseObserver.getError());
        List<RemoveGroupMembersResponse> results = responseObserver.getValues();

        assertEquals(1, results.size());
        RemoveGroupMembersResponse response = results.get(0);
        assertTrue(response.getIsSuccess(), "Member failed to delete from the group: " + response.getMessage());
        assertEquals(0, groupsRepository.findById(1).get().getUsers().stream().filter(user -> user.getUserId() == 1).count());
    }

    /**
     * Tests that all members are removed from the teacher group
     */
    @Test
    @Transactional
    void givenSampleData_whenRemoveGroupMembersIsCalledOnTeacherGroup_thenMemberIsNotTeacher() {
        RemoveGroupMembersRequest request = RemoveGroupMembersRequest.newBuilder().setGroupId(-1).addAllUserIds(Arrays.asList(2)).build();
        StreamRecorder<RemoveGroupMembersResponse> responseObserver = StreamRecorder.create();
        groupServerService.removeGroupMembers(request, responseObserver);

        assertNull(responseObserver.getError());
        List<RemoveGroupMembersResponse> results = responseObserver.getValues();

        assertEquals(1, results.size());
        RemoveGroupMembersResponse response = results.get(0);
        assertTrue(response.getIsSuccess(), "Member failed to delete from the group: " + response.getMessage());
        assertFalse(userRepository.findById(2).get().getRoles().contains(UserRole.TEACHER), "User is still a teacher");
    }

    /**
     * Tests a teacher is not removed when they only have 1 role
     */
    @Test
    @Transactional
    void givenUserWithSingleRole_whenRemoveGroupMembersIsCalledOnTeacherGroup_thenMemberIsNotAffected() {
        RemoveGroupMembersRequest request = RemoveGroupMembersRequest.newBuilder().setGroupId(-1).addAllUserIds(Arrays.asList(1)).build();
        StreamRecorder<RemoveGroupMembersResponse> responseObserver = StreamRecorder.create();
        groupServerService.removeGroupMembers(request, responseObserver);

        assertNull(responseObserver.getError());
        List<RemoveGroupMembersResponse> results = responseObserver.getValues();

        assertEquals(1, results.size());
        RemoveGroupMembersResponse response = results.get(0);
        assertFalse(response.getIsSuccess(), "Teacher was removed: " + response.getMessage());
        assertTrue(userRepository.findById(1).get().getRoles().contains(UserRole.TEACHER), "User is still a teacher");
    }

    /**
     * Tests that remove groups fails when a group doesn't exist
     */
    @Test
    @Transactional
    void givenSampleData_whenRemoveGroupMembersIsCalledOnNoGroup_thenFails() {
        RemoveGroupMembersRequest request = RemoveGroupMembersRequest.newBuilder().setGroupId(5826).addAllUserIds(Arrays.asList(1)).build();
        StreamRecorder<RemoveGroupMembersResponse> responseObserver = StreamRecorder.create();
        groupServerService.removeGroupMembers(request, responseObserver);

        assertNull(responseObserver.getError());
        List<RemoveGroupMembersResponse> results = responseObserver.getValues();

        assertEquals(1, results.size());
        RemoveGroupMembersResponse response = results.get(0);
        assertFalse(response.getIsSuccess(), "Non-existant group was found: " + response.getMessage());
    }

    /**
     * Tests that add groups fails when a group doesn't exist
     */
    @Test
    @Transactional
    void givenSampleData_whenAddGroupMembersIsCalledOnNoGroup_thenFails() {
        AddGroupMembersRequest request = AddGroupMembersRequest.newBuilder().setGroupId(5826).addAllUserIds(Arrays.asList(1)).build();
        StreamRecorder<AddGroupMembersResponse> responseObserver = StreamRecorder.create();
        groupServerService.addGroupMembers(request, responseObserver);

        assertNull(responseObserver.getError());
        List<AddGroupMembersResponse> results = responseObserver.getValues();

        assertEquals(1, results.size());
        AddGroupMembersResponse response = results.get(0);
        assertFalse(response.getIsSuccess(), "Non-existant group was found: " + response.getMessage());
    }

    /**
     * Tests that a user is added to the teacher group
     */
    @Test
    @Transactional
    void givenSampleData_whenAddGroupMembersIsCalledOnTeacherGroup_thenMemberIsTeacher() {
        AddGroupMembersRequest request = AddGroupMembersRequest.newBuilder().setGroupId(-1).addAllUserIds(Arrays.asList(8)).build();
        StreamRecorder<AddGroupMembersResponse> responseObserver = StreamRecorder.create();
        groupServerService.addGroupMembers(request, responseObserver);

        assertNull(responseObserver.getError());
        List<AddGroupMembersResponse> results = responseObserver.getValues();

        assertEquals(1, results.size());
        AddGroupMembersResponse response = results.get(0);
        assertTrue(response.getIsSuccess(), "Member failed to add to the group: " + response.getMessage());
        assertTrue(userRepository.findById(8).get().getRoles().contains(UserRole.TEACHER), "User is not a teacher");
    }

    /**
     * Tests that a member is added to the specified group
     */
    @Test
    @Transactional
    void givenSampleData_whenAddGroupMembersIsCalledOnNormalGroup_thenMemberAdded() {
        AddGroupMembersRequest request = AddGroupMembersRequest.newBuilder().setGroupId(1).addAllUserIds(Arrays.asList(15)).build();
        StreamRecorder<AddGroupMembersResponse> responseObserver = StreamRecorder.create();
        groupServerService.addGroupMembers(request, responseObserver);

        assertNull(responseObserver.getError());
        List<AddGroupMembersResponse> results = responseObserver.getValues();

        assertEquals(1, results.size());
        AddGroupMembersResponse response = results.get(0);
        assertTrue(response.getIsSuccess(), "Member failed to add to the group: " + response.getMessage());
        assertEquals(1, groupsRepository.findById(1).get().getUsers().stream().filter(user -> user.getUserId() == 15).count(), "User not added to group");
    }
}
