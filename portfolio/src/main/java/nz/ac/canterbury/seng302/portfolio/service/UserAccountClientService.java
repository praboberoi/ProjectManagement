package nz.ac.canterbury.seng302.portfolio.service;

import com.google.protobuf.ByteString;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import nz.ac.canterbury.seng302.portfolio.model.User;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import nz.ac.canterbury.seng302.shared.util.FileUploadStatus;
import nz.ac.canterbury.seng302.shared.util.FileUploadStatusResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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
        UserRegisterResponse response = userAccountStub.register(UserRegisterRequest.newBuilder()
                .setUsername(username)
                .setFirstName(firstName)
                .setLastName(lastName)
                .setNickname(nickname)
                .setBio(bio)
                .setPersonalPronouns(pronouns)
                .setEmail(email)
                .setPassword(password)
                .build());
        return response;
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
        EditUserResponse response = userAccountStub.editUser(EditUserRequest.newBuilder()
                .setUserId(userId)
                .setFirstName(firstName)
                .setLastName(lastName)
                .setNickname(nickname)
                .setBio(bio)
                .setPersonalPronouns(pronouns)
                .setEmail(email)
                .build());
        return response;
    }

    /**
     * Get user account with id
     * @param id The id of the account to get
     * @return Response containing user info
     * @throws StatusRuntimeException
     */
    public UserResponse getUser(int id) throws StatusRuntimeException {
        UserResponse response = userAccountStub.getUserAccountById(GetUserByIdRequest.newBuilder().setId(id).build());
        return response;
    }


    /**
     * Request a list of users from the IDP server and prepares them for use
     * @param page The page to retrive, indexing starts at 0
     * @param limit The number of users to retrieve, 0 for no limit
     * @return List of unpackaged users
     * @throws StatusRuntimeException
     */
    public PaginatedUsersResponse getUsers(int page, int limit) throws StatusRuntimeException{
        PaginatedUsersResponse response = userAccountStub.getPaginatedUsers(GetPaginatedUsersRequest.newBuilder().setLimit(limit).setOffset(page).build());
        return response;
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

        UserResponse response = userAccountStub.getUserAccountById(GetUserByIdRequest.newBuilder().setId(id).build());
        return response;
    }

    /**
     * Get the current user (principal) roles and returns them as a list.
     * @param principal - current user detail.
     * @return List of current user roles.
     */
    public List<String> getUserRole(AuthState principal) {
        List<String> userRoles = Arrays.asList(principal.getClaimsList().stream()
        .filter(claim -> claim.getType().equals("role")).findFirst().map(ClaimDTO::getValue).orElse("NOT FOUND").split(","));
        return userRoles;
    }

    /**
     * Check whether the current user has a teacher or course administrator role.
     * @param principal - current user
     * @return true if the user has role teacher or course administrator
     */
    public boolean checkUserIsTeacherOrAdmin(@AuthenticationPrincipal AuthState principal) {
        List<String> userRoles = Arrays.asList(principal.getClaimsList().stream().filter(claim -> claim.getType().equals("role")).findFirst().map(ClaimDTO::getValue).orElse("NOT FOUND").split(","));
        if ((userRoles.contains(UserRole.TEACHER.name()) || userRoles.contains(UserRole.COURSE_ADMINISTRATOR.name()))) {
            return true;
        }
        return false;
    }




    /**
     * This method gets data about a User Profile image from a StreamObserver and returns
     * a StreamObserver about the FileUploadStatus response.
     * @param responseObserver The request containing image information
     * @return The response observer records the result of the operation
     */
    public StreamObserver<UploadUserProfilePhotoRequest> getImage(StreamObserver<FileUploadStatusResponse> responseObserver){
        return new StreamObserver<>() {

        OutputStream writer;
            FileUploadStatus status = FileUploadStatus.IN_PROGRESS;
            User user;
            Path path;

            /**
             * Sets the OutputStream on the first request and then sends
             * the data to be saved on subsequent requests
             *
             * @param request Request containing image information
             */
            @Override
            public void onNext(UploadUserProfilePhotoRequest request) {
                try {
                    if (request.hasMetaData()) {
                        path = getFilePath(request);
                        writer = Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
//                    user = userRepository.getUserByUserId(request.getMetaData().getUserId());
                    } else {
                        writeFile(writer, request.getFileContent());
                    }
                } catch (IOException e) {
                    this.onError(e);
                }
            }

            /**
             * Updates file upload status
             *
             * @param t the error occurred on the stream
             */
            @Override
            public void onError(Throwable t) {
                status = FileUploadStatus.FAILED;
                this.onCompleted();
            }

            /**
             * Compiles file upload status response
             */
            @Override
            public void onCompleted() {
                closeFile(writer);
                status = FileUploadStatus.IN_PROGRESS.equals(status) ? FileUploadStatus.SUCCESS : status;
                FileUploadStatusResponse response = FileUploadStatusResponse.newBuilder()
                        .setStatus(status)
                        .setMessage("File upload progress: " + status)
                        .build();
                user.setProfileImagePath(String.valueOf(path));
//            userRepository.save(user);
                responseObserver.onNext(response);
                responseObserver.onCompleted();

            }
        };

    }


    /**
     * Writes image file to profilePhotos
     * @param writer OutputStream containing pathway
     * @param content Image content
     */
    private void writeFile(OutputStream writer, ByteString content) throws IOException {
        writer.write(content.toByteArray());
        writer.flush();
    }

    /**
     * Creates file name and writer
     * @param request Contains image information
     * @return Writer containing information to save file
     */
    private Path getFilePath(UploadUserProfilePhotoRequest request) throws IOException {
        Path SERVER_BASE_PATH = Paths.get("portfolio/src/main/resources/static/cachedprofilephoto");
        var fileName = "UserProfile" + request.getMetaData().getUserId() + "." + request.getMetaData().getFileType();
        return SERVER_BASE_PATH.resolve(fileName);
    }

    /**
     * Closes OutputStream
     * @param writer The OutputStream
     */
    private void closeFile(OutputStream writer){
        try {
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * Sends an upload image request to the server
     * @param id Id of user (required)
     * @param ext Extension of the image the user has uploaded (required)
     * @param file File of the image (required)
     * @throws IOException Failure to upload the image
     */
    public void uploadImage(final int id, final String ext, final MultipartFile file) throws IOException {
        // request observer from UserAccountServiceGrpc
        StreamObserver<UploadUserProfilePhotoRequest> streamObserver = this.userAccountServiceStub.uploadUserProfilePhoto(new UserAccountClientService.FileUploadObserver());

        // build metadata from proto in user_accounts.proto
        UploadUserProfilePhotoRequest metadata = (UploadUserProfilePhotoRequest.newBuilder()
                .setMetaData(ProfilePhotoUploadMetadata.newBuilder()
                        .setUserId(id)
                        .setFileType(ext).build())
                .build());

        streamObserver.onNext(metadata);

        // upload file in chunks and upload as a stream
//        InputStream inputStream = Files.newInputStream(path);
        InputStream inputStream = file.getInputStream();
        byte[] bytes = new byte[4096];
        int size;
        while((size = inputStream.read(bytes)) > 0){
            UploadUserProfilePhotoRequest uploadImage = UploadUserProfilePhotoRequest.newBuilder()
                    .setFileContent(ByteString.copyFrom(bytes,0,size))
                    .build();
            streamObserver.onNext(uploadImage);
        }
        // close stream
        inputStream.close();
        streamObserver.onCompleted();
        // write image to portfolio when image has been successfully uploaded
        Path path = Paths.get("portfolio/src/main/resources/static/cachedprofilephoto/" + "UserProfile" + id + "." + ext);
        Files.write(path, file.getBytes());

    }
    /**
     * Returns a status update when the file upload is complete using protos
     */
    private static class FileUploadObserver implements StreamObserver<FileUploadStatusResponse>{
        @Override
        public void onNext(FileUploadStatusResponse fileUploadStatusResponse) {
            System.out.println("File upload status" + fileUploadStatusResponse.getStatus());
        }

        @Override
        public void onError(Throwable throwable){

        }

        @Override
        public void onCompleted(){

        }
    }

    /**
     * Sends a ModifyRoleOfUserRequest to delete a users role, returns the success or failure of the request
     * @param userId ID of the user whose role is being deleted
     * @param role The role being deleted
     * @return A UserRoleChangeResponse object containing success or failure of the request
     */
    public UserRoleChangeResponse removeUserRole(int userId, UserRole role) {
        UserRoleChangeResponse response = userAccountStub.removeRoleFromUser(
                ModifyRoleOfUserRequest.newBuilder().setRole(role).setUserId(userId).build());
        return response;
    }

}