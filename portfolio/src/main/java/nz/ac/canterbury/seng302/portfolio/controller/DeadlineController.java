package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Deadline;
import nz.ac.canterbury.seng302.portfolio.service.DeadlineService;
import nz.ac.canterbury.seng302.portfolio.service.IncorrectDetailsException;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class DeadlineController {
    @Autowired private DeadlineService deadlineService;
    @Autowired private ProjectService projectService;


    @RequestMapping(path="/newDeadline", method = RequestMethod.GET)
    public String newDeadline(Model model, RedirectAttributes ra) {
        Deadline deadline = deadlineService.getNewDeadline();
        try {
            deadline.setProject(projectService.getProjectById(1));
            model.addAttribute("deadline", deadline);
            return "deadlineTest";
        } catch (IncorrectDetailsException e) {
            ra.addFlashAttribute("messageDanger", e.getMessage());
            return "redirect:/dashboard";
        }

    }


    @PostMapping(path="/postDeadline")
    public String saveDeadline(Model model, @ModelAttribute Deadline deadline, RedirectAttributes ra) {
        try {
            String message = deadlineService.saveDeadline(deadline);
            model.addAttribute("deadline", deadline);
            ra.addFlashAttribute("messageSuccess", message);
            return "resultTest";
        } catch (IncorrectDetailsException e) {
            ra.addFlashAttribute("messageDanger", e.getMessage());
            return "redirect:/dashboard";
        }

    }


}
