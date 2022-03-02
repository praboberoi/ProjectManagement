package nz.ac.canterbury.seng302.identityprovider.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import nz.ac.canterbury.seng302.identityprovider.authentication.AuthenticationServerInterceptor;
import nz.ac.canterbury.seng302.identityprovider.authentication.JwtTokenUtil;
import nz.ac.canterbury.seng302.identityprovider.model.User;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthenticateRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthenticateResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthenticationServiceGrpc.AuthenticationServiceImplBase;

@GrpcService
public class AuthenticateServerService extends AuthenticationServiceImplBase{

    User user = new User(1, "abc123", "Valid", "User", null, null, null, null, "A9r8gjI/EB/S1PIcR03nU/6VhKQnP/LFyWjOlQ6oOJ8=", "FEDFST", new ArrayList<>(Arrays.asList("student")));

    private JwtTokenUtil jwtTokenService = JwtTokenUtil.getInstance();

    /**
     * Attempts to authenticate a user with a given username and password. 
     */
    @Override
    public void authenticate(AuthenticateRequest request, StreamObserver<AuthenticateResponse> responseObserver) {
        AuthenticateResponse.Builder reply = AuthenticateResponse.newBuilder();

        String username = request.getUsername();
        String password = request.getPassword();

        if (username.equals(user.username) && validatePassword(user, password)) {
            String token = jwtTokenService.generateTokenForUser(user.username, user.userId, user.firstName + user.lastName, user.roles);
            reply
                .setEmail("validuser@email.com")
                .setFirstName(user.firstName)
                .setLastName(user.lastName)
                .setMessage("Logged in successfully!")
                .setSuccess(true)
                .setToken(token)
                .setUserId(1)
                .setUsername(user.username);
        } else {
            reply
            .setMessage("Log in attempt failed: username or password incorrect")
            .setSuccess(false)
            .setToken("");
        }

        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }

    /**
     * Validates the given password using a salt and SHA-256 hash
     * @param username
     * @param password
     * @return If the hashed password matches the stored password
     */
    private boolean validatePassword(User user, String password) {
        MessageDigest digest;
        byte[] hashedPassword = null;

		try {
            String salt = user.salt;
			digest = MessageDigest.getInstance("SHA-256");
            hashedPassword = digest.digest((password + salt).getBytes(StandardCharsets.UTF_8));
		} catch (NoSuchAlgorithmException e) {
            System.out.println("Unable to find SHA-256 algorithm");
		}
        
        return Base64.getEncoder().encodeToString(hashedPassword).equals(user.password);
	}



	/**
     * The AuthenticationInterceptor already handles validating the authState for us, so here we just need to
     * retrieve that from the current context and return it in the gRPC body
     */
    @Override
    public void checkAuthState(Empty request, StreamObserver<AuthState> responseObserver) {
        responseObserver.onNext(AuthenticationServerInterceptor.AUTH_STATE.get());
        responseObserver.onCompleted();
    }
}
