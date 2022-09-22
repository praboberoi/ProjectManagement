package nz.ac.canterbury.seng302.portfolio.utils;

import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.security.Principal;

/**
 * New principal to override the authstate one that is being used and provide a name for the user
 */
public class WebSocketPrincipal implements Principal{

    AuthState authState = null;

    public WebSocketPrincipal(Principal authState) {
        PreAuthenticatedAuthenticationToken auth = (PreAuthenticatedAuthenticationToken) authState;
        this.authState = (AuthState) auth.getPrincipal();
    }

    @Override
    public String getName() {
        return PrincipalUtils.getUserName(authState);
    }

    /**
     * Gets the authstate principal that this is replacing
     * @return
     */
    public AuthState getPrincipal() {
        return authState;
    }

}
