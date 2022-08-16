package nz.ac.canterbury.seng302.identityprovider.service;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import net.devh.boot.grpc.server.service.GrpcService;
import nz.ac.canterbury.seng302.identityprovider.authentication.AuthenticationServerInterceptor;
import nz.ac.canterbury.seng302.identityprovider.authentication.JwtTokenUtil;
import nz.ac.canterbury.seng302.identityprovider.model.User;
import nz.ac.canterbury.seng302.identityprovider.model.UserRepository;
import nz.ac.canterbury.seng302.identityprovider.util.EncryptionUtilities;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthenticateRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthenticateResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthenticationServiceGrpc.AuthenticationServiceImplBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Grpc service used to authenticate the user.
 */
@GrpcService
public class AuthenticateServerService extends AuthenticationServiceImplBase{

    private final UserRepository userRepository;
    private Logger logger = LoggerFactory.getLogger(AuthenticateServerService.class);

    public AuthenticateServerService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private final JwtTokenUtil jwtTokenService = JwtTokenUtil.getInstance();

    /**
     * Attempts to authenticate a user with a given username and password. 
     */
    @Override
    public void authenticate(AuthenticateRequest request, StreamObserver<AuthenticateResponse> responseObserver) {
        AuthenticateResponse.Builder reply = AuthenticateResponse.newBuilder();
        String username = request.getUsername();
        String password = request.getPassword();
        String sessionToken = AuthenticationServerInterceptor.SESSION_TOKEN.get();
        String token = sessionToken != null ? sessionToken.replaceFirst("Bearer ", "") : "";
        if (!token.equals("null") && username.equals("") && password.equals("")) {
            try {
                username = jwtTokenService.getUsernameFromToken(token);
            } catch (SignatureException | MalformedJwtException e) {
                username = "";
            }
            
            logger.debug("Attempting to re-athenticate user {}", username);
        }

        User user = userRepository.getUserByUsername(username);
        if ((request.getUsername().equals("") && jwtTokenService.validateToken(token)) ||
         (user != null && !username.equals("") 
            && username.equals(user.getUsername()) 
            && EncryptionUtilities.encryptPassword(user.getSalt(), password).equals(user.getPassword()))) {
        token = jwtTokenService.generateTokenForUser(user.getUsername(), user.getUserId(), user.getFirstName() + user.getLastName(), user.getRoles());
            reply
                .setEmail(user.getEmail())
                .setFirstName(user.getFirstName())
                .setLastName(user.getLastName())
                .setMessage("Logged in successfully!")
                .setSuccess(true)
                .setToken(token)
                .setUserId(user.getUserId())
                .setUsername(user.getUsername());
        } else {
            reply
            .setMessage("Log in failed: incorrect username or password")
            .setSuccess(false)
            .setToken("");
        }

        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
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
