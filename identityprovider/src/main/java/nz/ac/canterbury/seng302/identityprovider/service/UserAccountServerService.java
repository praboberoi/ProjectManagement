package nz.ac.canterbury.seng302.identityprovider.service;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import nz.ac.canterbury.seng302.identityprovider.model.User;
import nz.ac.canterbury.seng302.identityprovider.model.UserRepository;
import nz.ac.canterbury.seng302.identityprovider.util.EncryptionUtilities;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@GrpcService
public class UserAccountServerService extends UserAccountServiceGrpc.UserAccountServiceImplBase {
    @Autowired private UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserAccountServerService.class);

    @Override
    public void register(UserRegisterRequest request, StreamObserver<UserRegisterResponse> responseObserver) {
        UserRegisterResponse.Builder reply = UserRegisterResponse.newBuilder();
        if (userRepository.getUserByUsername(request.getUsername()) != null) {
            reply.setIsSuccess(false);
            reply.setMessage("User already exists");
            responseObserver.onNext(reply.build());
            responseObserver.onCompleted();
            return;
        }

        String userSalt = EncryptionUtilities.createSalt();
        String userPassword = EncryptionUtilities.encryptPassword(userSalt, request.getPassword());
        User user = userRepository.save(new User(request, userPassword, userSalt));
        reply.setIsSuccess(true);
        reply.setNewUserId(user.getUserId());
        reply.setMessage(String.format(
                "User %s has been created successfully",
                user.getUsername()
        ));

        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getUserAccountById(GetUserByIdRequest request, StreamObserver<UserResponse> responseObserver) {
        UserResponse.Builder reply = UserResponse.newBuilder();
        User user = userRepository.getUserByUserId(request.getId());
        reply.setUsername(user.getUsername());
        reply.setFirstName(user.getFirstName());
        reply.setLastName(user.getLastName());
        reply.setNickname(user.getNickname());
        reply.setPersonalPronouns(user.getPersonalPronouns());
        reply.setBio(user.getBio());
        reply.setEmail(user.getEmail());
        reply.addAllRoles(user.getRoles());
        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }
}
