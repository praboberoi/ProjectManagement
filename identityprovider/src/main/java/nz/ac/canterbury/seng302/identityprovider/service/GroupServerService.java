package nz.ac.canterbury.seng302.identityprovider.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

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

    private final GroupsRepository groupsRepository;

    private UserRepository userRepository;

    @Value("${hostAddress}")
    private String hostAddress;

    Logger logger = LoggerFactory.getLogger(GroupServerService.class);

    public GroupServerService(GroupsRepository groupsRepository, UserRepository userRepository) {
        this.groupsRepository = groupsRepository;
        this.userRepository = userRepository;
    }

    /**
     * Gets a list of users that don't have a group and returns it to the gRPC client in the StreamObserver
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
     * Gets a list of users that are teachers and returns it to the gRPC client in the StreamObserver
     * @param request          Empty protobuf request
     * @param responseObserver Returns to previous method with data
     */
    @Override
    public void getTeachingStaffGroup(Empty request, StreamObserver<GroupDetailsResponse> responseObserver) {
        GroupDetailsResponse.Builder reply = GroupDetailsResponse.newBuilder();
        reply.setShortName("Teaching Staff");
        reply.addAllMembers(groupsRepository.findTeacherGroup().stream().map(user -> ResponseUtils.prepareUserResponse(user, hostAddress)).toList());
        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }

    /**
     * Delete a group from the database and return if the deletion was successful to the gRPC client in the StreamObserver
     * @param request          Delete group request containing the id of the group to delete
     * @param responseObserver Returns to previous method with data
     */
    @Override
    @Transactional
    public void deleteGroup(DeleteGroupRequest request, StreamObserver<DeleteGroupResponse> responseObserver) {
        DeleteGroupResponse.Builder reply = DeleteGroupResponse.newBuilder();
        try {
            if (groupsRepository.existsById(request.getGroupId())) {
                groupsRepository.deleteById(request.getGroupId());
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
     * Create a group from the database and return if the creation was successful to the gRPC client in the StreamObserver
     *
     * @param request          Create group request containing the id of the group to create
     * @param responseObserver Returns to previous method with data
     */
    @Override
    public void createGroup(CreateGroupRequest request, StreamObserver<CreateGroupResponse> responseObserver) {
        CreateGroupResponse.Builder reply = CreateGroupResponse.newBuilder();
        if (request.getShortName().length() < 3 || request.getShortName().length() > 50 || request.getLongName().length() < 3 || request.getLongName().length() > 100) {
            reply.setIsSuccess(false);
            reply.setMessage("Incorrect group details entered");
        } else if (groupsRepository.getAllByShortNameEquals(request.getShortName()) != null) {
            reply.setIsSuccess(false);
            if (groupsRepository.getAllByLongNameEquals(request.getLongName()) != null) {
                reply.setMessage(String.format("Group with short name %s and long name %s already exists", request.getShortName(), request.getLongName()));
            } else {
                reply.setMessage(String.format("Group with short name %s already exists", request.getShortName()));
            }
        } else if (groupsRepository.getAllByLongNameEquals(request.getLongName()) != null) {
            reply.setIsSuccess(false);
            reply.setMessage(String.format("Group with long name %s already exists", request.getLongName()));
        } else {
            try {
                Groups newGroup = new Groups(request.getShortName(), request.getLongName());
                groupsRepository.save(newGroup);
                reply.setIsSuccess(true);
                reply.setMessage("Group created successfully");
            } catch (Exception e) {
                logger.error("An error occurred while creating group", e);
                reply.setIsSuccess(false);
                reply.setMessage("An error occurred while creating group.");
            }
        }
        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }

    /**
     * Gets all groups from the database and returns them to the gRPC client in the StreamObserver
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
     * Gets the specified group and returns them to the gRPC client in the StreamObserver
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
     * Removes the specified members from the group and returns the status to the gRPC client in the StreamObserver
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

        reply.setMessage(String.format("%d member%s removed from group %s", request.getUserIdsCount(), request.getUserIdsCount()==1?"":"s" , group.getShortName()));
        reply.setIsSuccess(true);
        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }

    /**
     * Adds the specified members from the group and returns the status to the gRPC client in the StreamObserver
     * @param request           The protobuf Add members request containing the group id and member ids. Group id of -1 is teacher group.
     * @param responseObserver  Returns to previous method with data
     */
    @Override
    public void addGroupMembers(AddGroupMembersRequest request, StreamObserver<AddGroupMembersResponse> responseObserver) {
        AddGroupMembersResponse.Builder reply = AddGroupMembersResponse.newBuilder();

        // Logic if the group is the teacher's group
        if (request.getGroupId() == -1) {
            List<User> users = StreamSupport.stream(userRepository.findAllById(request.getUserIdsList()).spliterator(), false)
            .filter(user -> !user.getRoles().contains(UserRole.TEACHER)).toList();

            for (User user: users) {
                user.addRole(UserRole.TEACHER);
                userRepository.save(user);
                logger.info("Added Teacher role to user {}", user.getUserId());
            }

            reply.setMessage("Added Teacher role to selected users");
            reply.setIsSuccess(true);
            responseObserver.onNext(reply.build());
            responseObserver.onCompleted();
            return;
        }

        Groups group = groupsRepository.findById(request.getGroupId()).orElse(null);
        if (group == null) {
            reply.setMessage("Unable to find group.");
            reply.setIsSuccess(false);

            responseObserver.onNext(reply.build());
            responseObserver.onCompleted();
            return;
        }

        Set<User> newUserList = group.getUsers();
        Iterable<User> users =  userRepository.findAllById(request.getUserIdsList());
        users.forEach(newUserList::add);
        group.setUsers(newUserList);

        logger.info("{} members added to group {}", request.getUserIdsCount(), request.getGroupId());
        groupsRepository.save(group);

        reply.setMessage(String.format("%d member%s added to group %s", request.getUserIdsCount(), request.getUserIdsCount()==1?"":"s", group.getShortName()));
        reply.setIsSuccess(true);
        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }
}
