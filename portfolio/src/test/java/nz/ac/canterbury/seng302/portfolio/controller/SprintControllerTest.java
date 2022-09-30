package nz.ac.canterbury.seng302.portfolio.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import nz.ac.canterbury.seng302.portfolio.model.*;
import nz.ac.canterbury.seng302.portfolio.model.dto.SprintDTO;
import nz.ac.canterbury.seng302.portfolio.service.*;
import nz.ac.canterbury.seng302.portfolio.utils.IncorrectDetailsException;
import nz.ac.canterbury.seng302.portfolio.utils.PrincipalUtils;
import nz.ac.canterbury.seng302.portfolio.utils.SprintColor;

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
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = SprintController.class)
@AutoConfigureMockMvc(addFilters = false)
public class SprintControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SimpMessagingTemplate template;

    @MockBean
    private SprintService sprintService;

    @MockBean
    private ProjectService projectService;

    @MockBean
    private UserAccountClientService userAccountClientService;

    @MockBean
    private DeadlineService deadlineService;

    @MockBean
    private EventService eventService;

    private static MockedStatic<PrincipalUtils> utilities;

    @BeforeAll
    private static void beforeAllInit() {
        utilities = Mockito.mockStatic(PrincipalUtils.class);
        utilities.when(() -> PrincipalUtils.checkUserIsTeacherOrAdmin(any())).thenReturn(true);
    }

    @BeforeEach
    public void beforeEachInit() {
        when(PrincipalUtils.checkUserIsTeacherOrAdmin(any())).thenReturn(true);
    }

    @AfterAll
    public static void afterAll() {
        utilities.close();
    }

    private SprintDTO toDTO(Sprint sprint) {
        return new SprintDTO(sprint.getSprintId(), sprint.getProject(), sprint.getSprintLabel(), sprint.getSprintName(),
                sprint.getDescription(), sprint.getStartDate(), sprint.getEndDate(), sprint.getColor());
    }

    /**
     * Test get new sprint page and check that it returns the correct response.
     * 
     * @throws Exception Thrown during mockmvc run time
     */
    @Test
    void givenServer_WhenGetNewSprint_ThenNewSprintPageReturnedSuccessfully() throws Exception {
        Project project = new Project(1, "projectName", "description", new Date(5), new Date(10));
        Sprint sprint = new Sprint(1, project, "sprintLabel", "sprintName", "description", new Date(5), new Date(10), SprintColor.BLUE);

        when(projectService.getProjectById(1)).thenReturn(project);
        when(sprintService.getNewSprint(any())).thenReturn(sprint);
        when(sprintService.getSprintDateRange(any(), any())).thenReturn(List.of("123", "123"));

        this.mockMvc
                .perform(get("/project/1/newSprint"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("submissionName", "Create"));
    }

    /**
     * Test get new sprint page as a student and check that it returns the correct response.
     * 
     * @throws Exception Thrown during mockmvc run time
     */
    @Test
    void givenStudent_WhenGetNewSprint_ThenRedirectedToDashboard() throws Exception {
        when(PrincipalUtils.checkUserIsTeacherOrAdmin(any())).thenReturn(false);

        this.mockMvc
                .perform(get("/project/1/newSprint"))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Test get edit sprint page and check that it returns the correct response.
     * 
     * @throws Exception Thrown during mockmvc run time
     */
    @Test
    void givenServer_WhenGetEditSprint_ThenEditSprintPageReturnedSuccessfully() throws Exception {
        Project project = new Project(1, "projectName", "description", new Date(5), new Date(10));
        Sprint sprint = new Sprint(1, project, "sprintLabel", "sprintName", "description", new Date(5), new Date(10), SprintColor.BLUE);

        when(projectService.getProjectById(1)).thenReturn(project);
        when(sprintService.getSprint(anyInt())).thenReturn(sprint);

        this.mockMvc
                .perform(get("/project/1/editSprint/1"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("submissionName", "Save"));
    }

    /**
     * Test get edit sprint page as a student and check that it returns the correct response.
     * 
     * @throws Exception Thrown during mockmvc run time
     */
    @Test
    void givenStudent_WhenGetEditSprint_ThenRedirectsToDashboard() throws Exception {
        when(PrincipalUtils.checkUserIsTeacherOrAdmin(any())).thenReturn(false);

        this.mockMvc
                .perform(get("/project/1/editSprint/1"))
                .andExpect(status().is3xxRedirection());
    }


    /**
     * Test get sprints and check that it returns the correct response.
     * 
     * @throws Exception Thrown during mockmvc run time
     */
    @Test
    void givenServer_WhenGetSprints_ThenSprintsReturnedSuccessfully() throws Exception {
        when(sprintService.getSprintsByProject(anyInt())).thenReturn(List.of(new Sprint()));

        this.mockMvc
                .perform(get("/project/1/sprints"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("listSprints", List.of(new Sprint())));
    }

    /**
     * Tests that the sprint accordion will be returned with the values provided
     * 
     * @throws Exception
     */
    @Test
    void givenServer_WhenGetSprintAccordion_ThenElementsReturnedCorrectly() throws Exception {
        when(sprintService.getSprint(anyInt())).thenReturn(new Sprint());
        when(projectService.getProjectById(anyInt())).thenReturn(new Project());

        when(sprintService.getSprintsByProject(anyInt())).thenReturn(List.of(new Sprint()));
        when(eventService.getEventsBySprintId(anyInt())).thenReturn(List.of(new Event()));
        when(deadlineService.getDeadlinesBySprintId(anyInt())).thenReturn(List.of(new Deadline()));

        List<String> testNames = new ArrayList<String>();
        testNames.add("TestName 1");
        testNames.add("TestName 2");

        Map<Integer, List<String>> testData = new HashMap<Integer, List<String>>();
        testData.put(0, testNames);
        when(eventService.getSprintLabelsForStartAndEndDates(any())).thenReturn(testData);

        Map<Integer, String> deadlineDateMapping = new HashMap<Integer, String>();
        deadlineDateMapping.put(0, "Test name");
        when(deadlineService.getSprintOccurringOnDeadlines(any())).thenReturn(deadlineDateMapping);

        this.mockMvc
                .perform(get("/project/1/sprint/1/accordion"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("listEvents", List.of(new Event())))
                .andExpect(model().attribute("listDeadlines", List.of(new Deadline())));
    }

    /**
     * Tests that the sprint accordion will return an error page if it tries to get
     * a sprint that doesn't exist
     * 
     * @throws Exception
     */
    @Test
    void givenSprintDoesNotExist_whenGetSprintAccordion_thenErrorPage() throws Exception {
        when(sprintService.getSprint(anyInt())).thenThrow(new IncorrectDetailsException("Sprint doesn't exist"));

        this.mockMvc
                .perform(get("/project/1/sprint/1/accordion"))
                .andExpect(status().isNotFound());
    }

    /**
     * Tests that the only sprint in a project will be verified successfully
     * @throws Exception
     */
    @Test
    void givenValidSprint_whenVerifySprint_thenStatusOk() throws Exception {
        LocalDate now = LocalDate.now();
        SprintDTO sprint = new SprintDTO(1, null, "Test1", "Test Sprint", "This is a description",
                java.sql.Date.valueOf(now), java.sql.Date.valueOf(now.plusDays(30)), SprintColor.BLUE);

        String json  = new ObjectMapper().writeValueAsString(sprint);

        this.mockMvc.perform(post("/project/1/verifySprint").contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    /**
     * Tests that students are unable to request verification
     * @throws Exception
     */
    @Test
    void givenStudentUser_whenVerifySprint_thenForbidden() throws Exception {
        when(PrincipalUtils.checkUserIsTeacherOrAdmin(any())).thenReturn(false);
        LocalDate now = LocalDate.now();
        SprintDTO sprint = new SprintDTO(1, null, "Sprint 1", "Test Sprint", "This is a description",
                java.sql.Date.valueOf(now), java.sql.Date.valueOf(now.plusDays(30)), SprintColor.BLUE);

        String json  = new ObjectMapper().writeValueAsString(sprint);
        this.mockMvc.perform(post("/project/1/verifySprint").contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isForbidden());
    }

    /**
     * Tests that the only sprint in a project will be verified successfully
     * @throws Exception
     */
    @Test
    void givenInvalidSprint_whenVerifySprint_thenStatusInvalid() throws Exception {
        LocalDate now = LocalDate.now();
        SprintDTO sprint = new SprintDTO(1, null, "Sprint 1", "Test Sprint", "This is a description",
                java.sql.Date.valueOf(now), java.sql.Date.valueOf(now.plusDays(30)), SprintColor.BLUE);

        String json  = new ObjectMapper().writeValueAsString(sprint);

        when(sprintService.verifySprint(any())).thenThrow(new IncorrectDetailsException("Invalid Sprint"));

        this.mockMvc.perform(post("/project/1/verifySprint").contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid Sprint"));
    }

    /**
     * Tests that students are unable to save a sprint
     * @throws Exception
     */
    @Test
    void givenStudentUser_whenSaveSprint_thenForbidden() throws Exception {
        when(PrincipalUtils.checkUserIsTeacherOrAdmin(any())).thenReturn(false);
        this.mockMvc.perform(post("/project/1/sprint").flashAttr("sprintDTO", toDTO(new Sprint())))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/dashboard"));
    }

    /**
     * Tests that a sprint redirects to the project page on successful saving
     * @throws Exception
     */
    @Test
    void givenValidSprint_whenSaveSprint_thenSprintSaved() throws Exception {
        LocalDate now = LocalDate.now();
        Sprint sprint = new Sprint(1, new Project(), "Sprint 1", "Test Sprint", "This is a description",
        java.sql.Date.valueOf(now), java.sql.Date.valueOf(now.plusDays(30)), SprintColor.BLUE);

        this.mockMvc.perform(post("/project/1/sprint").flashAttr("sprintDTO", toDTO(sprint)))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/project/1"));
    }

    /**
     * Tests that a sprint redirects to the project page on successful saving
     * @throws Exception
     */
    @Test
    void givenInvalidSprint_whenSaveSprint_thenErrorMessagePresent() throws Exception {
        LocalDate now = LocalDate.now();
        Sprint sprint = new Sprint(1, new Project(), "Sprint 1", "Test Sprint", "This is a description",
        java.sql.Date.valueOf(now), java.sql.Date.valueOf(now.plusDays(30)), SprintColor.BLUE);
        when(sprintService.verifySprint(any())).thenThrow(new IncorrectDetailsException("Invalid Sprint"));

        this.mockMvc.perform(post("/project/1/sprint").flashAttr("sprintDTO", toDTO(sprint)))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/project/1"))
                .andExpect(flash().attribute("messageDanger", "Invalid Sprint"));
    }

    /**
     * Tests that students are unable to edit sprint dates
     * @throws Exception
     */
    @Test
    void givenStudentUser_whenEditSprint_thenForbidden() throws Exception {
        when(PrincipalUtils.checkUserIsTeacherOrAdmin(any())).thenReturn(false);

        LocalDate now = LocalDate.now();
        this.mockMvc.perform(post("/sprint/1/editSprint").flashAttr("startDate", Date.valueOf(now)).flashAttr("endDate", Date.valueOf(now.plusDays(30))))
                .andExpect(status().isForbidden());
    }

    /**
     * Tests that a sprint with valid dates can be saved
     * @throws Exception
     */
    @Test
    void givenValidSprintDates_whenEditSprint_thenSprintSaved() throws Exception {   
        when(sprintService.getSprint(anyInt())).thenReturn(new Sprint());
        LocalDate now = LocalDate.now();
        this.mockMvc.perform(post("/sprint/1/editSprint").flashAttr("startDate", Date.valueOf(now)).flashAttr("endDate", Date.valueOf(now.plusDays(30))))
                .andExpect(status().isOk());
    }
    
    /**
     * Tests that a sprint with invalid dates can't be saved
     * @throws Exception
     */
    @Test
    void givenValidSprintDates_whenEditSprint_thenBadRequest() throws Exception {   
        when(sprintService.getSprint(anyInt())).thenReturn(new Sprint());
        when(sprintService.verifySprint(any())).thenThrow(new IncorrectDetailsException("Invalid Sprint"));
        LocalDate now = LocalDate.now();
        this.mockMvc.perform(post("/sprint/1/editSprint").flashAttr("startDate", Date.valueOf(now)).flashAttr("endDate", Date.valueOf(now.plusDays(30))))
                .andExpect(status().isBadRequest());
    }

    /**
     * Tests that students are unable to delete a sprint
     * @throws Exception
     */
    @Test
    void givenStudentUser_whenDeleteSprint_thenForbidden() throws Exception {
        when(PrincipalUtils.checkUserIsTeacherOrAdmin(any())).thenReturn(false);
        this.mockMvc.perform(post("/project/1/deleteSprint/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/dashboard"));
    }

    /**
     * Tests that a sprint redirects to the project page on successful deletion
     * @throws Exception
     */
    @Test
    void givenValidSprint_whenDeleteSprint_thenSprintDeleted() throws Exception {

        this.mockMvc.perform(post("/project/1/deleteSprint/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/project/1"));
    }

    /**
     * Tests that a sprint redirects to the project page on unsuccessful deletion
     * @throws Exception
     */
    @Test
    void givenInvalidSprint_whenDeleteSprint_thenErrorMessagePresent() throws Exception {
        when(sprintService.deleteSprint(anyInt())).thenThrow(new IncorrectDetailsException("Invalid Sprint"));

        this.mockMvc.perform(post("/project/1/deleteSprint/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/project/1"))
                .andExpect(flash().attribute("messageDanger", "Invalid Sprint"));
    }
}
