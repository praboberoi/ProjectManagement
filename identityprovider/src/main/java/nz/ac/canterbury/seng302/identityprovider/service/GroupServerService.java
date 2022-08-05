package nz.ac.canterbury.seng302.identityprovider.service;

import java.util.stream.Collectors;

import nz.ac.canterbury.seng302.identityprovider.model.Groups;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final GroupsRepository groupsRepository;
    
    @Value("${hostAddress}")
    private String hostAddress;

    Logger logger = LoggerFactory.getLogger(GroupServerService.class);

    public GroupServerService(GroupsRepository groupsRepository) {
        this.groupsRepository = groupsRepository;
    }

    /**
     * Gets a list of users that don't have a group and returns it to the gRPC client
     * @param request          Empty protobuf request
     * @param responseObserver Returns to previous method with data
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
     * @param request          Empty protobuf request
     * @param responseObserver Returns to previous method with data
     */
    @Override
    public void getTeachingStaffGroup(Empty request, StreamObserver<GroupDetailsResponse> responseObserver) {
        GroupDetailsResponse.Builder reply = GroupDetailsResponse.newBuilder();
        reply.addAllMembers(groupsRepository.findTeacherGroup().stream().map(user -> ResponseUtils.prepareUserResponse(user, hostAddress)).collect(Collectors.toList()));
        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }

    /**
     * Delete a group from the database and return if the deletion was successful to the gRPC client
     * @param request          Delete group request containing the id of the group to delete
     * @param responseObserver Returns to previous method with data
     */
    @Override
    public void deleteGroup(DeleteGroupRequest request, StreamObserver<DeleteGroupResponse> responseObserver) {
        DeleteGroupResponse.Builder reply = DeleteGroupResponse.newBuilder();
        try {
            if (groupsRepository.deleteGroupByGroupId(request.getGroupId()) == 1) {
                logger.info("Group {} has been deleted", request.getGroupId());
                reply.setIsSuccess(true);
                reply.setMessage(String.format("Group %d deleted successfully", request.getGroupId()));
            } else {
                reply.setIsSuccess(false);
                reply.setMessage(String.format("Unable to delete group %d", request.getGroupId()));
            }
        } catch (Exception e) {
            logger.error("An error occurred while deleting group {}", request.getGroupId(), e);
            reply.setIsSuccess(false);
            reply.setMessage(String.format("An error occurred while deleting group %d", request.getGroupId()));
        }

        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }

    /**
     * Create a group from the database and return if the creation was successful to the gRPC client
     * @param request          Create group request containing the id of the group to create
     * @param responseObserver Returns to previous method with data
     */
    @Override
    public void createGroup(CreateGroupRequest request, StreamObserver<CreateGroupResponse> responseObserver) {
        CreateGroupResponse.Builder reply = CreateGroupResponse.newBuilder();
        if (groupsRepository.getAllByShortNameEquals(request.getShortName()) == null) {

        } else if (groupsRepository.getAllByLongNameEquals(request.getLongName()) == null)
        try {
            Groups newGroup = new Groups(request.getLongName(), request.getShortName());
            groupsRepository.save(newGroup);
            reply.setIsSuccess(true);
            reply.setMessage("Group created successfully");
        } catch (Exception e) {
            logger.error("An error occurred while creating group", e);
            reply.setIsSuccess(false);
            reply.setMessage("An error occurred while creating group.");
        }
        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }

}
