package nz.ac.canterbury.seng302.identityprovider.service;

import com.google.protobuf.ByteString;
import com.google.protobuf.Timestamp;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import nz.ac.canterbury.seng302.identityprovider.model.User;
import nz.ac.canterbury.seng302.identityprovider.model.UserRepository;
import nz.ac.canterbury.seng302.identityprovider.util.ResponseUtils;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import nz.ac.canterbury.seng302.shared.util.FileUploadStatus;
import nz.ac.canterbury.seng302.shared.util.FileUploadStatusResponse;
import nz.ac.canterbury.seng302.shared.util.ValidationError;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.h2.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
/**
 * Grpc service used to perform function relating to users. This includes registration, editing and retriving User objects
 * stored in the server.
 */
@GrpcService
public class UserAccountServerService extends UserAccountServiceGrpc.UserAccountServiceImplBase {

    private final UserRepository userRepository;

    private final static Path FILE_PATH_ROOT = Paths.get("./profilePhotos/");
    private final Logger logger = LoggerFactory.getLogger(UserAccountServerService.class);

    @Value("${hostAddress}")
    private String hostAddress;

    public UserAccountServerService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * This method registers a user in the database based off a UserRegisterRequest, and uses the StreamObserver to
     * contain it's UserRegisterResponse as the result of the operation
     * @param request The request containing details of the user to be registered
     * @param responseObserver The response observer records the result of the operation
     */
    @Override
    public void register(UserRegisterRequest request, StreamObserver<UserRegisterResponse> responseObserver) {
        RegistrationService controller = new RegistrationService(userRepository);
        UserRegisterResponse.Builder reply = UserRegisterResponse.newBuilder();
        reply.addAllValidationErrors(controller.validateUserDetails(request));
        if (reply.getValidationErrorsCount() != 0) {
            reply.setIsSuccess(false);
            reply.setMessage("Registration validation failed");
            responseObserver.onNext(reply.build());
            responseObserver.onCompleted();
            return;
        }

        if (userRepository.getUserByUsername(request.getUsername()) != null) {
            ValidationError.Builder errorBuilder = ValidationError.newBuilder();
            errorBuilder.setFieldName("usernameError");
            errorBuilder.setErrorText("Username must be unique.");
            reply.addValidationErrors(errorBuilder);
            reply.setIsSuccess(false);
            reply.setMessage("User already exists");
            responseObserver.onNext(reply.build());
            responseObserver.onCompleted();
            return;
        }

        User user = controller.createUser(request);
        if (user != null) {
            reply.setIsSuccess(true);
            reply.setNewUserId(user.getUserId());
            reply.setMessage(String.format(
                    "User %s has been created successfully",
                    user.getUsername()
            ));
            responseObserver.onNext(reply.build());
            responseObserver.onCompleted();
            return;
        }

        reply.setIsSuccess(false);
        reply.setMessage("User failed to create");
        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }

    /**
     * This method builds a user based off an EditUserRequest, first it checks that the user exists in the database,
     * if the user from the edit request exists, it saves the new details to the user from the request and saves the
     * user back into the repository.
     * @param request A proto containing all the details of the user to be edited, such as the userId, first name etc
     * @param responseObserver The streamObserver contains the edit user response which will be passed back down the line with details of the operation
     */
    @Override
    public void editUser(EditUserRequest request, StreamObserver<EditUserResponse> responseObserver) {
        EditUserResponse.Builder reply = EditUserResponse.newBuilder();
        User user = userRepository.getUserByUserId(request.getUserId());
        EditUserAccountService controller = new EditUserAccountService();
        reply.addAllValidationErrors(controller.validateUserDetails(request));
        if (reply.getValidationErrorsCount() != 0) {
            reply.setIsSuccess(false);
            reply.setMessage("Edit validation failed");
            responseObserver.onNext(reply.build());
            responseObserver.onCompleted();
            return;
        }
        if (user == null) {
            reply.setIsSuccess(false);
            reply.setMessage("User cannot be found in database");
            responseObserver.onNext(reply.build());
            responseObserver.onCompleted();
            return;
        }
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setNickname(request.getNickname());
        user.setBio(request.getBio());
        user.setPersonalPronouns(request.getPersonalPronouns());
        user.setEmail(request.getEmail());
        userRepository.save(user);

        reply.setIsSuccess(true);
        reply.setMessage(String.format("User with id %s has been edited successfully", user.getUserId()));
        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }

    /**
     * Gets a user object from the db and sets the template elements with the user's details
     * @param request Request containing the users id
     * @param responseObserver Returns to previous method with data
     */
    @Override
    public void getUserAccountById(GetUserByIdRequest request, StreamObserver<UserResponse> responseObserver) throws NoSuchElementException {
        User user = userRepository.getUserByUserId(request.getId());
        if (user == null) {
            throw new NoSuchElementException("User doesn't exist");
        }

        UserResponse reply = ResponseUtils.prepareUserResponse(user, hostAddress);

        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    /**
     * This method gets data about a User Profile image from a StreamObserver and returns
     * a StreamObserver about the FileUploadStatus response. It sends the image data to UserProfilePhotoService
     * to be saved.
     * @param responseObserver The request containing image information
     * @return The response observer records the result of the operation
     */
    @Override
    public StreamObserver<UploadUserProfilePhotoRequest> uploadUserProfilePhoto(StreamObserver<FileUploadStatusResponse> responseObserver) {
        return new StreamObserver<>() {
            OutputStream writer;
            FileUploadStatus status = FileUploadStatus.IN_PROGRESS;
            User user;
            String fileName;
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
                        fileName = "UserProfile" + request.getMetaData().getUserId() + "." + request.getMetaData().getFileType();
                        path = FILE_PATH_ROOT.resolve(fileName);
                        user = userRepository.getUserByUserId(request.getMetaData().getUserId());
                        new File(String.valueOf(FILE_PATH_ROOT)).mkdirs();
                        File f = new File(String.valueOf(path));
                        if (f.exists()) {
                            try {
                                Files.delete(path);
                            } catch (IOException e) {
                                this.onError(e);
                            }
                        }
                        writer = Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
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
                logger.error("An error occured while uploading the users profile photo", t);
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

                user.setProfileImagePath(String.valueOf(fileName));
                userRepository.save(user);
                logger.info("User {}'s profile photo has been saved to {}", user.getUserId(), fileName);
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
     * Closes OutputStream
     * @param writer The OutputStream
     */
    private void closeFile(OutputStream writer){
        try {
            writer.close();
        } catch (Exception e) {
            logger.error("An error occured while closing the writer", e);
        }
    }


    /**
     * Deletes a user's profile photo
     * @param request Request containing the users id
     * @param responseObserver Returns to previous method with data
     */
    @Override
    public void deleteUserProfilePhoto(DeleteUserProfilePhotoRequest request, StreamObserver<DeleteUserProfilePhotoResponse> responseObserver) {
        try {
            User user = userRepository.getUserByUserId(request.getUserId());
            if (!StringUtils.isNullOrEmpty(user.getProfileImagePath())) {
                String fileName = user.getProfileImagePath();
                Path path = FILE_PATH_ROOT.resolve(fileName);
                Files.deleteIfExists(path);

                user.setProfileImagePath(null);
                userRepository.save(user);

                responseObserver.onNext(DeleteUserProfilePhotoResponse.newBuilder().setIsSuccess(true).build());
                responseObserver.onCompleted();
            } else {
                responseObserver.onNext(DeleteUserProfilePhotoResponse.newBuilder()
                        .setIsSuccess(false)
                        .build());
                responseObserver.onCompleted();
            }

        } catch (IOException e) {
            responseObserver.onError(e);
        } catch (Exception e) {
            responseObserver.onError(e);
        }

    }

    /**
     * Uses the request to add the new role to the user.
     * If the user is not found the response sets a message and success as false, and if the addition is successful, it sets the response success as true.
     * @param request :contains the id of the user to add the role to, and the role to add to the user.
     * @param responseObserver: set success true if added role to user, else false.
     */
    public void addRoleToUser(ModifyRoleOfUserRequest request, StreamObserver<UserRoleChangeResponse> responseObserver) {
        User user = userRepository.getUserByUserId(request.getUserId());
        UserRoleChangeResponse.Builder userRoleChangeResponse = UserRoleChangeResponse.newBuilder();

        if (user == null) {
            userRoleChangeResponse.setIsSuccess(false);
            userRoleChangeResponse.setMessage("User could not be found.");
            responseObserver.onNext(userRoleChangeResponse.build());
            responseObserver.onCompleted();
            return;
        }

        if (user.getRoles().contains(request.getRole())) {
            userRoleChangeResponse.setIsSuccess(false);
            userRoleChangeResponse.setMessage("User already has this role.");
            responseObserver.onNext(userRoleChangeResponse.build());
            responseObserver.onCompleted();
            return;
        }

        user.addRole(request.getRole());
        userRepository.save(user);
        userRoleChangeResponse.setIsSuccess(true);
        userRoleChangeResponse.setMessage(String.format("User with id %s has had role %s added successfully", user.getUserId(), request.getRole()));
        responseObserver.onNext(userRoleChangeResponse.build());
        responseObserver.onCompleted();
    }


    /**
     * Uses the request to remove a role from the user.
     * If the user is not found the response sets a message and success as false, and if the removal is successful, it sets the response success as true.
     * @param request :contains the id of the user to remove the role from, and the role to remove from the user.
     * @param responseObserver: set success true if removed role from user, else false.
     */
    public void removeRoleFromUser(ModifyRoleOfUserRequest request, StreamObserver<UserRoleChangeResponse> responseObserver) {
        User user = userRepository.getUserByUserId(request.getUserId());
        UserRoleChangeResponse.Builder userRoleChangeResponse = UserRoleChangeResponse.newBuilder();

        user.removeRole(request.getRole());
        userRepository.save(user);
        userRoleChangeResponse.setIsSuccess(true);
        userRoleChangeResponse.setMessage(String.format("User with id %s has had role %s removed successfully", user.getUserId(), request.getRole()));
        responseObserver.onNext(userRoleChangeResponse.build());
        responseObserver.onCompleted();
    }

    /**
     * Gets all users from the db and returns them packaged into a protobuf
     *
     * @param request          Request containing the offset, limit and order of users to get
     * @param responseObserver Returns to previous method with data
     */
    @Override
    public void getPaginatedUsers(GetPaginatedUsersRequest request, StreamObserver<PaginatedUsersResponse> responseObserver) {
        List<User> users;

        long resultSetSize = userRepository.count();
        Sort sort;

        if (request.getOrderBy().isEmpty()) {
            sort = Sort.by(Sort.Order.by("userId").ignoreCase().with(Direction.ASC));
        } else {
            sort = Sort.by(Sort.Order.by(request.getOrderBy()).ignoreCase().with(request.getIsAscendingOrder()? Direction.ASC:Direction.DESC), Sort.Order.by("lastName").ignoreCase().with(request.getIsAscendingOrder()? Direction.ASC:Direction.DESC));
        }
        
        if (request.getLimit() == 0) {
            users = userRepository.findAll(sort);
        } else {
            PageRequest pageable = PageRequest.of(request.getOffset(), request.getLimit(), sort);
            users = userRepository.findAll(pageable).getContent();
        }

        List<UserResponse> preparedUsers = users.stream().map(user -> ResponseUtils.prepareUserResponse(user, hostAddress)).collect(Collectors.toList());

        PaginatedUsersResponse reply = ResponseUtils.preparePaginatedUsersResponse(preparedUsers, resultSetSize);

        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

}
