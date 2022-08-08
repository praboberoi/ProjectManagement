package nz.ac.canterbury.seng302.portfolio.controller;

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
import nz.ac.canterbury.seng302.shared.identityprovider.AddGroupMembersResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.DeleteGroupResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.RemoveGroupMembersResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;

import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Controller for group page
 */
@Controller
public class GroupController {
    @Autowired
    private GroupService groupService;

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
        List<Groups> groups = Arrays.asList(groupService.getMembersWithoutAGroup(), groupService.getTeachingStaffGroup());
        groups = Stream.concat(groups.stream(), groupService.getPaginatedGroups().stream()).toList();
        model.addAttribute("listGroups", groups);
        model.addAttribute("selectedGroup", groupService.getMembersWithoutAGroup());
        return "groups";
    }

    /**
     * Get message for empty registration page
     * @param request HTTP request sent to this endpoint
     * @param response HTTP response that will be returned by this endpoint
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @return Registration html page
     */
    @GetMapping(path="/groups/list")
    public ModelAndView groupsList() {
        List<Groups> groups = Arrays.asList(groupService.getMembersWithoutAGroup(), groupService.getTeachingStaffGroup());
        groups = Stream.concat(groups.stream(), groupService.getPaginatedGroups().stream()).toList();
        ModelAndView mv = new ModelAndView("groups::groupList");
        mv.addObject("listGroups", groups);
        return mv;
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
     * Removes the selected users from the selected group
     * @param listOfUserIds List of users to remove in csv format
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @param principal Authentication information containing user info
     * @return Response with status code and message
     */
    @PostMapping("/groups/{groupId}/removeMembers")
    public ResponseEntity<String> removeMembersFromGroup(
            @PathVariable Integer groupId,
            String listOfUserIds,
            Model model,
            @AuthenticationPrincipal AuthState principal){
        if (!PrincipalUtils.checkUserIsTeacherOrAdmin(principal)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Insufficient Permissions");
        }

        String additionalInfo = "";
        List<Integer> userIds;
        try {
            userIds = new ArrayList<>(Arrays.stream(listOfUserIds.split(",")).map(Integer::parseInt).toList());
        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User list must be a csv of integers");
        }

        if (groupId == -1 && !PrincipalUtils.getUserRole(principal).contains(UserRole.COURSE_ADMINISTRATOR.name()) && userIds.remove(Integer.valueOf(PrincipalUtils.getUserId(principal)))) {
            additionalInfo += "\nUnable to remove own role";
        }

        RemoveGroupMembersResponse response = groupService.removeGroupMembers(userIds, groupId);
        if (response.getIsSuccess() && "".equals(additionalInfo)) {
            return ResponseEntity.status(HttpStatus.OK).body(response.getMessage());
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response.getMessage() + additionalInfo);
        }
    }

    /**
     * Adds the selected users to the selected group
     * @param listOfUserIds List of users to add in csv format
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @param principal Authentication information containing user info
     * @return Response with status code and message
     */
    @PostMapping("/groups/{groupId}/addMembers")
    public ResponseEntity<String> addMembersFromGroup(
            @PathVariable Integer groupId,
            String listOfUserIds,
            Model model,
            @AuthenticationPrincipal AuthState principal){
        if (!PrincipalUtils.checkUserIsTeacherOrAdmin(principal)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Insufficient Permissions");
        }

        List<Integer> userIds;
        try {
            userIds = new ArrayList<>(Arrays.stream(listOfUserIds.split(",")).map(Integer::parseInt).toList());
        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User list must be a csv of integers");
        }

        AddGroupMembersResponse response = groupService.addGroupMembers(userIds, groupId);
        if (response.getIsSuccess()) {
            return ResponseEntity.status(HttpStatus.OK).body(response.getMessage());
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response.getMessage());
        }
    }
}
