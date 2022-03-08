package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.service.GreeterClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;

import nz.ac.canterbury.seng302.shared.projectDAL.Datastore;
import nz.ac.canterbury.seng302.shared.projectDAL.model.User;
import nz.ac.canterbury.seng302.shared.projectDAL.readWrite.UserDAL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AccountController {

    @GetMapping("/account")
    public String account(
            @AuthenticationPrincipal AuthState principal,
            @RequestParam(name="userId", required=false) String favouriteColour,
            Model model
    ) {
        Datastore db = new Datastore();
        User user = UserDAL.getUserByUsername(db, principal.getClaims(1).getValue());
        model.addAttribute("username", user.username);
        model.addAttribute("firstName", user.firstName);
        model.addAttribute("lastName", user.lastName);
        model.addAttribute("nickname", user.nickname);
        model.addAttribute("pronouns", user.pronouns);
        model.addAttribute("email", user.email);
        model.addAttribute("bio", user.bio);
        return "account";
    }

}
