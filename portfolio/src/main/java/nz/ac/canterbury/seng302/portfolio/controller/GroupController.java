package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.CreateGroupResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import nz.ac.canterbury.seng302.portfolio.model.Groups;
import nz.ac.canterbury.seng302.portfolio.service.GroupService;
import nz.ac.canterbury.seng302.portfolio.utils.PrincipalUtils;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.DeleteGroupResponse;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        List<Groups> groups = Arrays.asList(groupService.getMembersWithoutAGroup(), groupService.getTeachingStaffGroup());
        model.addAttribute("listGroups", groups);
        model.addAttribute("selectedGroup", groupService.getMembersWithoutAGroup());
        return "groups";
    }


    /**
     * Attempts to delete a group from the idp server
     * @param groupId The id of the group to be deleted
     * @param principal Authentication information containing user info
     * @return Status of the request and corresponding message
     */
    @DeleteMapping(value = "/groups/{groupId}/delete")
    public ResponseEntity<String> deleteGroup(@PathVariable int groupId, @AuthenticationPrincipal AuthState principal) {
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

    /**
     * Get method for the selected group.
     * @return The selected group fragment
     */
    @GetMapping("/groups/{groupId}")
    public ModelAndView selectedGroup(
            @PathVariable int groupId
    ) {
        Groups selectedGroup = groupService.getGroupById(groupId);
        ModelAndView mv = new ModelAndView("groups::selectedGroup");
        mv.addObject("selectedGroup", selectedGroup);
        return mv;
    }

    /**
     * Get method for the unassigned members group.
     * @return The selected group fragment
     */
    @GetMapping("/groups/unassigned")
    public ModelAndView unassignedGroup() {
        Groups selectedGroup = groupService.getMembersWithoutAGroup();
        ModelAndView mv = new ModelAndView("groups::selectedGroup");
        mv.addObject("selectedGroup", selectedGroup);
        return mv;
    }

    /**
     * Get method for the teachers group.
     * @return The teacher group fragment
     */
    @GetMapping("/groups/teachers")
    public ModelAndView teachersGroup() {
        Groups selectedGroup = groupService.getTeachingStaffGroup();
        ModelAndView mv = new ModelAndView("groups::selectedGroup");
        mv.addObject("selectedGroup", selectedGroup);
        return mv;
    }

    /**
     * Attempts to create a group from in the idp server
     * @param principal Authentication information containing user info
     * @return Status of the request and corresponding message
     */
    @PostMapping(value = "/groups")
    public String createGroup(@AuthenticationPrincipal AuthState principal,
                              @RequestParam String shortName,
                              @RequestParam String longName,
                              Model model,
                              RedirectAttributes ra
    ) {
        if (!(PrincipalUtils.checkUserIsTeacherOrAdmin(principal))) {
            ra.addFlashAttribute("messageDanger", "Insufficient permissions to create group.");
            return "redirect:/groups";
        }
        CreateGroupResponse response = groupService.createGroup(shortName, longName);
        model.addAttribute("roles", PrincipalUtils.getUserRole(principal));
        model.addAttribute("user", userAccountClientService.getUser(principal));
        ResponseEntity.BodyBuilder reply;
        if (response.getIsSuccess()) {
            reply = ResponseEntity.status(HttpStatus.OK);
            ra.addFlashAttribute("messageSuccess", response.getMessage());
        } else {
            reply = ResponseEntity.status(HttpStatus.NOT_FOUND);
            ra.addFlashAttribute("messageDanger", response.getMessage());
        }
        return "redirect:/groups";
    }
}
