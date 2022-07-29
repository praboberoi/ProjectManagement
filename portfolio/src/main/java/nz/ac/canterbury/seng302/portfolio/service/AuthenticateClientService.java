package nz.ac.canterbury.seng302.portfolio.service;

import com.google.protobuf.Empty;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthenticateRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthenticateResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthenticationServiceGrpc;
import org.springframework.stereotype.Service;

@Service
public class AuthenticateClientService {

    @GrpcClient("identity-provider-grpc-server")
    private AuthenticationServiceGrpc.AuthenticationServiceBlockingStub authenticationStub;

    /**
     * Log the user in and generate a token
     * @param username The user's username
     * @param password The user's password
     * @return
     */
    public AuthenticateResponse authenticate(final String username, final String password)  {
        AuthenticateRequest authRequest = AuthenticateRequest.newBuilder()
                .setUsername(username)
                .setPassword(password)
                .build();
        return authenticationStub.authenticate(authRequest);
    }

    /**
     * Run authenticate with empty body to try get a new user token
     */
    public AuthenticateResponse reAuthenticate()  {
        try {
            AuthenticateRequest authRequest = AuthenticateRequest.newBuilder().build();
            return authenticationStub.authenticate(authRequest);
        } catch (StatusRuntimeException e) {
            return null;
        }
    }

    /**
     * Checks if the AuthState of the user is valid 
     * @return An AuthState object
     * @throws StatusRuntimeException
     */
    public AuthState checkAuthState() throws StatusRuntimeException {
        return authenticationStub.checkAuthState(Empty.newBuilder().build());
    }

}
