package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Groups;
import nz.ac.canterbury.seng302.portfolio.service.GroupService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.portfolio.utils.ControllerAdvisor;
import nz.ac.canterbury.seng302.portfolio.utils.PrincipalUtils;
import nz.ac.canterbury.seng302.shared.identityprovider.DeleteGroupResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.RemoveGroupMembersResponse;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collection;
import java.util.List;

import org.hamcrest.Matchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(controllers = GroupController.class)
@AutoConfigureMockMvc(addFilters = false)
public class GroupControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GroupService groupService;

    @MockBean
    private UserAccountClientService userAccountClientService;

    @InjectMocks
    private ControllerAdvisor controllerAdvisor;

    private static MockedStatic<PrincipalUtils> mockedUtil;

    @BeforeAll
    private static void initStaticMocks() {
        mockedUtil = mockStatic(PrincipalUtils.class);
    }

    @AfterAll
    public static void close() {
        mockedUtil.close();
    }

    /**
     * Checks that the group delete functionality will be called correctly for teachers
     * @throws Exception Exception thrown during mockmvc runtime
     */
    @Test
    void givenTeacherUserAndGroupExists_whenDeleteGroupCalled_thenGroupIsDeleted() throws Exception{
        when(groupService.deleteGroup(anyInt())).thenReturn(DeleteGroupResponse.newBuilder().setIsSuccess(true).setMessage("Group 1 has been deleted").build());
        when(PrincipalUtils.checkUserIsTeacherOrAdmin(any())).thenReturn(true);
        mockMvc
            .perform(delete("/groups/1"))
            .andExpect(status().isOk());
    }

    /**
     * Checks that the group delete functionality will be called correctly for non teacher/admin users
     * @throws Exception Exception thrown during mockmvc runtime
     */
    @Test
    void givenNonTeacherUserAndGroupExists_whenDeleteGroupCalled_thenPermissionDenied() throws Exception{
        when(groupService.deleteGroup(anyInt())).thenReturn(DeleteGroupResponse.newBuilder().setIsSuccess(true).setMessage("Group 1 has been deleted").build());
        when(PrincipalUtils.checkUserIsTeacherOrAdmin(any())).thenReturn(false);
        mockMvc
            .perform(delete("/groups/1"))
            .andExpect(status().isForbidden());
    }

    /**
     * Checks that all the groups are returned when group page is requested
     * @throws Exception Exception thrown during mockmvc runtime
     */
    @Test
    void whenGroupPageRequested_thenAllGroupsReturned() throws Exception{
        when(groupService.getMembersWithoutAGroup()).thenReturn(new Groups("Members without a group", null, 0, List.of()));
        when(groupService.getTeachingStaffGroup()).thenReturn(new Groups("Teaching Staff", null, 0, List.of()));
        mockMvc
            .perform(get("/groups"))
            .andExpect(status().isOk())
            .andExpect(model().attribute("listGroups", Matchers.<Collection<Groups>> allOf(hasSize(2))));
    }

    /**
     * Checks that a group are returned when a specific group is requested
     * @throws Exception Exception thrown during mockmvc runtime
     */
    @Test
    void whenSelectedGroupFragmentRequested_thenGroupIsReturned() throws Exception{
        Groups selectedGroup = new Groups("Team: 400", "Bad Request", 1, null);
        when(groupService.getGroupById(1)).thenReturn(selectedGroup);
        mockMvc
            .perform(get("/groups/1"))
            .andExpect(status().isOk())
            .andExpect(model().attribute("selectedGroup", selectedGroup));
    }

    /**
     * Checks that a group are returned when the members without a group is requested
     * @throws Exception Exception thrown during mockmvc runtime
     */
    @Test
    void whenUnassignedFragmentRequested_thenGroupIsReturned() throws Exception{
        Groups unassignedGroup = new Groups("Members without a group", null, 0, null);
        when(groupService.getMembersWithoutAGroup()).thenReturn(unassignedGroup);
        mockMvc
            .perform(get("/groups/unassigned"))
            .andExpect(status().isOk())
            .andExpect(model().attribute("selectedGroup", unassignedGroup));
    }

    /**
     * Checks that a group are returned when the teacher group is requested
     * @throws Exception Exception thrown during mockmvc runtime
     */
    @Test
    void whenTeacherFragmentRequested_thenGroupIsReturned() throws Exception{
        Groups teachingGroup = new Groups("Teaching Staff", null, 0, null);
        when(groupService.getTeachingStaffGroup()).thenReturn(teachingGroup);
        mockMvc
            .perform(get("/groups/teachers"))
            .andExpect(status().isOk())
            .andExpect(model().attribute("selectedGroup", teachingGroup));
    }

    /**
     * Checks that member is removed from when RemoveMembers is requested
     * @throws Exception Exception thrown during mockmvc runtime
     */
    @Test
    void whenRemoveMembersIsCalled_thenMemberIsRemoved() throws Exception{
        RemoveGroupMembersResponse reply = RemoveGroupMembersResponse.newBuilder().setIsSuccess(true).build();
        when(groupService.removeGroupMembers(any(), anyInt())).thenReturn(reply);
        when(PrincipalUtils.checkUserIsTeacherOrAdmin(any())).thenReturn(true);
        mockMvc
            .perform(post("/groups/1/removeMembers?listOfUserIds=1"))
            .andExpect(status().isOk());
    }
}
