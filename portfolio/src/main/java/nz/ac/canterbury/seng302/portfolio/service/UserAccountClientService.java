package nz.ac.canterbury.seng302.portfolio.service;

import com.google.protobuf.ByteString;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import nz.ac.canterbury.seng302.portfolio.controller.AccountController;
import nz.ac.canterbury.seng302.portfolio.utils.UserField;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import nz.ac.canterbury.seng302.shared.util.FileUploadStatusResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * Client service used to communicate to the IDP application relating to user account features
 */
@Service
public class UserAccountClientService {

    @GrpcClient("identity-provider-grpc-server")
    private UserAccountServiceGrpc.UserAccountServiceBlockingStub userAccountStub;
    @GrpcClient("identity-provider-grpc-server")
    private UserAccountServiceGrpc.UserAccountServiceStub userAccountServiceStub;

    private static Logger logger =LoggerFactory.getLogger(UserAccountClientService.class);


    /**
     * Sends a register request to the server
     * @param username New user's username (required)
     * @param firstName New user's first name (required)
     * @param lastName New user's last name (required)
     * @param nickname New user's nickname
     * @param bio New user's bio
     * @param pronouns New user's pronouns
     * @param email New user's email (required)
     * @param password New user's password (required)
     * @return Response from the server
     * @throws StatusRuntimeException Failure status of the server call
     */
    public UserRegisterResponse register(final String username,
                           final String firstName,
                           final String lastName,
                           final String nickname,
                           final String bio,
                           final String pronouns,
                           final String email,
                           final String password) throws StatusRuntimeException {
        return userAccountStub.register(UserRegisterRequest.newBuilder()
                .setUsername(username)
                .setFirstName(firstName)
                .setLastName(lastName)
                .setNickname(nickname)
                .setBio(bio)
                .setPersonalPronouns(pronouns)
                .setEmail(email)
                .setPassword(password)
                .build());
    }

    /**
     * Sends an edit user request to the server
     *
     * @param firstName First Name of User (edited or not)
     * @param lastName Last Name of User (edited or not)
     * @param nickname Nickname of User (edited or not)
     * @param bio Bio of User (edited or not)
     * @param pronouns Pronouns of User (edited or not)
     * @param email Email of User (edited or not)
     * @return Response from server
     * @throws StatusRuntimeException Failure status of the server call
     */
    public EditUserResponse edit(
            final int userId,
            final String firstName,
            final String lastName,
            final String nickname,
            final String bio,
            final String pronouns,
            final String email
    ) throws StatusRuntimeException {
        return userAccountStub.editUser(EditUserRequest.newBuilder()
                .setUserId(userId)
                .setFirstName(firstName)
                .setLastName(lastName)
                .setNickname(nickname)
                .setBio(bio)
                .setPersonalPronouns(pronouns)
                .setEmail(email)
                .build());
    }

    /**
     * Get user account with id
     * @param id The id of the account to get
     * @return Response containing user info
     * @throws StatusRuntimeException
     */
    public UserResponse getUser(int id) throws StatusRuntimeException {
        return userAccountStub.getUserAccountById(GetUserByIdRequest.newBuilder().setId(id).build());
    }


    /**
     * Request a list of users from the IDP server and prepares them for use
     * @param page The page to retrive, indexing starts at 0
     * @param limit The number of users to retrieve, 0 for no limit
     * @param order The field that the results are ordered by
     * @param isAsc If the results are in accending or decending order
     * @return List of unpackaged users
     * @throws StatusRuntimeException
     */
    public PaginatedUsersResponse getUsers(int page, int limit, UserField order, boolean isAsc) throws StatusRuntimeException{
        return userAccountStub.getPaginatedUsers(GetPaginatedUsersRequest.newBuilder().setOrderBy(order.value).setIsAscendingOrder(isAsc).setLimit(limit).setOffset(page).build());
    }

    /**
     * Gets currently logged-in user's account
     * @param principal The security principal of the currently logged-in user
     * @return Response containing user info
     */
    public UserResponse getUser(AuthState principal) {
        int id = Integer.parseInt(principal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("nameid"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("-100"));

        return userAccountStub.getUserAccountById(GetUserByIdRequest.newBuilder().setId(id).build());
    }

    /**
     * Deletes the selected users profile picture
     * @param userId The id of the user
     * @return
     */
    public DeleteUserProfilePhotoResponse deleteUserProfilePhoto(int userId) {
        return userAccountStub.deleteUserProfilePhoto(DeleteUserProfilePhotoRequest.newBuilder().setUserId(userId).build());
    }

    /**
     * Get the current user (principal) roles and returns them as a list.
     * @param principal - current user detail.
     * @return List of current user roles.
     */
    public List<String> getUserRole(AuthState principal) {
        return Arrays.asList(principal.getClaimsList().stream()
        .filter(claim -> claim.getType().equals("role")).findFirst().map(ClaimDTO::getValue).orElse("NOT FOUND").split(","));
    }

    /**
     * Check whether the current user has a teacher or course administrator role.
     * @param principal - current user
     * @return true if the user has role teacher or course administrator
     */
    public boolean checkUserIsTeacherOrAdmin(@AuthenticationPrincipal AuthState principal) {
        List<String> userRoles = Arrays.asList(principal.getClaimsList().stream().filter(claim -> claim.getType().equals("role")).findFirst().map(ClaimDTO::getValue).orElse("NOT FOUND").split(","));
        return(userRoles.contains(UserRole.TEACHER.name()) || userRoles.contains(UserRole.COURSE_ADMINISTRATOR.name()));
    }

    /**
     * Sends an upload image request to the server
     * @param id Id of user (required)
     * @param ext Extension of the image the user has uploaded (required)
     * @param file File of the image (required)
     * @throws IOException Failure to upload the image
     */
    public void uploadImage(final int id, final String ext, final MultipartFile file) throws IOException {
        StreamObserver<UploadUserProfilePhotoRequest> streamObserver = this.userAccountServiceStub.uploadUserProfilePhoto(new UserAccountClientService.FileUploadObserver());

        UploadUserProfilePhotoRequest metadata = (UploadUserProfilePhotoRequest.newBuilder()
                .setMetaData(ProfilePhotoUploadMetadata.newBuilder()
                        .setUserId(id)
                        .setFileType(ext).build())
                .build());

        streamObserver.onNext(metadata);

        InputStream inputStream = file.getInputStream();
        byte[] bytes = new byte[4096];
        int size;
        while((size = inputStream.read(bytes)) > 0){
            UploadUserProfilePhotoRequest uploadImage = UploadUserProfilePhotoRequest.newBuilder()
                    .setFileContent(ByteString.copyFrom(bytes,0,size))
                    .build();
            streamObserver.onNext(uploadImage);
        }

        inputStream.close();
        streamObserver.onCompleted();
    }
    /**
     * Returns a status update when the file upload is complete using protos
     */
    private static class FileUploadObserver implements StreamObserver<FileUploadStatusResponse>{
        @Override
        public void onNext(FileUploadStatusResponse fileUploadStatusResponse) {
          // Does not make any changes depending on response as error and complete are already handled
        }

        @Override
        public void onError(Throwable throwable){
            logger.error("Profile image uploaded", throwable);
        }

        @Override
        public void onCompleted(){
            logger.info("Profile image uploaded");
        }
    }

    /**
     * Sends a ModifyRoleOfUserRequest to delete a user's role, returns the success or failure of the request
     * @param userId ID of the user whose role is being deleted
     * @param role The role being deleted
     * @return A UserRoleChangeResponse object containing success or failure of the request
     */
    public UserRoleChangeResponse removeUserRole(int userId, UserRole role) {
        return userAccountStub.removeRoleFromUser(
                ModifyRoleOfUserRequest.newBuilder().setRole(role).setUserId(userId).build());
    }

    /**
     * Sends a ModifyRoleOfUserRequest to add a user's role, returns the success or failure of the request
     * @param userId ID of the user whose role is being deleted
     * @param role The role being added
     * @return A UserRoleChangeResponse object containing success or failure of the request
     */
    public UserRoleChangeResponse addRoleToUser(int userId, UserRole role) {
        return userAccountStub.addRoleToUser(
            ModifyRoleOfUserRequest.newBuilder().setUserId(userId).setRole(role).build());
    }
}
