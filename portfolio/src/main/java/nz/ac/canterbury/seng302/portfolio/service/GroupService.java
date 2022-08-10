package nz.ac.canterbury.seng302.portfolio.service;

import com.google.protobuf.Empty;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import nz.ac.canterbury.seng302.portfolio.model.Groups;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.springframework.stereotype.Service;

import java.util.List;

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
     * Sends a create group request to the gRPC server
     * @param shortName Short name of the group
     * @param longName Long name of the group
     * @return The result of the create operation
     * @throws StatusRuntimeException Failure status of the server call
     */
    public CreateGroupResponse createGroup(String shortName, String longName) throws StatusRuntimeException {
        return groupsStub.createGroup(CreateGroupRequest.newBuilder()
                        .setLongName(longName)
                        .setShortName(shortName)
                .build());
    }

    /**
     * Sends a modify group request to the gRPC server
     * @param groupId Id of the group to edit
     * @param shortName Short name of the group
     * @param longName Long name of the group
     * @return The result of the create operation
     * @throws StatusRuntimeException Failure status of the server call
     */
    public ModifyGroupDetailsResponse modifyGroup(Integer groupId, String shortName, String longName) throws StatusRuntimeException {
        return groupsStub.modifyGroupDetails(ModifyGroupDetailsRequest.newBuilder().setGroupId(groupId).setLongName(longName)
            .setShortName(shortName).build());
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

    /**
     * Request the removal of users from the group from the IDP.
     * @param userIds List of users to be removed
     * @param groupId Group users are to be removed from
     * @return Success status and message
     */
    public RemoveGroupMembersResponse removeGroupMembers(List<Integer> userIds, int groupId) {
        RemoveGroupMembersRequest request = RemoveGroupMembersRequest.newBuilder().setGroupId(groupId).addAllUserIds(userIds).build();
        return groupsStub.removeGroupMembers(request);
    }

    /**
     * Request the addition of users to the group on the IDP.
     * @param userIds List of users to be added
     * @param groupId Group users are to be added to
     * @return Success status and message
     */
    public AddGroupMembersResponse addGroupMembers(List<Integer> userIds, Integer groupId) {
        return groupsStub.addGroupMembers(AddGroupMembersRequest.newBuilder().setGroupId(groupId).addAllUserIds(userIds).build());
    }
}
