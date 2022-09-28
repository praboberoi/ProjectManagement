package nz.ac.canterbury.seng302.portfolio.controller;

import com.google.protobuf.Timestamp;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.model.User;
import nz.ac.canterbury.seng302.portfolio.service.DashboardService;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import nz.ac.canterbury.seng302.portfolio.service.SprintService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.portfolio.utils.PrincipalUtils;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = DashboardController.class)
@AutoConfigureMockMvc(addFilters = false)
public class DashboardControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SprintService sprintService;

    @MockBean
    private ProjectService projectRepository;

    @MockBean
    private DashboardService dashboardService;

    @MockBean
    private UserAccountClientService userAccountClientService;

    private List<Project> listProjects;

    private User user;

    private UserResponse.Builder userResponse;

    private List<String> testList;

    private Project defaultProject;

    private static MockedStatic<PrincipalUtils> mockedPrincipalUtils;

    @BeforeAll
    private static void beforeAllInit() {
        mockedPrincipalUtils = mockStatic(PrincipalUtils.class);
    }


    @BeforeEach
    void beforeEach() {
        when(PrincipalUtils.checkUserIsTeacherOrAdmin(any())).thenReturn(true);
        testList = new ArrayList<String>();
        testList.add("testRole");
        when(PrincipalUtils.getUserRole(any())).thenReturn(testList);
        LocalDate now = LocalDate.now();
        listProjects = new ArrayList<Project>();
        defaultProject = new Project.Builder()
                .projectName("Project " + now.getYear())
                .startDate(Date.valueOf(now))
                .endDate(Date.valueOf(now.plusMonths(8)))
                .build();;

        listProjects.add(defaultProject);

        user = new User.Builder()
                .userId(0)
                .username("Username")
                .firstName("Time")
                .lastName("Tester")
                .email("Test@tester.nz")
                .creationDate(new java.util.Date())
                .build();

        userResponse = UserResponse.newBuilder();
        userResponse.setUsername(user.getUsername());
        userResponse.setFirstName(user.getFirstName());
        userResponse.setLastName(user.getLastName());
        userResponse.setEmail(user.getEmail());
        userResponse.setCreated(Timestamp.newBuilder()
                .setSeconds(user.getDateCreated().getTime())
                .build());
    }

    @AfterAll
    public static void afterAll() {
        mockedPrincipalUtils.close();
    }

    /**
     * Test that the dashboard with a list of projects is returned when showProjectList called.
     * @throws Exception Thrown during mockmvc run time
     */
    @Test
    void givenServer_WhenShowProjectList_ThenDashboardWithProjectListReturned() throws Exception{
        when(dashboardService.getAllProjects()).thenReturn(listProjects);
        when(userAccountClientService.getUser(any())).thenReturn(userResponse.build());
        this.mockMvc
                .perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("listProjects", listProjects))
                .andExpect(model().attribute("roles", testList))
                .andExpect(model().attribute("user", new User(userResponse.build())));
    }

    /**
     * Test that the projectForm with required model attributes is returned when showNewForm called.
     * @throws Exception Thrown during mockmvc run time
     */
    @Test
    void givenServer_WhenShowNewForm_ThenProjectFormWithAttributesReturned() throws Exception{
        when(dashboardService.getNewProject()).thenReturn(defaultProject);
        List<Date> dateRange = List.of(Date.valueOf(defaultProject.getStartDate().toLocalDate().minusYears(1)), Date.valueOf(listProjects.get(0).getStartDate().toLocalDate().plusYears(10)));
        when(dashboardService.getProjectDateRange(any())).thenReturn(dateRange);
        when(userAccountClientService.getUser(any())).thenReturn(userResponse.build());
        this.mockMvc
                .perform(get("/dashboard/newProject"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("project", defaultProject))
                .andExpect(model().attribute("pageTitle", "Add New Project"))
                .andExpect(model().attribute("user", new User(userResponse.build())))
                .andExpect(model().attribute("projectStartDateMin", dateRange.get(0)))
                .andExpect(model().attribute("projectStartDateMax", Date.valueOf(defaultProject.getEndDate().toLocalDate().minusDays(1))))
                .andExpect(model().attribute("projectEndDateMin", Date.valueOf(defaultProject.getStartDate().toLocalDate().plusDays(1))))
                .andExpect(model().attribute("projectEndDateMax", dateRange.get(1)));
    }

    /**
     * Test verification of event object and check that it redirect the user to the project page.
     * @throws Exception Thrown during mockmvc run time
     */
    @Test
    void givenServer_WhenSaveValidProject_ThenProjectSavedSuccessfully() throws Exception{
        when(sprintService.getSprintsByProject(anyInt())).thenReturn(new ArrayList<Sprint>());
        when(dashboardService.saveProject(any())).thenReturn("Successfully Created " + defaultProject.getProjectName());
        this.mockMvc
                .perform(post("/dashboard/saveProject").flashAttr("project", defaultProject))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("messageDanger", nullValue()))
                .andExpect(flash().attribute("messageSuccess", "Successfully Created " + defaultProject.getProjectName()));
    }

    /**
     * Test that the projectForm with required model attributes is returned when showEditForm called.
     * @throws Exception Thrown during mockmvc run time
     */
    @Test
    void givenServer_WhenShowEditForm_ThenProjectFormWithAttributesReturned() throws Exception{
        when(dashboardService.getProject(anyInt())).thenReturn(defaultProject);
        List<Date> dateRange = List.of(Date.valueOf(defaultProject.getStartDate().toLocalDate().minusYears(1)), Date.valueOf(listProjects.get(0).getStartDate().toLocalDate().plusYears(10)));
        when(dashboardService.getProjectDateRange(any())).thenReturn(dateRange);
        when(userAccountClientService.getUser(any())).thenReturn(userResponse.build());
        this.mockMvc
                .perform(get("/dashboard/editProject/1"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("project", defaultProject))
                .andExpect(model().attribute("pageTitle", "Edit Project: " + defaultProject.getProjectName()))
                .andExpect(model().attribute("submissionName", "Save"))
                .andExpect(model().attribute("user", new User(userResponse.build())))
                .andExpect(model().attribute("projectStartDateMin", dateRange.get(0)))
                .andExpect(model().attribute("projectStartDateMax", Date.valueOf(defaultProject.getEndDate().toLocalDate().minusDays(1))))
                .andExpect(model().attribute("projectEndDateMin", Date.valueOf(defaultProject.getStartDate().toLocalDate().plusDays(1))))
                .andExpect(model().attribute("projectEndDateMax", dateRange.get(1)));
    }

    /**
     * Test project is deleted and user redirected to dashboard with required model attributes when delete project called.
     * @throws Exception Thrown during mockmvc run time
     */
    @Test
    void givenServer_WhenDeleteProject_ThenProjectDeleted_AndDashboardWithAttributesReturned() throws Exception {
        when(dashboardService.getProject(anyInt())).thenReturn(defaultProject);
        doNothing().when(dashboardService).deleteProject(anyInt());
        when(userAccountClientService.getUser(any())).thenReturn(userResponse.build());
        this.mockMvc
                .perform(post("/dashboard/deleteProject/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("messageSuccess", "Successfully Deleted " + defaultProject.getProjectName()));
    }
}
