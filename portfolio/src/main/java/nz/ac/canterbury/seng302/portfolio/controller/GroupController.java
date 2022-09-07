package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Groups;
import nz.ac.canterbury.seng302.portfolio.service.GroupService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.portfolio.utils.PrincipalUtils;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import nz.ac.canterbury.seng302.shared.identityprovider.AddGroupMembersResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.DeleteGroupResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.RemoveGroupMembersResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

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
        List<Groups> groups = Arrays.asList(groupService.getMembersWithoutAGroup(), groupService.getTeachingStaffGroup());
        groups = Stream.concat(groups.stream(), groupService.getPaginatedGroups().stream()).toList();
        model.addAttribute("listGroups", groups);
        model.addAttribute("group", groupService.getMembersWithoutAGroup());
        return "groups";
    }

    /**
     * Get list of groups
     * @return List of groups
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
    @DeleteMapping(value = "/groups/{groupId}")
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
    public ModelAndView group(
            @PathVariable int groupId
    ) {
        Groups group = groupService.getGroupById(groupId);
        List<Groups> groups = Arrays.asList(groupService.getMembersWithoutAGroup(), groupService.getTeachingStaffGroup());
        groups = Stream.concat(groups.stream(), groupService.getPaginatedGroups().stream()).toList();
        ModelAndView mv = new ModelAndView("groups::group");
        mv.addObject("listGroups", groups);
        mv.addObject("group", group);
        return mv;
    }

    /**
     * Get method for the unassigned members group.
     * @return The selected group fragment
     */
    @GetMapping("/groups/unassigned")
    public ModelAndView unassignedGroup() {
        Groups group = groupService.getMembersWithoutAGroup();
        List<Groups> groups = Arrays.asList(groupService.getMembersWithoutAGroup(), groupService.getTeachingStaffGroup());
        groups = Stream.concat(groups.stream(), groupService.getPaginatedGroups().stream()).toList();
        ModelAndView mv = new ModelAndView("groups::group");
        mv.addObject("listGroups", groups);
        mv.addObject("group", group);
        return mv;
    }

    /**
     * Get method for the teachers group.
     * @return The teacher group fragment
     */
    @GetMapping("/groups/teachers")
    public ModelAndView teachersGroup() {
        Groups group = groupService.getTeachingStaffGroup();
        List<Groups> groups = Arrays.asList(groupService.getMembersWithoutAGroup(), groupService.getTeachingStaffGroup());
        groups = Stream.concat(groups.stream(), groupService.getPaginatedGroups().stream()).toList();
        ModelAndView mv = new ModelAndView("groups::group");
        mv.addObject("listGroups", groups);
        mv.addObject("group", group);
        return mv;
    }

    /**
     * Attempts to create a group from in the idp server
     * @param groupId Id of the new group, null if it is new
     * @param shortName short name of the group being created
     * @param longName long name of the group being created
     * @param principal Authentication information containing user info
     * @return Status of the request and corresponding message
     */
    @PostMapping(value = "/groups")
    public String createGroup(
        @AuthenticationPrincipal AuthState principal,
        @RequestParam(required = false) Integer groupId,
        @RequestParam String shortName,
        @RequestParam String longName,
        Model model,
        RedirectAttributes ra
    ) {
        if (!(PrincipalUtils.checkUserIsTeacherOrAdmin(principal))) {
            ra.addFlashAttribute("messageDanger", "Insufficient permissions to create group.");
            return "redirect:/groups";
        }
        boolean status;
        String message;
        if (groupId == null) {
            CreateGroupResponse response = groupService.createGroup(shortName, longName);
            status = response.getIsSuccess();
            message = response.getMessage();
        } else {
            ModifyGroupDetailsResponse response = groupService.modifyGroup(groupId, shortName, longName);
            status = response.getIsSuccess();
            message = response.getMessage();
        }
        model.addAttribute("roles", PrincipalUtils.getUserRole(principal));
        model.addAttribute("user", userAccountClientService.getUser(principal));
        if (status) {
            ra.addFlashAttribute("messageSuccess", message);
        } else {
            ra.addFlashAttribute("messageDanger", message);
        }
        return "redirect:/groups";
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
            if (listOfUserIds.length() == 1) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unable to remove own role");
            }
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
    public ResponseEntity<String> addMembersToGroup(
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

    /**
     * Gets the individual group page. This is currently the same page for every group.
     * @param groupId The pages group id for future implementation
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @return The group page
     */
    @GetMapping(path="/group/{groupId}")
    public String groupPage(@PathVariable int groupId, Model model, RedirectAttributes ra) {
        Groups group = groupService.getGroupById(groupId);
        if (group.getGroupId() == 0) {
            ra.addFlashAttribute("messageDanger", "Group " + groupId + " does not exist.");
            return "redirect:/groups";
        }
        model.addAttribute("group", group);
        return "group";
    }
}
