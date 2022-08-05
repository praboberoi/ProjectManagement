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

import java.util.List;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;
/**
 * Unit tests for methods in the GroupServerService class
 */
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext
class GroupServerServiceIntergrationTests {

    @Autowired
    private GroupsRepository groupsRepository;

    @Autowired
    private UserRepository userRepository;

    private GroupServerService groupServerService;

    @BeforeEach
    void initServerService() {
        groupServerService = new GroupServerService(groupsRepository);
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
        assertEquals(30, response.getMembersCount(), "Incorrect number of users returned in 'no group' group");
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
        assertEquals(4, response.getMembersCount(), "Incorrect number of users returned in 'teacher' group");
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
     * Tests that all the groups are returned.
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
}
