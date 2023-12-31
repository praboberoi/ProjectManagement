package nz.ac.canterbury.seng302.portfolio.controller;

import com.google.protobuf.Timestamp;
import nz.ac.canterbury.seng302.portfolio.model.*;
import nz.ac.canterbury.seng302.portfolio.model.dto.ProjectDTO;
import nz.ac.canterbury.seng302.portfolio.service.*;
import nz.ac.canterbury.seng302.portfolio.utils.IncorrectDetailsException;
import nz.ac.canterbury.seng302.portfolio.utils.PrincipalUtils;
import nz.ac.canterbury.seng302.portfolio.utils.SprintColor;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
    private DeadlineService deadlineService;

    @MockBean
    private MilestoneService milestoneService;

    @MockBean
    private UserAccountClientService userAccountClientService;

    @MockBean
    private SimpMessagingTemplate template;

    private List<Sprint> testSprintList;

    private Project project;

    private Project invalidProject;

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
        when(PrincipalUtils.checkUserIsTeacherOrAdmin(any())).thenReturn(true);
        testList = new ArrayList<String>();
        testSprintList = new ArrayList<Sprint>();
        testList.add("testRole");
        when(PrincipalUtils.getUserRole(any())).thenReturn(testList);
        LocalDate now = LocalDate.now();
        project = new Project(1, "Test Project", "test", java.sql.Date.valueOf(now), java.sql.Date.valueOf(now.plusDays(50)));
        invalidProject = new Project(1, "", "test", java.sql.Date.valueOf(now), java.sql.Date.valueOf(now.plusDays(50)));
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
                .color(SprintColor.BLUE)
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

    public ProjectDTO toDTO(Project project) {
		return new ProjectDTO(
				project.getProjectId(),
				project.getProjectName(),
				project.getDescription(),
				project.getStartDate(),
                project.getEndDate());
	}

    /**
     * Tests that the project page is returned with correct information when called.
     * @throws Exception Thrown during mockmvc run time
     */
    @Test
    void givenServer_WhenNavigateToProjectPage_ThenProjectPageReturned() throws Exception{
        Deadline newDeadline = new Deadline.Builder().project(project).name("Test").build();
        Milestone newMilestone = new Milestone.Builder().project(project).name("Test").build();
        Event newEvent = new Event.Builder().eventName("Test").build();
        when(sprintService.getSprintsByProject(anyInt())).thenReturn(testSprintList);
        when(projectService.getProjectById(anyInt())).thenReturn(project);
        when(eventService.getNewEvent(any())).thenReturn(newEvent);
        when(deadlineService.getNewDeadline(any())).thenReturn(newDeadline);
        when(milestoneService.getNewMilestone(any())).thenReturn(newMilestone);

        this.mockMvc
                .perform(get("/project/1"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("listSprints", testSprintList))
                .andExpect(model().attribute("listEvents", List.of()))
                .andExpect(model().attribute("listDeadlines", List.of()))
                .andExpect(model().attribute("deadline", newDeadline))
                .andExpect(model().attribute("milestone", newMilestone))
                .andExpect(model().attribute("project", project))
                .andExpect(model().attribute("event", newEvent))
                .andExpect(view().name("project"));
    }

    /**
     * Tests that the user is redirected to the dashboard if trying to view a non-existing project
     * @throws Exception Thrown during mockmvc runtime
     */
    @Test
    void givenIncorrectDetails_whenNavigateToProjectPage_thenDashboardReturned() throws Exception {
        when(projectService.getProjectById(anyInt())).thenThrow(new IncorrectDetailsException("Project not found"));
        this.mockMvc
                .perform(get("/project/9999"))
                .andExpect(flash().attribute("messageDanger", "Project not found"))
                .andExpect(view().name("redirect:/dashboard"));
    }

    /**
     * Tests that the user is redirected to the dashboard if trying to view a non-existing project
     * @throws Exception Thrown during mockmvc runtime
     */
    @Test
    void whenProjectCalled_thenListOfProjectReturned() throws Exception {
        when(projectService.getAllProjects()).thenReturn(List.of());
        this.mockMvc
                .perform(get("/projects/"))
                .andExpect(view().name("dashboard::projectList"))
                .andExpect(model().attribute("listProjects", List.of()));
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
        when(sprintService.getSprintsByProject(anyInt())).thenReturn(testSprintList);
        this.mockMvc
                .perform(get("/project/1/getAllSprints"))
                .andExpect(status().isOk());
    }

    /**
     * Test project can be saved correctly.
     * @throws Exception Thrown during mockmvc run time
     */
    @Test
    void givenServer_WhenSaveValidProject_ThenProjectSavedSuccessfully() throws Exception{
        when(sprintService.getSprintsByProject(anyInt())).thenReturn(new ArrayList<Sprint>());
        when(projectService.saveProject(any())).thenReturn("Project created successfully");

        this.mockMvc
                .perform(post("/project/").flashAttr("projectDTO", toDTO(project)))
                .andExpect(status().isOk())
                .andExpect(content().string("Project created successfully"));
    }

    /**
     * Test project can be saved correctly.
     * @throws Exception Thrown during mockmvc run time
     */
    @Test
    void givenInvalidProject_WhenSaveValidProject_ThenProjectSavedSuccessfully() throws Exception{
        when(sprintService.getSprintsByProject(anyInt())).thenReturn(new ArrayList<Sprint>());
        when(projectService.saveProject(any())).thenReturn("Project created successfully");

        this.mockMvc
                .perform(post("/project/").flashAttr("projectDTO", toDTO(invalidProject)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Project Name must not be empty or greater than 50 characters."));
    }

    /**
     * Test project can not be saved by a student.
     * @throws Exception Thrown during mockmvc run time
     */
    @Test
    void givenStudent_WhenSaveProject_ThenErrorThrown() throws Exception{
        when(sprintService.getSprintsByProject(anyInt())).thenReturn(new ArrayList<Sprint>());
        when(projectService.saveProject(any())).thenReturn("Project created successfully");
        when(PrincipalUtils.checkUserIsTeacherOrAdmin(any())).thenReturn(false);

        this.mockMvc
                .perform(post("/project/").flashAttr("projectDTO", toDTO(new Project())))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Insufficient Permissions"));
    }

    /**
     * Test project is deleted when delete project called.
     * @throws Exception Thrown during mockmvc run time
     */
    @Test
    void givenServer_WhenDeleteProject_ThenProjectDeleted() throws Exception {
        when(projectService.getProject(anyInt())).thenReturn(new Project());
        when(userAccountClientService.getUser(any())).thenReturn(userResponse.build());
        this.mockMvc
                .perform(delete("/project/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfully Deleted " + null));
    }

    /**
     * Tests that students are unable to delete a project
     * @throws Exception Thrown during mockmvc run time
     */
    @Test
    void givenStudent_WhenDeleteProject_ThenErrorThrown() throws Exception {
        when(PrincipalUtils.checkUserIsTeacherOrAdmin(any())).thenReturn(false);

        this.mockMvc
                .perform(delete("/project/1"))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Insufficient Permissions"));
    }

    /**
     * Tests that students are unable to delete a project
     * @throws Exception Thrown during mockmvc run time
     */
    @Test
    void givenNoProject_WhenDeleteProject_ThenErrorThrown() throws Exception {
        when(projectService.getProject(anyInt())).thenThrow(new IncorrectDetailsException("Project not found"));

        this.mockMvc
                .perform(delete("/project/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Project not found"));
    }
}
