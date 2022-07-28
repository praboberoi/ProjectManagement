package nz.ac.canterbury.seng302.identityprovider.service;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;

import com.google.protobuf.Empty;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import nz.ac.canterbury.seng302.identityprovider.model.GroupsRepository;
import nz.ac.canterbury.seng302.identityprovider.util.ResponseUtils;
import nz.ac.canterbury.seng302.shared.identityprovider.*;

/**
 * Grpc service used to perform function relating to groups.
 */
@GrpcService
public class GroupServerService extends GroupsServiceGrpc.GroupsServiceImplBase {

    private GroupsRepository groupsRepository;
    
    @Value("${hostAddress}")
    private String hostAddress;

    public GroupServerService(GroupsRepository groupsRepository) {
        this.groupsRepository = groupsRepository;
    }

    /**
     * Gets a list of users that don't have a group and returns it to the gRPC client
     */
    @Override
    public void getMembersWithoutAGroup(Empty request, StreamObserver<GroupDetailsResponse> responseObserver) {
        GroupDetailsResponse.Builder reply = GroupDetailsResponse.newBuilder();
        reply.addAllMembers(groupsRepository.findUsersNotInGroup().stream().map(user -> ResponseUtils.prepareUserResponse(user, hostAddress)).collect(Collectors.toList()));
        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }

    /**
     * Gets a list of users that are teachers and returns it to the gRPC client
     */
    @Override
    public void getTeachingStaffGroup(Empty request, StreamObserver<GroupDetailsResponse> responseObserver) {
        GroupDetailsResponse.Builder reply = GroupDetailsResponse.newBuilder();
        reply.addAllMembers(groupsRepository.findTeacherGroup().stream().map(user -> ResponseUtils.prepareUserResponse(user, hostAddress)).collect(Collectors.toList()));
        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }
}
