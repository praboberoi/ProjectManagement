package nz.ac.canterbury.seng302.portfolio.authentication;

import nz.ac.canterbury.seng302.portfolio.utils.WebSocketPrincipal;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

/**
 * Overrides the default websocket handshake to return a custom websocket username 
 */
public class CustomHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        Principal principal = request.getPrincipal();

        principal = new WebSocketPrincipal(principal);

        return principal;
    }

}