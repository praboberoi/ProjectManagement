package nz.ac.canterbury.seng302.identityprovider.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.google.protobuf.Empty;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import nz.ac.canterbury.seng302.identityprovider.model.Groups;
import nz.ac.canterbury.seng302.identityprovider.model.GroupsRepository;
import nz.ac.canterbury.seng302.identityprovider.model.User;
import nz.ac.canterbury.seng302.identityprovider.model.UserRepository;
import nz.ac.canterbury.seng302.identityprovider.util.ResponseUtils;
import nz.ac.canterbury.seng302.shared.identityprovider.*;

/**
 * Grpc service used to perform function relating to groups.
 */
@GrpcService
public class GroupServerService extends GroupsServiceGrpc.GroupsServiceImplBase {

    private GroupsRepository groupsRepository;

    private UserRepository userRepository;
    
    @Value("${hostAddress}")
    private String hostAddress;

    Logger logger = LoggerFactory.getLogger(GroupServerService.class);

    public GroupServerService(GroupsRepository groupsRepository, UserRepository userRepository) {
        this.groupsRepository = groupsRepository;
        this.userRepository = userRepository;
    }

    /**
     * Gets a list of users that don't have a group and returns it to the gRPC client
     * @param request          Empty protobuf request
     * @param responseObserver Returns to previous method with data
     */
    @Override
    public void getMembersWithoutAGroup(Empty request, StreamObserver<GroupDetailsResponse> responseObserver) {
        GroupDetailsResponse.Builder reply = GroupDetailsResponse.newBuilder();
        reply.setShortName("Members without a group");
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
        reply.setShortName("Teaching Staff");
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
            logger.error("An error occured while deleting group {}", request.getGroupId(), e);
            reply.setIsSuccess(false);
            reply.setMessage(String.format("An error occured while deleting group %d", request.getGroupId()));
        }

        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }

    /**
     * Gets all groups from the database and returns them to the gRPC client
     * @param request           The filters for pagination
     * @param responseObserver  Returns to previous method with data
     */
    @Override
    public void getPaginatedGroups(GetPaginatedGroupsRequest request, StreamObserver<PaginatedGroupsResponse> responseObserver) {
        List<Groups> groups = (List<Groups>) groupsRepository.findAll();
        PaginatedGroupsResponse.Builder response = PaginatedGroupsResponse.newBuilder();
        for (Groups group : groups) {
            response.addGroups(ResponseUtils.prepareGroupDetailResponse(group, hostAddress));
        }

        response.setResultSetSize(groups.size());

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    /**
     * Gets the specified group and returns them to the gRPC client
     * @param request           The protobuf request containing the id of the group to get
     * @param responseObserver  Returns to previous method with data
     */
    @Override
    public void getGroupDetails(GetGroupDetailsRequest request, StreamObserver<GroupDetailsResponse> responseObserver) {
        GroupDetailsResponse.Builder reply = GroupDetailsResponse.newBuilder();
        Groups group = groupsRepository.findById(request.getGroupId()).orElse(null);
        if (group != null) {
            reply.setGroupId(group.getGroupId());
            reply.setShortName(group.getShortName());
            reply.setLongName(group.getLongName());
            reply.addAllMembers(group.getUsers().stream().map(user -> ResponseUtils.prepareUserResponse(user, hostAddress)).collect(Collectors.toList()));
        }
        
        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }

    /**
     * Gets the specified members from the group and returns the status to the gRPC client
     * @param request           The protobuf Remove members request containing the group id and member ids. Group id of -1 is teacher group.
     * @param responseObserver  Returns to previous method with data
     */
    @Override
    public void removeGroupMembers(RemoveGroupMembersRequest request, StreamObserver<RemoveGroupMembersResponse> responseObserver) {
        RemoveGroupMembersResponse.Builder reply = RemoveGroupMembersResponse.newBuilder();
        if (request.getGroupId() == -1) {
            List<User> users = StreamSupport.stream(userRepository.findAllById(request.getUserIdsList()).spliterator(), false)
            .filter(user -> user.getRoles().size() > 1).toList();
            
            // If the number of users is incorrect remove applicable user's roles, move below the if statment if it shouldn't remove any
            for (User user: users) {
                user.removeRole(UserRole.TEACHER);
                userRepository.save(user);
                logger.info("Removed Teacher role from user {}", user.getUserId());
            }

            if (users.size() != request.getUserIdsCount()) {
                reply.setMessage("Unable to remove all users. Users must have 1 role at all times.");
                reply.setIsSuccess(false);
                responseObserver.onNext(reply.build());
                responseObserver.onCompleted();
                return;
            }

            reply.setMessage("Removed Teacher role from selected users");
            reply.setIsSuccess(true);
            responseObserver.onNext(reply.build());
            responseObserver.onCompleted();
            return;
        }

        Groups group = groupsRepository.findById(request.getGroupId()).orElse(null);
        if (group == null) {
            reply.setIsSuccess(false);
            reply.setMessage("Unable to find group.");

            responseObserver.onNext(reply.build());
            responseObserver.onCompleted();
            return;
        }
        if (group.getUsers().stream().filter(user -> request.getUserIdsList().contains(user.getUserId())).toList().size() != request.getUserIdsCount()) {
            reply.setIsSuccess(false);
            reply.setMessage("Unable to find all members in group.");

            responseObserver.onNext(reply.build());
            responseObserver.onCompleted();
            return;
        }
        List<User> newUserList = group.getUsers().stream().filter(user -> !request.getUserIdsList().contains(user.getUserId())).toList();
        group.setUsers(newUserList);
        logger.info("{} members removed from group {}", request.getUserIdsCount(), request.getGroupId());
        groupsRepository.save(group);

        reply.setIsSuccess(true);
        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }
}
