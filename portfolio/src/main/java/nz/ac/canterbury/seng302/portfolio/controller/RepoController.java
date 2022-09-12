package nz.ac.canterbury.seng302.portfolio.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import nz.ac.canterbury.seng302.portfolio.utils.PrincipalUtils;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;

/**
 * Controller for repo components
 */
@Controller
public class RepoController {

    @Autowired
    private GroupService groupService;

    @Autowired
    private RepoRepository repoRepository;

    /**
     * Gets the groups repo.
     * 
     * @param groupId The group id of the repo to get.
     * @return The repo page segment
     */
    @GetMapping(path = "/repo/{groupId}")
    public ModelAndView groupPage(@PathVariable int groupId, @AuthenticationPrincipal AuthState principal) {
        Groups group = groupService.getGroupById(groupId);
        ModelAndView mv;
        if (group == null || group.getGroupId() == 0) {
            mv = new ModelAndView("groupFragments::repoSettingsError");
            mv.setStatus(HttpStatus.NOT_FOUND);
            return mv;
        } else if (!(PrincipalUtils.checkUserIsTeacherOrAdmin(principal)) && group.getMembers().stream()
                .noneMatch(user -> user.getUserId() == PrincipalUtils.getUserId(principal))) {
            mv = new ModelAndView("groupFragments::repoSettingsError");
            mv.setStatus(HttpStatus.FORBIDDEN);
            return mv;
        }

        Repo repo = repoRepository.getByGroupId(groupId);
        mv = new ModelAndView("groupFragments::repoSettings");
        mv.addObject("repo", repo);
        return mv;
    }

    /**
     * Gets the individual group page.
     * 
     * @param groupId The pages group id for future implementation
     * @param model   Parameters sent to thymeleaf template to be rendered into HTML
     * @param ra      Redirect Attribute frontend message object
     * @return The group page
     */
    @PostMapping(path = "/repo/{groupId}/save")
    public ResponseEntity<String> saveGroupRepo(@PathVariable int groupId, @ModelAttribute RepoDTO repoDTO, @AuthenticationPrincipal AuthState principal) {
        Groups group = groupService.getGroupById(groupId);
        if (group == null || group.getGroupId() == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Unable to find group");
        } else if (!(PrincipalUtils.checkUserIsTeacherOrAdmin(principal)) && group.getMembers().stream()
                .noneMatch(user -> user.getUserId() == PrincipalUtils.getUserId(principal))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not have permission to edit these repo settings");
        }

        Repo repo = new Repo(repoDTO);

        repoRepository.save(repo);

        return ResponseEntity.status(HttpStatus.OK).body("Successfully updated the group's repository");
    }

}
