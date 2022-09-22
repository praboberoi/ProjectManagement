package nz.ac.canterbury.seng302.portfolio.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import nz.ac.canterbury.seng302.portfolio.model.Groups;
import nz.ac.canterbury.seng302.portfolio.model.Repo;
import nz.ac.canterbury.seng302.portfolio.model.User;
import nz.ac.canterbury.seng302.portfolio.service.GroupService;
import nz.ac.canterbury.seng302.portfolio.service.RepoService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.portfolio.utils.ControllerAdvisor;
import nz.ac.canterbury.seng302.portfolio.utils.IncorrectDetailsException;
import nz.ac.canterbury.seng302.portfolio.utils.PrincipalUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RepoController.class)
@AutoConfigureMockMvc(addFilters = false)
public class RepoControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GroupService groupService;
    @MockBean
    private RepoService repoService;

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
     * Checks that the repo fails when a group doesn't exist
     * @throws Exception Exception thrown during mockmvc runtime
     */
    @Test
    void givenGroupNotExists_whenGetRepoCalled_thenErrorReturned() throws Exception{
        when(PrincipalUtils.checkUserIsTeacherOrAdmin(any())).thenReturn(true);
        mockMvc
            .perform(get("/repo/1"))
            .andExpect(status().isNotFound());
    }

    /**
     * Checks that the repo is returned correctly when the group exists
     * @throws Exception Exception thrown during mockmvc runtime
     */
    @Test
    void givenGroupExists_whenGetRepoCalled_thenRepoReturned() throws Exception{
        Groups group = new Groups("Team: 400", "Bad Request", 1, List.of());
        Repo repo = new Repo(1, 1, group.getShortName() + "'s repo", 0, null, "https://gitlab.com");
        when(repoService.getRepoByGroup(anyInt())).thenReturn(repo);
        when(groupService.getGroupById(anyInt())).thenReturn(group);

        when(PrincipalUtils.checkUserIsTeacherOrAdmin(any())).thenReturn(true);
        mockMvc
            .perform(get("/repo/1"))
            .andExpect(status().isOk())
            .andExpect(content().json(new ObjectMapper().writeValueAsString(repo)));
    }

    /**
     * Checks that the repo page fails when a group doesn't exist
     * @throws Exception Exception thrown during mockmvc runtime
     */
    @Test
    void givenGroupNotExists_whenGetRepoSettingsCalled_thenErrorReturned() throws Exception{
        when(PrincipalUtils.checkUserIsTeacherOrAdmin(any())).thenReturn(true);
        mockMvc
            .perform(get("/repo/1/settings"))
            .andExpect(status().isNotFound());
    }

    /**
     * Checks that a user not in the group can't access the repo information
     * @throws Exception Exception thrown during mockmvc runtime
     */
    @Test
    void givenGroupExists_andUserNotAMember_whenGetRepoSettingsCalled_thenErrorReturned() throws Exception{
        Groups group = new Groups("Team: 400", "Bad Request", 1, List.of());
        when(PrincipalUtils.checkUserIsTeacherOrAdmin(any())).thenReturn(false);
        when(groupService.getGroupById(anyInt())).thenReturn(group);
        mockMvc
            .perform(get("/repo/1/settings"))
            .andExpect(status().isForbidden());
    }

    /**
     * Checks that a teacher or admin is able to access the repo information
     * @throws Exception Exception thrown during mockmvc runtime
     */
    @Test
    void givenGroupExists_andUserNotAMember_andUserIsATeacher_whenGetRepoSettingsCalled_thenPageReturned() throws Exception{
        Groups group = new Groups("Team: 400", "Bad Request", 1, List.of());
        Repo repo = new Repo(1, group.getShortName() + "'s repo", 0, null, "https://gitlab.com");
        when(PrincipalUtils.checkUserIsTeacherOrAdmin(any())).thenReturn(true);
        when(groupService.getGroupById(anyInt())).thenReturn(group);
        when(repoService.getRepoByGroup(anyInt())).thenReturn(repo);
        mockMvc
            .perform(get("/repo/1/settings"))
            .andExpect(status().isOk())
            .andExpect(model().attribute("repo", repo));
    }

    /**
     * Checks that a student in the group is able to access the repo information
     * @throws Exception Exception thrown during mockmvc runtime
     */
    @Test
    void givenGroupExists_andUserIsAMember_whenGetRepoSettingsCalled_thenPageReturned() throws Exception{
        User user = new User.Builder().userId(1).build();
        Groups group = new Groups("Team: 400", "Bad Request", 1, List.of(user));
        Repo repo = new Repo(1, group.getShortName() + "'s repo", 0, null, "https://gitlab.com");
        when(PrincipalUtils.checkUserIsTeacherOrAdmin(any())).thenReturn(false);
        when(PrincipalUtils.getUserId(any())).thenReturn(1);
        when(groupService.getGroupById(anyInt())).thenReturn(group);
        when(repoService.getRepoByGroup(anyInt())).thenReturn(repo);
        mockMvc
            .perform(get("/repo/1/settings"))
            .andExpect(status().isOk())
            .andExpect(model().attribute("repo", repo));
    }

    /**
     * Checks that a student in the group is able to edit the repo information
     * @throws Exception Exception thrown during mockmvc runtime
     */
    @Test
    void givenGroupExists_andUserIsAMember_whenEditRepoCalled_thenRepoEdited() throws Exception{
        User user = new User.Builder().userId(1).build();
        Groups group = new Groups("Team: 400", "Bad Request", 1, List.of(user));
        Repo repo = new Repo(1, 1, group.getShortName() + "'s repo", 0, null, "https://gitlab.com");

        when(PrincipalUtils.checkUserIsTeacherOrAdmin(any())).thenReturn(false);
        when(PrincipalUtils.getUserId(any())).thenReturn(1);
        when(groupService.getGroupById(anyInt())).thenReturn(group);
        when(repoService.getRepoByGroup(anyInt())).thenReturn(repo);

        mockMvc
            .perform(post("/repo/1/save").contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("repoId", "1")
            .param("groupId", "1")
            .param("repoName", "Team: 400's repo")
            .param("gitlabProjectId", "0")
            .param("accessToken", "")
            .param("hostAddress", "https://gitlab.com"))
            .andExpect(status().isOk());
    }

    /**
     * Checks that a student not in the group is unable able to edit the repo information
     * @throws Exception Exception thrown during mockmvc runtime
     */
    @Test
    void givenGroupExists_andUserIsNotAMember_whenEditRepoCalled_thenRepoEdited() throws Exception{
        Groups group = new Groups("Team: 400", "Bad Request", 1, List.of());
        Repo repo = new Repo(1, 1, group.getShortName() + "'s repo", 0, null, "https://gitlab.com");

        when(PrincipalUtils.checkUserIsTeacherOrAdmin(any())).thenReturn(false);
        when(PrincipalUtils.getUserId(any())).thenReturn(1);
        when(groupService.getGroupById(anyInt())).thenReturn(group);
        when(repoService.getRepoByGroup(anyInt())).thenReturn(repo);

        mockMvc
            .perform(post("/repo/1/save").contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("repoId", "1")
            .param("groupId", "1")
            .param("repoName", "Team: 400's repo")
            .param("gitlabProjectId", "0")
            .param("accessToken", "")
            .param("hostAddress", "https://gitlab.com"))
            .andExpect(status().isForbidden());
    }

    /**
     * Asserts that a bad request is given when repo isn't valid
     * @throws Exception Exception thrown during mockmvc runtime
     */
    @Test
    void givenInvalidGroupRepo_andUserIsNotAMember_whenSaveRepoCalled_thenBadRequestReceived() throws Exception{
        User user = new User.Builder().userId(1).build();
        Groups group = new Groups("Team: 400", "Bad Request", 1, List.of(user));
        Repo repo = new Repo(1, 1, group.getShortName() + "'s repo", 0, null, "https://gitlab.com");

        when(PrincipalUtils.checkUserIsTeacherOrAdmin(any())).thenReturn(false);
        when(PrincipalUtils.getUserId(any())).thenReturn(1);
        when(groupService.getGroupById(anyInt())).thenReturn(group);
        when(repoService.getRepoByGroup(anyInt())).thenReturn(repo);
        when(repoService.saveRepo(any())).thenThrow(new IncorrectDetailsException("Failure saving repo"));

        mockMvc
                .perform(post("/repo/1/save").contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("repoId", "1")
                        .param("groupId", "1")
                        .param("repoName", "Team: 400's repo")
                        .param("gitlabProjectId", "0")
                        .param("accessToken", "")
                        .param("hostAddress", "https://gitlab.com"))
                .andExpect(status().isBadRequest());
    }
}
