package nz.ac.canterbury.seng302.portfolio.authentication;

import io.grpc.StatusRuntimeException;
import nz.ac.canterbury.seng302.portfolio.service.AuthenticateClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthenticateResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private AuthenticateClientService authenticateClientService;
    private static final String COOKIE_NAME = "lens-session-token";

    private AuthenticateClientService getAuthenticateClientService(HttpServletRequest request) {
        if(authenticateClientService == null){
            ServletContext servletContext = request.getServletContext();
            WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
            if (webApplicationContext == null) {
                return null;
            }
            authenticateClientService = webApplicationContext.getBean(AuthenticateClientService.class);
        }
        return authenticateClientService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        PreAuthenticatedAuthenticationToken authentication = getAuthentication(req, null);
        
        if(!authentication.isAuthenticated()) {
            authenticateClientService = getAuthenticateClientService(req);
            AuthenticateResponse reply;
            if (authenticateClientService != null && (reply = authenticateClientService.reAuthenticate()) != null && reply.getSuccess()) {
                var domain = req.getHeader("host");
                Cookie newCookie = CookieUtil.create(
                    res,
                    COOKIE_NAME,
                    reply.getToken(),
                    true,
                    5 * 60 * 60, // Expires in 5 hours
                    domain.startsWith("localhost") ? null : domain
                );
                authentication = getAuthentication(req, newCookie.getValue());
            } else {
                CookieUtil.clear(res, COOKIE_NAME);
            }
        }
            
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }

    /**
     * Check with the IdP whether the user making this request is authenticated, and set the authState returned
     * as our authentication principal. This allows us to access the authState (including name, roles, id, etc.)
     * in any of our controllers just by adding an @AuthenticationPrincipal parameter.
     *
     * @param request HTTP request sent by client
     * @param lensSessionCookieJwtString lensfolio cookie string
     * @return PreAuth token with the authState of user, and whether they are authenticated
     */
    private PreAuthenticatedAuthenticationToken getAuthentication(HttpServletRequest request, String lensSessionCookieJwtString) {
        // Create an auth token for an unauthenticated user
        PreAuthenticatedAuthenticationToken authToken = new PreAuthenticatedAuthenticationToken(null, null);
        authToken.setAuthenticated(false);

        if (lensSessionCookieJwtString == null) {
            lensSessionCookieJwtString = CookieUtil.getValue(request, COOKIE_NAME);
        }
        if (!StringUtils.hasText(lensSessionCookieJwtString)) {
            // No cookie with jwt session token found, return unauthenticated token
            return authToken;
        }

        AuthState authState;
        try {
            AuthenticateClientService  authenticateClientServiceRequest = getAuthenticateClientService(request);
            if (authenticateClientServiceRequest == null) {
                return authToken;
            }
            authState = authenticateClientServiceRequest.checkAuthState();
        } catch (StatusRuntimeException e) {
            // This exception is thrown if the IdP encounters some error, or if the IdP can not be reached
            // Also may be thrown if some error connecting to IdP, either way, return unauthenticated token
            return authToken;
        }

        // If we get here, then the IdP has returned 'some' auth state, so we configure our auth token with whatever
        // the IdP has said about the authentication status of the user that provided this token
        authToken = new PreAuthenticatedAuthenticationToken(authState, lensSessionCookieJwtString);
        authToken.setAuthenticated(authState.getIsAuthenticated());
        return authToken;
    }
}
