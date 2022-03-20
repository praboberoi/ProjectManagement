package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;

import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class RegisterController {

    @Autowired
    private UserAccountClientService userAccountClientService;

    /**
     * @param request HTTP request sent to this endpoint
     * @param response HTTP response that will be returned by this endpoint
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @return Message generated by IdP about authenticate attempt
     */
    @GetMapping("/register")
    public String register(
            HttpServletRequest request,
            HttpServletResponse response,
            Model model
    ) {
        return "register";
    }

    @PostMapping("/register")
    public String createUser(
            HttpServletResponse response,
            @RequestParam String username,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String nickname,
            @RequestParam String bio,
            @RequestParam String pronouns,
            @RequestParam String email,
            @RequestParam String password,
            Model model
    ) {
        UserRegisterResponse idpResponse = userAccountClientService.register(username, firstName, lastName, nickname, bio, pronouns, email, password);
        if (idpResponse.getIsSuccess()) {
            return "redirect:/login";
        } else {
            model.addAttribute("error", idpResponse.getMessage());
            model.addAttribute("username", username);
            model.addAttribute("firstName", firstName);
            model.addAttribute("lastName", lastName);
            model.addAttribute("nickname", nickname);
            model.addAttribute("bio", bio);
            model.addAttribute("pronouns", pronouns);
            model.addAttribute("email", email);
            return "/register";
        }
    }

}
