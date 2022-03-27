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
