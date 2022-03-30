package nz.ac.canterbury.seng302.portfolio.controller;

import io.grpc.StatusRuntimeException;
import nz.ac.canterbury.seng302.portfolio.authentication.CookieUtil;
import nz.ac.canterbury.seng302.portfolio.service.AuthenticateClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthenticateResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class LoginController {

    private final AuthenticateClientService authenticateClientService;

    public LoginController (AuthenticateClientService authenticateClientService) {
        this.authenticateClientService = authenticateClientService;
    }

    /**
     * Attempts to authenticate with the Identity Provider via gRPC.
     *
     * This process works in a few stages:
     *  1.  We send the username and password to the IdP
     *  2.  We check the response, and if it is successful we add a cookie to the HTTP response so that
     *      the client's browser will store it to be used for future authentication with this service.
     *  3.  We return the thymeleaf login template with the 'message' given by the identity provider,
     *      this message will be something along the lines of "Logged in successfully!",
     *      "Bad username or password", etc.
     *
     * @param request HTTP request sent to this endpoint
     * @param response HTTP response that will be returned by this endpoint
     * @param username Username of account to log in to IdP with
     * @param password Password associated with username
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @return Message generated by IdP about authenticate attempt
     */
    @GetMapping("/login")
    public String login(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(name="username", required=false, defaultValue="abc123") String username,
            @RequestParam(name="password", required=false, defaultValue="Password123!") String password,
            Model model
    ) {
        return "login";
    }

    @PostMapping("/login")
    public String submitLogin (
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(name="username") String username,
            @RequestParam(name="password") String password,
            Model model)
        {
        AuthenticateResponse loginReply;
        try {
            loginReply = authenticateClientService.authenticate(username, password);
        } catch (StatusRuntimeException e){
            model.addAttribute("error", "Error connecting to Server");
            return "login";
        }
        if (loginReply.getSuccess()) {
            var domain = request.getHeader("host");
            CookieUtil.create(
                response,
                "lens-session-token",
                    loginReply.getToken(),
                true,
                5 * 60 * 60, // Expires in 5 hours
                domain.startsWith("localhost") ? null : domain
            );
            return "redirect:/dashboard";
        }

        model.addAttribute("error", loginReply.getMessage());
        return "login";
    }

}
