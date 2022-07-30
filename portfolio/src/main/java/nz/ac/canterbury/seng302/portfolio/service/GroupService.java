package nz.ac.canterbury.seng302.portfolio.service;

import org.springframework.stereotype.Service;

import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import nz.ac.canterbury.seng302.shared.identityprovider.DeleteGroupRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.DeleteGroupResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.GroupsServiceGrpc;

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
        DeleteGroupResponse response = groupsStub.deleteGroup(DeleteGroupRequest.newBuilder()
                .setGroupId(groupId)
                .build());
        return response;
    }
    
}
