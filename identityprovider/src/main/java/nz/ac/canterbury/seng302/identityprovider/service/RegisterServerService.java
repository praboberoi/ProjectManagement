package nz.ac.canterbury.seng302.identityprovider.service;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import nz.ac.canterbury.seng302.identityprovider.util.EncryptionUtilities;
import nz.ac.canterbury.seng302.shared.enums.Roles;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import nz.ac.canterbury.seng302.shared.projectDAL.Datastore;
import nz.ac.canterbury.seng302.shared.projectDAL.model.User;
import nz.ac.canterbury.seng302.shared.projectDAL.readWrite.UserDAL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

@GrpcService
public class RegisterServerService extends UserAccountServiceGrpc.UserAccountServiceImplBase {

    private static final Logger logger = LoggerFactory.getLogger(RegisterServerService.class);

    @Override
    public void register(UserRegisterRequest request, StreamObserver<UserRegisterResponse> responseObserver) {
        logger.info("register() has been called");
        UserRegisterResponse.Builder reply = UserRegisterResponse.newBuilder();
        Datastore db = new Datastore();
        String userSalt = EncryptionUtilities.createSalt();
        boolean userCreated = false;
        try {
            userCreated = UserDAL.addUser(
                    db,
                    request.getUsername(),
                    request.getFirstName(),
                    request.getLastName(),
                    request.getNickname(),
                    request.getBio(),
                    request.getPersonalPronouns(),
                    request.getEmail(),
                    EncryptionUtilities.encryptPassword(userSalt, request.getPassword()),
                    userSalt,
                    new ArrayList<>(Arrays.asList(Roles.STUDENT)));
        } catch (SQLException e) {
            reply.setIsSuccess(false);
            reply.setMessage("Unable to create user");
        }
        if (userCreated) {

            User user = UserDAL.getUserByUsername(db, request.getUsername());
            reply.setIsSuccess(true);
            reply.setNewUserId(user.userId);
            reply.setMessage(String.format(
                    "User %s has been created successfully",
                    user.username
            ));
        } else {
            reply.setIsSuccess(false);
            reply.setMessage("User already exists");
        }

        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }
}
