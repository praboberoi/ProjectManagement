package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.shared.identityprovider.*;

import java.util.List;

import org.springframework.stereotype.Service;

import com.google.protobuf.Empty;

import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import nz.ac.canterbury.seng302.portfolio.model.Groups;

/**
 * Client service used to communicate to the IDP application relating to group features
 */
@Service
public class GroupService {

    @GrpcClient("identity-provider-grpc-server")
    private GroupsServiceGrpc.GroupsServiceBlockingStub groupsStub;
    @GrpcClient("identity-provider-grpc-server")
    private GroupsServiceGrpc.GroupsServiceStub groupsServiceStub;

    /**
     * Sends a delete group request to the gRPC server
     * @param groupId Id of the group to return
     * @return The result of the delete operation
     * @throws StatusRuntimeException Failure status of the server call
     */
    public DeleteGroupResponse deleteGroup(int groupId) throws StatusRuntimeException {
        return groupsStub.deleteGroup(DeleteGroupRequest.newBuilder().setGroupId(groupId).build());
    }

    /**
     * Request the No-Group group details from IDP.
     * @return No-Group Group
     */
    public Groups getMembersWithoutAGroup() {
        GroupDetailsResponse response = groupsStub.getMembersWithoutAGroup(Empty.newBuilder().build());
        return new Groups(response);
    }

    /**
     * Request the Teaching group details from IDP.
     * @return Teacher Group
     */
    public Groups getTeachingStaffGroup() {
        GroupDetailsResponse response = groupsStub.getTeachingStaffGroup(Empty.newBuilder().build());
        return new Groups(response);
    }

    /**
     * Request a selected group details from IDP.
     * @param groupId id of the selected group
     * @return Selected Group's details.
     */
    public Groups getGroupById(int groupId) {
        GetGroupDetailsRequest groupDetailsRequest = GetGroupDetailsRequest.newBuilder().setGroupId(groupId).build();
        GroupDetailsResponse groupDetailsResponse = groupsStub.getGroupDetails(groupDetailsRequest);
        return new Groups(groupDetailsResponse);
    }

    /**
     * Request all normal groups from the IDP.
     * @return List of all groups
     */
    public List<Groups> getPaginatedGroups() {
        PaginatedGroupsResponse response = groupsStub.getPaginatedGroups(GetPaginatedGroupsRequest.newBuilder().build());
        return response.getGroupsList().stream().map(Groups::new).toList();
    }
}
