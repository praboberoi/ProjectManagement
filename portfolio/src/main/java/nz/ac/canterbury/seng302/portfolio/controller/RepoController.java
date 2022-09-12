package nz.ac.canterbury.seng302.portfolio.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import nz.ac.canterbury.seng302.portfolio.model.Groups;
import nz.ac.canterbury.seng302.portfolio.model.Repo;
import nz.ac.canterbury.seng302.portfolio.model.RepoRepository;
import nz.ac.canterbury.seng302.portfolio.model.dto.RepoDTO;
import nz.ac.canterbury.seng302.portfolio.service.GroupService;

/**
 * Controller for group page
 */
@Controller
public class RepoController {

    @Autowired
    private GroupService groupService;

    @Autowired
    private RepoRepository repoRepository;

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

    /**
     * Gets the individual group page.
     * @param groupId The pages group id for future implementation
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @param ra Redirect Attribute frontend message object
     * @return The group page
     */
    @PostMapping(path="/repo/{groupId}/save")
    public ResponseEntity<String> saveGroupRepo(@PathVariable int groupId, @ModelAttribute RepoDTO repoDTO) {
        Groups group = groupService.getGroupById(groupId);
        if (group.getGroupId() == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Unable to find group");
        }

        Repo repo = new Repo(repoDTO);

        repoRepository.save(repo);

        return ResponseEntity.status(HttpStatus.OK).body("Successfully updated the group's repository");
    }

}
