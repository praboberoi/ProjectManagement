package nz.ac.canterbury.seng302.identityprovider;

import io.grpc.internal.testing.StreamRecorder;
import nz.ac.canterbury.seng302.identityprovider.model.GroupsRepository;
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

    private GroupServerService groupServerService;

    @BeforeEach
    void initUAServerService() {
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
        assertEquals(40, response.getMembersCount(), "Incorrect number of users returned in 'no group' group");
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
}
