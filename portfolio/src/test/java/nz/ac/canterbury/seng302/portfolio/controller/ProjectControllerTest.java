package nz.ac.canterbury.seng302.portfolio.controller;

import com.google.protobuf.Timestamp;
import nz.ac.canterbury.seng302.portfolio.model.*;
import nz.ac.canterbury.seng302.portfolio.service.*;
import nz.ac.canterbury.seng302.portfolio.utils.IncorrectDetailsException;
import nz.ac.canterbury.seng302.portfolio.utils.PrincipalUtils;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;

import org.junit.After;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProjectController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SprintService sprintService;

    @MockBean
    private ProjectService projectService;

    @MockBean
    private EventService eventService;

    @MockBean
    private DashboardService dashboardService;

    @MockBean
    private UserAccountClientService userAccountClientService;

    private List<Sprint> testSprintList;

    private Project project;

    private User user;

    private UserResponse.Builder userResponse;

    private List<String> testList;

    private Sprint testSprint;

    private static MockedStatic<PrincipalUtils> utilities;

    @BeforeAll
    private static void beforeAllInit() {
        utilities = Mockito.mockStatic(PrincipalUtils.class);
        utilities.when(() -> PrincipalUtils.checkUserIsTeacherOrAdmin(any())).thenReturn(true);
    }


    @BeforeEach
    public void beforeEachInit() {

        testList = new ArrayList<String>();
        testSprintList = new ArrayList<Sprint>();
        testList.add("testRole");
        when(PrincipalUtils.getUserRole(any())).thenReturn(testList);
        LocalDate now = LocalDate.now();
        project = new Project(1, "Test Project", "test", java.sql.Date.valueOf(now), java.sql.Date.valueOf(now.plusDays(50)));
        user = new User.Builder()
                .userId(0)
                .username("TimeTester")
                .firstName("Time")
                .lastName("Tester")
                .email("Test@tester.nz")
                .creationDate(new Date())
                .build();

        testSprint = new Sprint.Builder()
                .sprintLabel("Sprint 1")
                .sprintName("Sprint 1")
                .description("Attempt 1")
                .project(project)
                .startDate(new java.sql.Date(2021,1,1))
                .endDate(new java.sql.Date(2021, 3, 1))
                .build();
        testSprintList.add(testSprint);
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
        utilities.close();
    }

    /**
     * Tests that the project page is returned with correct information when called.
     * @throws Exception Thrown during mockmvc run time
     */
    @Test
    public void givenServer_WhenNavigateToProjectPage_ThenProjectPageReturned() throws Exception{
        Event newEvent = new Event.Builder().eventName("Test").build();
        when(sprintService.getSprintByProject(anyInt())).thenReturn(testSprintList);
        when(projectService.getProjectById(anyInt())).thenReturn(project);
        when(userAccountClientService.getUser(any())).thenReturn(userResponse.build());
        when(eventService.getNewEvent(any())).thenReturn(newEvent);

        this.mockMvc
                .perform(get("/project/1"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("listSprints", testSprintList))
                .andExpect(model().attribute("listEvents", List.of()))
                .andExpect(model().attribute("project", project))
                .andExpect(model().attribute("event", newEvent))
                .andExpect(model().attribute("roles", testList))
                .andExpect(model().attribute("user", userResponse.build()))
                .andExpect(model().attribute("projectDateMin", project.getStartDate()))
                .andExpect(model().attribute("projectDateMax", project.getEndDate()))
                .andExpect(view().name("project"));
    }

    /**
     * Tests that the user is redirected to the dashboard if trying to view a non-existing project
     * @throws Exception Thrown during mockmvc runtime
     */
    @Test
    public void givenIncorrectDetails_whenNavigateToProjectPage_thenDashboardReturned() throws Exception {
        when(projectService.getProjectById(anyInt())).thenThrow(new IncorrectDetailsException("Project not found"));
        this.mockMvc
                .perform(get("/project/9999"))
                .andExpect(flash().attribute("messageDanger", "Project not found"))
                .andExpect(view().name("redirect:/dashboard"));
    }

    /**
     * Test verification of project dates and check that it returns the correct response.
     * @throws Exception Thrown during mockmvc run time
     */
    @Test
    void givenServer_WhenVerifyProject_ThenProjectVerifiedSuccessfully() throws Exception{
        this.mockMvc
                .perform(post("/verifyProject/1?" + "startDate=" + project.getStartDate() + "&endDate=" + project.getEndDate()))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    /**
     * Test get all sprints and check that it returns the correct response.
     * @throws Exception Thrown during mockmvc run time
     */
    @Test
    void givenServer_WhenGetAllSprints_ThenSprintsReturnedSuccessfully() throws Exception{
        when(sprintService.getSprintByProject(anyInt())).thenReturn(testSprintList);
        this.mockMvc
                .perform(get("/project/1/getAllSprints"))
                .andExpect(status().isOk());
    }
}
