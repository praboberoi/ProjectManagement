package nz.ac.canterbury.seng302.portfolio.utils;

import java.security.Principal;


public class WebSocketPrincipal implements Principal{

    Principal authState = null;

    public WebSocketPrincipal(Principal authState) {
        this.authState = authState;
    }

    @Override
    public String getName() {
        return "User";
    }

    public Principal getPrincipal(){
        return authState;
    }

}
