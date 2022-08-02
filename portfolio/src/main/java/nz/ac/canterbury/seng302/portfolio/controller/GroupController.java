package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import nz.ac.canterbury.seng302.portfolio.service.GroupService;
import nz.ac.canterbury.seng302.portfolio.utils.PrincipalUtils;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.DeleteGroupResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Controller for group page
 */
@Controller
public class GroupController {
    @Autowired
    private GroupService groupService;

    @Autowired private UserAccountClientService userAccountClientService;

    @Value("${apiPrefix}") private String apiPrefix;

    /**
     * Adds common model elements used by all controller methods.
     */
    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("apiPrefix", apiPrefix);
    }


    /**
     * Get message for empty registration page
     * @param request HTTP request sent to this endpoint
     * @param response HTTP response that will be returned by this endpoint
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @return Registration html page
     */
    @GetMapping(path="/groups")
    public String groups(
            HttpServletRequest request,
            HttpServletResponse response,
            Model model,
            @AuthenticationPrincipal AuthState principal

    ) {
        model.addAttribute("roles", PrincipalUtils.getUserRole(principal));
        model.addAttribute("user", userAccountClientService.getUser(principal));
        return "groups";
    }


    /**
     * Attempts to delete a group from the idp server
     * @param groupId The id of the group to be deleted
     * @param principal Authentication information containing user info
     * @return Status of the request and corresponding message
     */
    @DeleteMapping(value = "/groups/{groupId}/delete")
    public ResponseEntity<String> removeRole(@PathVariable int groupId, @AuthenticationPrincipal AuthState principal) {
        if (!(PrincipalUtils.checkUserIsTeacherOrAdmin(principal))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Insufficient Permissions");
        }
        DeleteGroupResponse response = groupService.deleteGroup(groupId);
        
        ResponseEntity.BodyBuilder reply;
        if (response.getIsSuccess()) {
            reply = ResponseEntity.status(HttpStatus.OK);
        } else {
            reply = ResponseEntity.status(HttpStatus.NOT_FOUND);
        }
        return reply.body(response.getMessage());
    }
}
