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

@Controller
public class DeadlineController {
    @Autowired private DeadlineService deadlineService;
    @Autowired private ProjectService projectService;


    @RequestMapping(path="/newDeadline", method = RequestMethod.GET)
    public String saveDeadline(Model model) throws IncorrectDetailsException {
        Deadline deadline = deadlineService.getNewDeadline();
        deadline.setProject(projectService.getProjectById(1));
        model.addAttribute("deadline", deadline);
        return "deadlineTest";
    }


    @PostMapping(path="/postDeadline")
    public String saveDeadline(Model model, @ModelAttribute Deadline deadline) {
        deadlineService.saveDeadline(deadline);
        model.addAttribute("deadline", deadline);
        return "resultTest";
    }


}
