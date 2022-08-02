package nz.ac.canterbury.seng302.identityprovider.util;

import java.util.List;
import java.util.stream.Collectors;

import com.google.protobuf.Timestamp;

import nz.ac.canterbury.seng302.identityprovider.model.Groups;
import nz.ac.canterbury.seng302.identityprovider.model.User;
import nz.ac.canterbury.seng302.shared.identityprovider.GroupDetailsResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.PaginatedUsersResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;

/**
 * A collection of methods that package raw types into protobufs to be
 * communicated along the gRPC pipeline
 */
public class ResponseUtils {

    private ResponseUtils() {}

    /**
     * Packages a user object into a userResponse protobuf object
     * @param user User to be packaged
     * @return userResponse as a protobuf object
     */
    public static UserResponse prepareUserResponse(User user, String hostAddress) {
        UserResponse.Builder response = UserResponse.newBuilder();
        response.setUsername(user.getUsername())
        .setId(user.getUserId())
        .setFirstName(user.getFirstName())
        .setLastName(user.getLastName())
        .setNickname(user.getNickname())
        .setPersonalPronouns(user.getPersonalPronouns())
        .setBio(user.getBio())
        .setEmail(user.getEmail())
        .addAllRoles(user.getRoles())
        .setProfileImagePath(hostAddress + "/profile/" + user.getUserId())
        .setCreated(Timestamp.newBuilder().setSeconds(user.getDateCreated().getTime()).build());

       return response.build();
    }

    /**
     * Packages a list of userResponses into a PaginatedUsersResponse protobuf object
     * @param users List of User objects to be packaged
     * @param resultSetSize The total number of entries in the search
     * @return PaginatedUsersResponse as a protobuf object
     */
    public static PaginatedUsersResponse preparePaginatedUsersResponse(List<UserResponse> users, long resultSetSize) {
        PaginatedUsersResponse.Builder response = PaginatedUsersResponse.newBuilder();
        response.addAllUsers(users).setResultSetSize((int)resultSetSize);
       return response.build();
    }

    public static GroupDetailsResponse prepareGroupDetailResponse(Groups group, String hostAddress) {
        GroupDetailsResponse.Builder response = GroupDetailsResponse.newBuilder();
        response.addAllMembers(group.getUsers().stream().map(user -> prepareUserResponse(user, hostAddress)).collect(Collectors.toList()));
        response.setGroupId(group.getGroupId());
        response.setLongName(group.getLongName());
        response.setShortName(group.getShortName());

        return response.build();
    }
}
