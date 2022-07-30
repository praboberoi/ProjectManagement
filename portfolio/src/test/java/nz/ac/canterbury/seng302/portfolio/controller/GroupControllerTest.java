package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.service.GroupService;
import nz.ac.canterbury.seng302.portfolio.utils.PrincipalUtils;
import nz.ac.canterbury.seng302.shared.identityprovider.DeleteGroupResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(controllers = GroupController.class)
@AutoConfigureMockMvc(addFilters = false)
public class GroupControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GroupService groupService;

    MockedStatic<PrincipalUtils> mockedPrincipalUtils;

    @BeforeEach
    private void initStaticMocks() {
        mockedPrincipalUtils = mockStatic(PrincipalUtils.class);
    }

    /**
     * Checks that the group delete functionality will be called correctly for teachers
     * @throws Exception Expection thrown during mockmvc runtime
     */
    @Test
    void givenTeacherUserAndGroupExists_whenDeleteGroupCalled_thenGroupIsDeleted() throws Exception{
        when(groupService.deleteGroup(anyInt())).thenReturn(DeleteGroupResponse.newBuilder().setIsSuccess(true).setMessage("Group 1 has been deleted").build());
        when(PrincipalUtils.checkUserIsTeacherOrAdmin(any())).thenReturn(true);
        mockMvc
            .perform(delete("/groups/1/delete"))
            .andExpect(status().isOk());
    }

    /**
     * Checks that the group delete functionality will be called correctly for non teacher/admin users
     * @throws Exception Expection thrown during mockmvc runtime
     */
    @Test
    void givenNonTeacherUserAndGroupExists_whenDeleteGroupCalled_thenPermissionDenied() throws Exception{
        when(groupService.deleteGroup(anyInt())).thenReturn(DeleteGroupResponse.newBuilder().setIsSuccess(true).setMessage("Group 1 has been deleted").build());
        when(PrincipalUtils.checkUserIsTeacherOrAdmin(any())).thenReturn(false);
        mockMvc
            .perform(delete("/groups/1/delete"))
            .andExpect(status().isForbidden());
    }
}
