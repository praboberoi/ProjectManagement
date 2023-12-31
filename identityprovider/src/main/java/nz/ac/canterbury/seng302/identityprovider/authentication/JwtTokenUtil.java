package nz.ac.canterbury.seng302.identityprovider.authentication;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;

import javax.crypto.SecretKey;
import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class JwtTokenUtil implements Serializable {

	private static final JwtTokenUtil singletonInstance = new JwtTokenUtil();

	public static JwtTokenUtil getInstance() {
		return singletonInstance;
	}

	// Private constructor to force using singleton instance
	private JwtTokenUtil() {}

	private static final long serialVersionUID = -2550185165626007488L;
	public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;

	public static final String ROLE_CLAIM_TYPE = "role";
	public static final String NAME_CLAIM_TYPE = "name";
	public static final String AUTHENTICATION_TYPE = "AuthenticationTypes.Federation";

	private static final Map<Class, String> jwtValueTypesForJavaClasses = Map.of(
		String.class, "http://www.w3.org/2001/XMLSchema#string",
		int.class, "http://www.w3.org/2001/XMLSchema#integer",
		Integer.class, "http://www.w3.org/2001/XMLSchema#integer"
	);

    SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

	// retrieve username from jwt token
	public String getUsernameFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}

	// retrieve user id from jwt token
	public Integer getUserIdFromToken(String token) {
		Claims claims = getAllClaimsFromToken(token);
		return Integer.parseInt(getClaimAsDTO("nameid", claims).getValue());
	}

	// retrieve expiration date from jwt token
	public Date getExpirationDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getExpiration);
	}

	public Object getNamedClaimFromToken(String token, String key) {
		final Claims claims = getAllClaimsFromToken(token);
		return claims.get(key);
	}

	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}

    // for retrieveing any information from token we will need the secret key
	private Claims getAllClaimsFromToken(String token) {
		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
	}

	// check if the token has expired
	private Boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}

	/**
	 * From a Claims object, pull out a single claim and build it as a ClaimDTO.
	 * @param type The type (or key) of the claim.
	 * @param claims The claims object storing the claim
	 * @return ClaimDTO representing some JWT claim
	 */
	private ClaimDTO getClaimAsDTO(String type, Claims claims) {
		if (claims.get(type) == null) {
			return null;
		}
		return ClaimDTO
				.newBuilder()
				.setIssuer(claims.getIssuer())
				.setOriginalIssuer(claims.getIssuer())
				.setType(type)
				.setValue(claims.get(type).toString())
				.setValueType(jwtValueTypesForJavaClasses.getOrDefault(claims.get(type).getClass(), ""))
				.build();
	}

	/**
	 * Parses a token and pulls certain expected claims out to form a list of ClaimDTOs. These DTOs allow
	 * for serializing a Java 'Claims' object for use in any other system. This format also corresponds with
	 * the authentication scheme expected by LENS.
	 *
	 * @param token Session token containing user claims
	 * @return List of ClaimDTOs generated from the session token
	 */
	public Collection<ClaimDTO> getClaimDTOsForAuthStateCheck(String token) {
		Claims claims = getAllClaimsFromToken(token);

		return Stream.of(
			getClaimAsDTO("unique_name", claims),
			getClaimAsDTO("sub", claims),
			getClaimAsDTO("nameid", claims),
			getClaimAsDTO("name", claims),
			getClaimAsDTO(ROLE_CLAIM_TYPE, claims),
			getClaimAsDTO("nbf", claims),
			getClaimAsDTO("exp", claims),
			getClaimAsDTO("iat", claims)
		).filter(Objects::nonNull).collect(toList());
	}

	/**
	 * Generate a JWT token for a user, containing some basic information about the user.
	 *
	 * @param username Username used to log in (e.g abc123)
	 * @param userId Internal ID of user assigned by the IdP
	 * @param nameOfUser The user's name, e.g "John Smith"
	 * @param rolesOfUser The user's roles, e.g student, teacher, or course administrator
	 * @return String encoded JWT token
	 */
	public String generateTokenForUser(String username, int userId, String nameOfUser, List<UserRole> rolesOfUser) {
		Map<String, Object> claims = new HashMap<>();

        claims.put("unique_name", username);
        claims.put("nameid", userId);
        claims.put("name", nameOfUser);

		// When assigning multiple roles to a user, encode them as a comma separated list
		// E.g "student,teacher" or "teacher,courseadministrator,student" (Order doesn't matter)
		claims.put(ROLE_CLAIM_TYPE, rolesOfUser.stream().map(UserRole::name).collect(Collectors.joining(",")));

		return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
				.setIssuer("LOCAL AUTHORITY")
                .setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
				.signWith(key).compact();
    }

	/**
	 * Validate the token. For now we simply check if it was signed using our signing key, and if it isn't expired.
	 * @param token JWT token string
	 * @return True if token validates, False otherwise
	 */
	public Boolean validateToken(String token) {
		try {
			return !isTokenExpired(token);
		} catch (SignatureException | MalformedJwtException e) {
			return false;
		}
	}

	/**
	 * Validate the roles of the user stored in the token
	 * @param token JWT token string
	 * @param userRoles The users current roles
	 * @return True if token roles match, False otherwise
	 */
	public Boolean validateTokenRoles(String token, List<UserRole> userRoles) {
		Claims claims = getAllClaimsFromToken(token);

		return getClaimAsDTO(ROLE_CLAIM_TYPE, claims).getValue()
			.equals(userRoles.stream().map(UserRole::name).collect(Collectors.joining(",")));
	}
}
