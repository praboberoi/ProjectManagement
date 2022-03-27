package nz.ac.canterbury.seng302.identityprovider.service;

import com.google.protobuf.Timestamp;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import nz.ac.canterbury.seng302.identityprovider.model.User;
import nz.ac.canterbury.seng302.identityprovider.model.UserRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.NoSuchElementException;

@GrpcService
public class UserAccountServerService extends UserAccountServiceGrpc.UserAccountServiceImplBase {

    private final UserRepository userRepository;

    public UserAccountServerService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private static final Logger logger = LoggerFactory.getLogger(UserAccountServerService.class);

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
        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }
}
