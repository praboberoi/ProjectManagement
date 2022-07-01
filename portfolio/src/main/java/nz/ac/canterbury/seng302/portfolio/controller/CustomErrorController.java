package nz.ac.canterbury.seng302.portfolio.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class CustomErrorController implements ErrorController {
    @Value("${apiPrefix}") private String apiPrefix;

    @RequestMapping("/error")
    public String handleError(Model model) {
        model.addAttribute("apiPrefix", apiPrefix);
        return "error";
    }
}
