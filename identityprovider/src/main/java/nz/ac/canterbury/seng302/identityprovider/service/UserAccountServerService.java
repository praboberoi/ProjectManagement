package nz.ac.canterbury.seng302.identityprovider.service;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.google.protobuf.ByteString;
import com.google.protobuf.Timestamp;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import nz.ac.canterbury.seng302.identityprovider.model.User;
import nz.ac.canterbury.seng302.identityprovider.model.UserRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import nz.ac.canterbury.seng302.shared.util.FileUploadStatus;
import nz.ac.canterbury.seng302.shared.util.FileUploadStatusResponse;
import nz.ac.canterbury.seng302.shared.util.ValidationError;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.NoSuchElementException;

@GrpcService
public class UserAccountServerService extends UserAccountServiceGrpc.UserAccountServiceImplBase {

    private final UserRepository userRepository;

    public UserAccountServerService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * This method registers a user in the database based off a UserRegisterRequest, and uses the StreamObserver to
     * contain it's UserRegisterResponse as the result of the operation
     *
     * @param request          The request containing details of the user to be registered
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
            errorBuilder.setErrorText("Username must unique.");
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
     *
     * @param request          A proto containing all the details of the user to be edited, such as the userId, first name etc
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
     *
     * @param request          Request containing the users id
     * @param responseObserver Returns to previous method with data
     */
    @Override
    public void getUserAccountById(GetUserByIdRequest request, StreamObserver<UserResponse> responseObserver) throws NoSuchElementException {
        UserResponse.Builder reply = UserResponse.newBuilder();
        User user = userRepository.getUserByUserId(request.getId());
        if (user == null) {
            throw new NoSuchElementException("User doesn't exist");
        }
        reply.setUsername(user.getUsername());
        reply.setFirstName(user.getFirstName());
        reply.setLastName(user.getLastName());
        reply.setNickname(user.getNickname());
        reply.setPersonalPronouns(user.getPersonalPronouns());
        reply.setBio(user.getBio());
        reply.setEmail(user.getEmail());
        reply.addAllRoles(user.getRoles());
        reply.setCreated(Timestamp.newBuilder()
                .setSeconds(user.getDateCreated().getTime())
                .build());

//        reply.setProfileImagePath("icons/userr.png");
//        if(user.getProfileImagePath() == ""){
//            reply.setProfileImagePath("icons/userr.png");
//        } else {
//            reply.setProfileImagePath(user.getProfileImagePath());
//        }

        responseObserver.onNext(reply.build());
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

                        File f = new File(String.valueOf(path));
                        if (f.exists()) {
                            try {
                                Files.delete(path);
                            } catch (IOException e) {
                                this.onError(e);
                            }
                        }

                        writer = Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                        user = userRepository.getUserByUserId(request.getMetaData().getUserId());
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
                userRepository.save(user);
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
        Path SERVER_BASE_PATH = Paths.get("identityprovider/src/main/resources/profilePhotos");
        var fileName = request.getMetaData().getUserId() + "." + request.getMetaData().getFileType();
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






}
