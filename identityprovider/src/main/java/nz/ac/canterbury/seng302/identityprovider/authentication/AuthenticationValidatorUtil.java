package nz.ac.canterbury.seng302.identityprovider.authentication;

import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import nz.ac.canterbury.seng302.identityprovider.model.User;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;

public class AuthenticationValidatorUtil {


    /**
     * Consumes the provided session token in the request body. If the session token is invalid, return an
     * 'unauthenticated' auth state. If the token is valid, return some useful information about the user
     * (and some less useful, but ultimately necessary authentication info) that whichever app requested the
     * auth state check can use.
     *
     * Fields like AuthenticationType, NameClaimType, and RoleClaimType are necessary in other technologies
     * for configuring JWT validation and parsing - just leave them intact and forget about them :)
     *
     * @param sessionToken The provided session token to validate
     * @param user The user that is currently logged in
     * @return An AuthState derived from validating the token
     */
    public static AuthState validateTokenForAuthState(String sessionToken, User user) {
        AuthState.Builder reply = AuthState.newBuilder();
        JwtTokenUtil jwtTokenUtil = JwtTokenUtil.getInstance();

        boolean tokenIsValid;
        try {
            tokenIsValid = jwtTokenUtil.validateToken(sessionToken);
            if(!jwtTokenUtil.validateTokenRoles(sessionToken, user == null ? null : user.getRoles())) {
                sessionToken = jwtTokenUtil.generateTokenForUser(user.getUsername(), user.getUserId(), user.getFirstName() + user.getLastName(), user.getRoles());
            }
        } catch (SignatureException | MalformedJwtException e) {
            // A token is given, that was not valid jwt, has been tampered with, or was not signed with they key we are using.
            // Currently, we generate a new signing key every time the IdP is started, so this exception can be expected if a browser
            // is still using a key that was generated by a previous instance of this application.
            tokenIsValid = false;
        }

        if(!tokenIsValid) {
            reply.setIsAuthenticated(false);
        } else {
            reply.addAllClaims(jwtTokenUtil.getClaimDTOsForAuthStateCheck(sessionToken));
            reply
                .setIsAuthenticated(true)
                .setNameClaimType(JwtTokenUtil.NAME_CLAIM_TYPE)
                .setRoleClaimType(JwtTokenUtil.ROLE_CLAIM_TYPE)
                .setAuthenticationType(JwtTokenUtil.AUTHENTICATION_TYPE)
                .setName(jwtTokenUtil.getNamedClaimFromToken(sessionToken, "name").toString());
        }

        return reply.build();
    }

}
