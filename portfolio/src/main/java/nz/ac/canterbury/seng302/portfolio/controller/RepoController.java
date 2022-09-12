package nz.ac.canterbury.seng302.portfolio.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller for group page
 */
@Controller
public class RepoController {

    /**
     * Gets the groups repo.
     * @param groupId The pages group id for future implementation
     * @return The group page
     */
    @GetMapping(path="/repo/{groupId}")
    public ModelAndView groupPage(@PathVariable int groupId) {
        ModelAndView mv = new ModelAndView("groupFragment::repoSettings");
        return mv;
    }

}
