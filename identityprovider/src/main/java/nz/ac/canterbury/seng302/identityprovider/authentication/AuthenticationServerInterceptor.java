package nz.ac.canterbury.seng302.identityprovider.authentication;

import org.springframework.beans.factory.annotation.Autowired;

import io.grpc.*;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import nz.ac.canterbury.seng302.identityprovider.model.User;
import nz.ac.canterbury.seng302.identityprovider.model.UserRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;

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
        User user;
        try {
            user = userRepository.getUserByUsername(jwtTokenUtil.getUsernameFromToken(bearerStrippedSessionToken));
        } catch (SignatureException | MalformedJwtException e){
            user = null;
        }
        AuthState authState = AuthenticationValidatorUtil.validateTokenForAuthState(bearerStrippedSessionToken, user);

        Context context = Context.current()
                .withValue(SESSION_TOKEN, sessionToken)
                .withValue(AUTH_STATE, authState);

        return Contexts.interceptCall(context, call, headers, next);
    }
}
