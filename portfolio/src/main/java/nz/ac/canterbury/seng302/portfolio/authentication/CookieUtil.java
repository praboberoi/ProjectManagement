package nz.ac.canterbury.seng302.portfolio.authentication;

import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This class contains some useful methods for managing cookies, such as creating, retrieving, and clearing them
 */
public class CookieUtil {

    private CookieUtil() {}

    /**
     * Creates a new cookie with the provided value
     * @param httpServletResponse Response to be returned to the client
     * @param name Name of the cookie to add
     * @param value What to set the cookie as
     * @param secure If the cookie is secure
     * @param maxAge How long the cookie will last
     * @param domain The domain the cookie belongs to
     * @return
     */
    public static Cookie create(HttpServletResponse httpServletResponse, String name, String value, Boolean secure, Integer maxAge, String domain) {
        Cookie cookie = new Cookie(name, value);
        cookie.setSecure(secure);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(maxAge);
        if (domain != null) {
            cookie.setDomain(domain);
        }
        cookie.setPath("/");
        httpServletResponse.addCookie(cookie);
        return cookie;
    }

    /**
     * Deletes the users cookie
     * @param httpServletResponse Response to be returned to the client
     * @param name Name of the cookie to remove
     */
    public static void clear(HttpServletResponse httpServletResponse, String name) {
        Cookie cookie = new Cookie(name, null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        httpServletResponse.addCookie(cookie);
    }

    /**
     * Gets the value of a cookie
     * @param httpServletRequest Request sent to the server
     * @param name Name of the cookie to retrieve
     * @return Value of the cookie
     */
    public static String getValue(HttpServletRequest httpServletRequest, String name) {
        Cookie cookie = WebUtils.getCookie(httpServletRequest, name);
        return cookie != null ? cookie.getValue() : null;
    }
}
