package nz.ac.canterbury.seng302.portfolio.service;

import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.springframework.stereotype.Service;

/**
 * Client service used to communicate to the IDP application relating to user account features
 */
@Service
public class UserAccountClientService {

    @GrpcClient("identity-provider-grpc-server")
    private UserAccountServiceGrpc.UserAccountServiceBlockingStub userAccountStub;

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
}