package nz.ac.canterbury.seng302.portfolio.utils;

import java.security.Principal;

/**
 * New principal to override the authstate one that is being used and provide a name for the user
 */
public class WebSocketPrincipal implements Principal{

    Principal authState = null;

    public WebSocketPrincipal(Principal authState) {
        this.authState = authState;
    }

    @Override
    public String getName() {
        return "User";
    }

    /**
     * Gets the authstate principal that this is replacing
     * @return
     */
    public Principal getPrincipal(){
        return authState;
    }

}
