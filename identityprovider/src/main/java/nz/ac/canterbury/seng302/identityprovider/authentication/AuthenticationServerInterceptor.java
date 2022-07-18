package nz.ac.canterbury.seng302.identityprovider.authentication;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import io.grpc.*;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import nz.ac.canterbury.seng302.identityprovider.model.UserRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;

@GrpcGlobalServerInterceptor
public class AuthenticationServerInterceptor implements ServerInterceptor {
    JwtTokenUtil jwtTokenUtil = JwtTokenUtil.getInstance();

    private final Metadata.Key<String> sessionTokenHeaderKey = Metadata.Key.of("X-Authorization", Metadata.ASCII_STRING_MARSHALLER);

    public static final Context.Key<String> SESSION_TOKEN = Context.key("lens-session-token");
    public static final Context.Key<AuthState> AUTH_STATE = Context.key("auth-state");

    @Autowired
    UserRepository userRepository;

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next
    ) {
        JwtTokenUtil jwtTokenUtil = JwtTokenUtil.getInstance();
        String sessionToken = headers.get(sessionTokenHeaderKey);
        String bearerStrippedSessionToken = sessionToken != null ? sessionToken.replaceFirst("Bearer ", "") : "";
        List<UserRole> userRoles;
        try {
            userRoles = userRepository.getUserByUsername(jwtTokenUtil.getUsernameFromToken(bearerStrippedSessionToken)).getRoles();
        } catch (SignatureException | MalformedJwtException e){
            userRoles = null;
        }
        AuthState authState = AuthenticationValidatorUtil.validateTokenForAuthState(bearerStrippedSessionToken, userRoles);

        Context context = Context.current()
                .withValue(SESSION_TOKEN, sessionToken)
                .withValue(AUTH_STATE, authState);

        return Contexts.interceptCall(context, call, headers, next);
    }
}