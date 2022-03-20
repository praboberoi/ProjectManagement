package nz.ac.canterbury.seng302.portfolio.service;

import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.springframework.stereotype.Service;

@Service
public class UserAccountClientService {

    @GrpcClient("identity-provider-grpc-server")
    private UserAccountServiceGrpc.UserAccountServiceBlockingStub userAccountStub;

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

    public UserResponse getUser(int id) throws StatusRuntimeException {
        UserResponse response = userAccountStub.getUserAccountById(GetUserByIdRequest.newBuilder().setId(id).build());
        return response;
    }
}
