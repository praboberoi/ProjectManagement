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
        assertFalse(response.getIsSuccess(), "Non-existent group was found: " + response.getMessage());
    }



    /**
     * Tests that the group is created successfully.
     */
    @Test
    @Transactional
    void givenSampleData_whenCreateGroupCalled_thenTheGroupExists() {
        CreateGroupRequest request = CreateGroupRequest.newBuilder().setShortName("Test").setLongName("Test Group").build();
        StreamRecorder<CreateGroupResponse> responseObserver = StreamRecorder.create();
        groupServerService.createGroup(request, responseObserver);

        assertNull(responseObserver.getError());
        List<CreateGroupResponse> results = responseObserver.getValues();
        assertEquals(1, results.size());

        CreateGroupResponse response = results.get(0);
        assertTrue(response.getIsSuccess(), "Group was incorrectly created: " + response.getMessage());
    }

    /**
     * Tests that the group with no short name is not created.
     */
    @Test
    @Transactional
    void givenAGroupWithNoShortName_whenCreateGroupCalled_thenTheGroupDoesNotExist() {
        CreateGroupRequest request = CreateGroupRequest.newBuilder().setShortName("").build();
        StreamRecorder<CreateGroupResponse> responseObserver = StreamRecorder.create();
        groupServerService.createGroup(request, responseObserver);

        assertNull(responseObserver.getError());
        List<CreateGroupResponse> results = responseObserver.getValues();
        assertEquals(1, results.size());

        CreateGroupResponse response = results.get(0);
        assertFalse(response.getIsSuccess(), "Group Short Name validation was incorrect: " + response.getMessage());
    }

    /**
     * Tests that the group with no long or short name is not created.
     */
    @Test
    @Transactional
    void givenAGroupWithNoLongName_whenCreateGroupCalled_thenTheGroupDoesNotExist() {
        CreateGroupRequest request = CreateGroupRequest.newBuilder().setLongName("").build();
        StreamRecorder<CreateGroupResponse> responseObserver = StreamRecorder.create();
        groupServerService.createGroup(request, responseObserver);

        assertNull(responseObserver.getError());
        List<CreateGroupResponse> results = responseObserver.getValues();
        assertEquals(1, results.size());

        CreateGroupResponse response = results.get(0);
        assertFalse(response.getIsSuccess(), "Group Long Name validation was incorrect: " + response.getMessage());
    }

    /**
     * Tests that the group with no short name is not created.
     */
    @Test
    @Transactional
    void givenAGroupWithNoShortNameOrLongName_whenCreateGroupCalled_thenTheGroupDoesNotExist() {
        CreateGroupRequest request = CreateGroupRequest.newBuilder().setShortName("").setLongName("").build();
        StreamRecorder<CreateGroupResponse> responseObserver = StreamRecorder.create();
        groupServerService.createGroup(request, responseObserver);

        assertNull(responseObserver.getError());
        List<CreateGroupResponse> results = responseObserver.getValues();
        assertEquals(1, results.size());

        CreateGroupResponse response = results.get(0);
        assertFalse(response.getIsSuccess(), "Group Short Name and Long Name validation was incorrect: " + response.getMessage());
    }


    /**
     * Tests that the group with no long name is not created.
     */
    @Test
    @Transactional
    void givenAGroupWithAShortNameLessThanThreeCharacters_whenCreateGroupCalled_thenTheGroupDoesNotExist() {
        CreateGroupRequest request = CreateGroupRequest.newBuilder().setLongName("Gr").build();
        StreamRecorder<CreateGroupResponse> responseObserver = StreamRecorder.create();
        groupServerService.createGroup(request, responseObserver);

        assertNull(responseObserver.getError());
        List<CreateGroupResponse> results = responseObserver.getValues();
        assertEquals(1, results.size());

        CreateGroupResponse response = results.get(0);
        assertFalse(response.getIsSuccess(), "Group Long Name validation was incorrect: " + response.getMessage());
    }

    /**
     * Tests that the group with a short name longer than 50 characters is not created.
     */
    @Test
    @Transactional
    void givenAGroupWithAShortNameLongerThanThreeCharacters_whenCreateGroupCalled_thenTheGroupDoesNotExist() {
        CreateGroupRequest request = CreateGroupRequest.newBuilder().setShortName("Test Group with a name longer than of 50 characters.").setLongName("Long name").build();
        StreamRecorder<CreateGroupResponse> responseObserver = StreamRecorder.create();
        groupServerService.createGroup(request, responseObserver);

        assertNull(responseObserver.getError());
        List<CreateGroupResponse> results = responseObserver.getValues();
        assertEquals(1, results.size());

        CreateGroupResponse response = results.get(0);
        assertFalse(response.getIsSuccess(), "Group short name validation was incorrect: " + response.getMessage());
    }

    /**
     * Tests that the group with a long name longer than 100 characters is not created.
     */
    @Test
    @Transactional
    void givenAGroupWithALongNameLongerThanOneHundredCharacters_whenCreateGroupCalled_thenTheGroupDoesNotExist() {
        CreateGroupRequest request = CreateGroupRequest.newBuilder().setLongName("Test Group with a name longer than of 100 characters. Test Group with a name longer than of 100 characters.").setShortName("short name").build();
        StreamRecorder<CreateGroupResponse> responseObserver = StreamRecorder.create();
        groupServerService.createGroup(request, responseObserver);

        assertNull(responseObserver.getError());
        List<CreateGroupResponse> results = responseObserver.getValues();
        assertEquals(1, results.size());

        CreateGroupResponse response = results.get(0);
        assertFalse(response.getIsSuccess(), "Group long name validation was incorrect: " + response.getMessage());
    }

    /**
     * Tests that a group is not created when one already exists with the same long name.
     */
    @Test
    @Transactional
    void givenAGroupWithExists_whenCreateGroupCalledWithSameLongName_thenTheGroupDoesNotExist() {
        CreateGroupRequest firstRequest = CreateGroupRequest.newBuilder().setLongName("Test Group").setShortName("short").build();

        StreamRecorder<CreateGroupResponse> firstResponseObserver = StreamRecorder.create();
        groupServerService.createGroup(firstRequest, firstResponseObserver);
        firstResponseObserver.getValues();

        CreateGroupRequest secondRequest = CreateGroupRequest.newBuilder().setLongName("Test Group").setShortName("short name").build();
        StreamRecorder<CreateGroupResponse> secondResponseObserver = StreamRecorder.create();
        groupServerService.createGroup(secondRequest, secondResponseObserver);

        assertNull(secondResponseObserver.getError());
        List<CreateGroupResponse> results = secondResponseObserver.getValues();
        assertEquals(1, results.size());

        CreateGroupResponse response = results.get(0);
        assertFalse(response.getIsSuccess(), "Group long name validation was incorrect: " + response.getMessage());
    }

    /**
     * Tests that a group is not created when one already exists with the same short name.
     */
    @Test
    @Transactional
    void givenAGroupWithExists_whenCreateGroupCalledWithSameShortName_thenTheGroupDoesNotExist() {
        CreateGroupRequest firstRequest = CreateGroupRequest.newBuilder().setShortName("Test Group").setLongName("Long").build();

        StreamRecorder<CreateGroupResponse> firstResponseObserver = StreamRecorder.create();
        groupServerService.createGroup(firstRequest, firstResponseObserver);
        firstResponseObserver.getValues();

        CreateGroupRequest secondRequest = CreateGroupRequest.newBuilder().setShortName("Test Group").setLongName("Long Name").build();
        StreamRecorder<CreateGroupResponse> secondResponseObserver = StreamRecorder.create();
        groupServerService.createGroup(secondRequest, secondResponseObserver);

        assertNull(secondResponseObserver.getError());
        List<CreateGroupResponse> results = secondResponseObserver.getValues();
        assertEquals(1, results.size());

        CreateGroupResponse response = results.get(0);
        assertFalse(response.getIsSuccess(), "Group short name validation was incorrect: " + response.getMessage());
    }

    /**
     * Tests that a group is not created when one already exists with the same long and short name.
     */
    @Test
    @Transactional
    void givenAGroupWithExists_whenCreateGroupCalledWithSameLongAndShortName_thenTheGroupDoesNotExist() {
        CreateGroupRequest firstRequest = CreateGroupRequest.newBuilder().setShortName("Test Group Short").setLongName("Test Group Long").build();

        StreamRecorder<CreateGroupResponse> firstResponseObserver = StreamRecorder.create();
        groupServerService.createGroup(firstRequest, firstResponseObserver);
        firstResponseObserver.getValues();

        CreateGroupRequest secondRequest = CreateGroupRequest.newBuilder().setShortName("Test Group Short").setLongName("Test Group Long").build();
        StreamRecorder<CreateGroupResponse> secondResponseObserver = StreamRecorder.create();
        groupServerService.createGroup(secondRequest, secondResponseObserver);

        assertNull(secondResponseObserver.getError());
        List<CreateGroupResponse> results = secondResponseObserver.getValues();
        assertEquals(1, results.size());

        CreateGroupResponse response = results.get(0);
        assertFalse(response.getIsSuccess(), "Group short and long name validation was incorrect: " + response.getMessage());
    }
}