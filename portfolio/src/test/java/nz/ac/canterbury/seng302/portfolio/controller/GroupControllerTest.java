package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Groups;
import nz.ac.canterbury.seng302.portfolio.model.RepoRepository;
import nz.ac.canterbury.seng302.portfolio.service.GroupService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.portfolio.utils.ControllerAdvisor;
import nz.ac.canterbury.seng302.portfolio.utils.PrincipalUtils;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = GroupController.class)
@AutoConfigureMockMvc(addFilters = false)
public class GroupControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GroupService groupService;

    @MockBean
    private UserAccountClientService userAccountClientService;

    @MockBean
    private RepoRepository repoRepository;

    @MockBean
    private SimpMessagingTemplate template;

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
     * Checks that the group create functionality will be called correctly for teacher/admin users
     * @throws Exception Exception thrown during mockmvc runtime
     */
    @Test
    void givenTeacherUser_whenCreateGroupCalled_thenRedirectToGroupsPage() throws Exception{
        when(groupService.createGroup(anyString(), anyString())).thenReturn(CreateGroupResponse.newBuilder().setIsSuccess(true).setMessage("success").build());
        when(PrincipalUtils.checkUserIsTeacherOrAdmin(any())).thenReturn(true);
        mockMvc
                .perform(post("/groups?shortName=&longName="))
                .andExpect(status().isOk())
                .andExpect(content().string( "success"));
    }

    /**
     * Checks that the group create functionality will be called correctly for non teacher/admin users
     * @throws Exception Exception thrown during mockmvc runtime
     */
    @Test
    void givenTeacherUser_whenCreateGroupCalledIncorrectly_thenRedirectToGroupsPage() throws Exception{
        when(groupService.createGroup(anyString(), anyString())).thenReturn(CreateGroupResponse.newBuilder().setIsSuccess(false).setMessage("unsuccessful").build());
        when(PrincipalUtils.checkUserIsTeacherOrAdmin(any())).thenReturn(true);
        mockMvc
                .perform(post("/groups?shortName=&longName="))
                .andExpect(status().isBadRequest())
                .andExpect(content().string( "unsuccessful"));
    }


    /**
     * Checks that the group create functionality will be called correctly for non teacher/admin users
     * @throws Exception Exception thrown during mockmvc runtime
     */
    @Test
    void givenNonTeacherUserAndGroupExists_whenCreateGroupCalled_thenPermissionDenied() throws Exception{
        when(PrincipalUtils.checkUserIsTeacherOrAdmin(any())).thenReturn(false);
        mockMvc
                .perform(post("/groups?shortName=&longName="))
                .andExpect(status().isForbidden())
                .andExpect(content().string( "Insufficient permissions to save group."));
    }

    /**
     * Checks that all the groups are returned when group page is requested
     * @throws Exception Exception thrown during mockmvc runtime
     */
    @Test
    void whenGroupsListRequested_thenAllGroupsReturned() throws Exception{
        when(groupService.getMembersWithoutAGroup()).thenReturn(new Groups("Members without a group", null, 0, List.of()));
        when(groupService.getTeachingStaffGroup()).thenReturn(new Groups("Teaching Staff", null, 0, List.of()));
        mockMvc
            .perform(get("/groups/list"))
            .andExpect(status().isOk())
            .andExpect(model().attribute("listGroups", Matchers.<Collection<Groups>> allOf(hasSize(2))));
    }

    /**
     * Checks that all the groups are returned when group page is requested
     * @throws Exception Exception thrown during mockmvc runtime
     */
    @Test
    void whenGroupsPageRequested_thenAllGroupsReturned() throws Exception{
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
    void whenGroupFragmentRequested_thenGroupIsReturned() throws Exception{
        Groups group = new Groups("Team: 400", "Bad Request", 1, List.of());
        when(groupService.getMembersWithoutAGroup()).thenReturn(new Groups("Members without a group", null, 0, List.of()));
        when(groupService.getTeachingStaffGroup()).thenReturn(new Groups("Teaching Staff", null, 0, List.of()));
        when(groupService.getGroupById(1)).thenReturn(group);
        mockMvc
            .perform(get("/groups/1"))
            .andExpect(status().isOk())
            .andExpect(model().attribute("group", group));
    }

    /**
     * Checks that a group are returned when the members without a group is requested
     * @throws Exception Exception thrown during mockmvc runtime
     */
    @Test
    void whenUnassignedFragmentRequested_thenGroupIsReturned() throws Exception{
        Groups unassignedGroup = new Groups("Members without a group", null, 0, List.of());
        when(groupService.getTeachingStaffGroup()).thenReturn(new Groups("Teaching Staff", null, 0, List.of()));
        when(groupService.getMembersWithoutAGroup()).thenReturn(unassignedGroup);
        mockMvc
            .perform(get("/groups/unassigned"))
            .andExpect(status().isOk())
            .andExpect(model().attribute("group", unassignedGroup));
    }

    /**
     * Checks that a group are returned when the teacher group is requested
     * @throws Exception Exception thrown during mockmvc runtime
     */
    @Test
    void whenTeacherFragmentRequested_thenGroupIsReturned() throws Exception{
        Groups teachingGroup = new Groups("Teaching Staff", null, 0, List.of());
        when(groupService.getMembersWithoutAGroup()).thenReturn(new Groups("Members without a group", null, 0, List.of()));
        when(groupService.getTeachingStaffGroup()).thenReturn(teachingGroup);
        mockMvc
            .perform(get("/groups/teachers"))
            .andExpect(status().isOk())
            .andExpect(model().attribute("group", teachingGroup));
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

    /**
     * Checks that member is added to a group when AddMembers is requested
     * @throws Exception Exception thrown during mockmvc runtime
     */
    @Test
    void whenAddMembersIsCalled_thenMemberIsAdded() throws Exception{
        AddGroupMembersResponse reply = AddGroupMembersResponse.newBuilder().setIsSuccess(true).build();
        when(groupService.addGroupMembers(any(), anyInt())).thenReturn(reply);
        when(PrincipalUtils.checkUserIsTeacherOrAdmin(any())).thenReturn(true);
        mockMvc
            .perform(post("/groups/1/addMembers?listOfUserIds=1"))
            .andExpect(status().isOk());
    }

    /**
     * Checks that member is rejected when AddMembers is requested if they are not an admin
     * @throws Exception Exception thrown during mockmvc runtime
     */
    @Test
    void givenStudentUser_whenAddMembersIsCalled_thenMemberIsAdded() throws Exception{
        AddGroupMembersResponse reply = AddGroupMembersResponse.newBuilder().setIsSuccess(true).build();
        when(groupService.addGroupMembers(any(), anyInt())).thenReturn(reply);
        when(PrincipalUtils.checkUserIsTeacherOrAdmin(any())).thenReturn(false);
        mockMvc
            .perform(post("/groups/1/addMembers?listOfUserIds=1"))
            .andExpect(status().isForbidden());
    }

    /**
     * Checks that request is rejected when AddMembers is requested on bad data
     * @throws Exception Exception thrown during mockmvc runtime
     */
    @Test
    void givenBadData_whenAddMembersIsCalled_thenRejected() throws Exception{
        AddGroupMembersResponse reply = AddGroupMembersResponse.newBuilder().setIsSuccess(true).build();
        when(groupService.addGroupMembers(any(), anyInt())).thenReturn(reply);
        when(PrincipalUtils.checkUserIsTeacherOrAdmin(any())).thenReturn(true);
        mockMvc
            .perform(post("/groups/1/addMembers?listOfUserIds=team400"))
            .andExpect(status().isBadRequest());
    }

    /**
     * Checks that request is rejected when AddMembers is requested on own user with only teacher role
     * @throws Exception Exception thrown during mockmvc runtime
     */
    @Test
    void givenUserWithTeacherRole_whenRemoveMembersIsCalledOnUser_thenRejected() throws Exception{
        when(PrincipalUtils.checkUserIsTeacherOrAdmin(any())).thenReturn(true);
        when(PrincipalUtils.getUserRole(any())).thenReturn(Arrays.asList(UserRole.TEACHER.name()));
        when(PrincipalUtils.getUserId(any())).thenReturn(1);
        mockMvc
            .perform(post("/groups/-1/removeMembers?listOfUserIds=1"))
            .andExpect(status().isForbidden())
            .andExpect(content().string("Unable to remove own role"));
    }

    /**
     * Checks that the group modification functionality will be called correctly for non teacher/admin users
     * @throws Exception Exception thrown during mockmvc runtime
     */
    @Test
    void givenStudentUser_whenModifyGroupCalled_thenRedirectToGroupsPage() throws Exception{
        when(groupService.modifyGroup(anyInt(), anyString(), anyString())).thenReturn(ModifyGroupDetailsResponse.newBuilder().setIsSuccess(true).setMessage("success").build());
        when(PrincipalUtils.checkUserIsTeacherOrAdmin(any())).thenReturn(false);
        when(groupService.getGroupById(anyInt())).thenReturn(new Groups("test", "Test longName", 1, List.of()));
        mockMvc
                .perform(post("/groups?groupId=1&shortName=&longName="))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Insufficient permissions to save group."));
    }

    /**
     * Checks that the group modification functionality will be called correctly for teacher/admin users
     * @throws Exception Exception thrown during mockmvc runtime
     */
    @Test
    void givenTeacherUser_whenModifyGroupCalled_thenSuccess() throws Exception{
        when(groupService.modifyGroup(anyInt(), anyString(), anyString())).thenReturn(ModifyGroupDetailsResponse.newBuilder().setIsSuccess(true).setMessage("success").build());
        when(PrincipalUtils.checkUserIsTeacherOrAdmin(any())).thenReturn(true);
        when(groupService.getGroupById(anyInt())).thenReturn(new Groups("test", "Test longName", 1, List.of()));
        mockMvc
                .perform(post("/groups?groupId=1&shortName=&longName="))
                .andExpect(status().isOk())
                .andExpect(content().string("success"));
    }

    /**
     * Checks that the group modification functionality will be called correctly for non teacher/admin users
     * @throws Exception Exception thrown during mockmvc runtime
     */
    @Test
    void givenTeacherUser_whenModifyGroupCalledWithInvalidData_thenFail() throws Exception{
        when(groupService.modifyGroup(anyInt(), anyString(), anyString())).thenReturn(ModifyGroupDetailsResponse.newBuilder().setIsSuccess(false).setMessage("Fail").build());
        when(PrincipalUtils.checkUserIsTeacherOrAdmin(any())).thenReturn(true);
        when(groupService.getGroupById(anyInt())).thenReturn(new Groups("test", "Test longName", 1, List.of()));
        mockMvc
                .perform(post("/groups?groupId=1&shortName=&longName="))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Fail"));
    }

    /**
     * Checks that the group is added to the model when an individual groups page is requested
     * @throws Exception Exception thrown during mockmvc runtime
     */
    @Test
    void givenGroupExists_whenGroupPageRequested_thenGroupIsInModel() throws Exception{
        Groups group = new Groups("Team: 400", "Bad Request", 1, List.of());
        when(groupService.getGroupById(1)).thenReturn(group);
        mockMvc
            .perform(get("/group/1"))
            .andExpect(status().isOk())
            .andExpect(model().attribute("group", group));
    }

    /**
     * Checks that the group page is redirected when the group doesn't exist
     * @throws Exception Exception thrown during mockmvc runtime
     */
    @Test
    void givenGroupDoesNotExist_whenGroupPageRequested_thenGroupIsNotInModel() throws Exception{
        when(groupService.getGroupById(1)).thenReturn(new Groups());
        mockMvc
            .perform(get("/group/1"))
            .andExpect(status().is3xxRedirection())
            .andExpect(flash().attribute("messageDanger", "Group 1 does not exist."));
    }

    /**
     * Checks that the group title is returned correctly
     * @throws Exception Exception thrown during mockmvc runtime
     */
    @Test
    void whenGroupsTitleRequested_thenTitleElementReturned() throws Exception{
        Groups group = new Groups("test", "Test longName", 1, List.of());
        when(groupService.getGroupById(1)).thenReturn(group);

        mockMvc
            .perform(get("/group/1/title"))
            .andExpect(status().isOk())
            .andExpect(model().attribute("group", group));
    }
}

