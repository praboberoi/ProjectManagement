package nz.ac.canterbury.seng302.portfolio.controller;

import io.grpc.StatusRuntimeException;
import nz.ac.canterbury.seng302.portfolio.Sprint;
import nz.ac.canterbury.seng302.portfolio.authentication.CookieUtil;
import nz.ac.canterbury.seng302.portfolio.service.AuthenticateClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthenticateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class ProjectController {

    @GetMapping("/project")
    public String sprintForm(Model model) {
        model.addAttribute("newSprint", new Sprint());
        return "project";
    }

    @PostMapping("/project")
    public String sprintSubmit(@ModelAttribute Sprint sprint, Model model) {
        model.addAttribute("newSprint", sprint);
        return "project";
    }

    /*
    @GetMapping
    String getProject(Model model){
        model.addAttribute("newproject", "this is a new project");
            return "project";

    }
    */

}
