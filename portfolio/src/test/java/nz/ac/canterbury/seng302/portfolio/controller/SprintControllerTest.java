package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.*;
import nz.ac.canterbury.seng302.portfolio.service.*;
import nz.ac.canterbury.seng302.portfolio.utils.IncorrectDetailsException;
import nz.ac.canterbury.seng302.portfolio.utils.PrincipalUtils;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    }

    @AfterAll
    public static void afterAll() {
        utilities.close();
    }

    /**
     * Test get sprints and check that it returns the correct response.
     * @throws Exception Thrown during mockmvc run time
     */
    @Test
    void givenServer_WhenGetSprints_ThenSprintsReturnedSuccessfully() throws Exception{
        when(sprintService.getSprintsByProject(anyInt())).thenReturn(List.of(new Sprint()));

        this.mockMvc
                .perform(get("/project/1/sprints"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("listSprints", List.of(new Sprint())));
    }

    @Test
    void givenServer_WhenGetSprintAccordion_ThenElementsReturnedCorrectly() throws Exception{
        when(sprintService.getSprint(anyInt())).thenReturn(new Sprint());
        when(projectService.getProjectById(anyInt())).thenReturn(new Project());

        when(sprintService.getSprintsByProject(anyInt())).thenReturn(List.of(new Sprint()));
        when(eventService.getEventsBySprintId(anyInt())).thenReturn(List.of(new Event()));
        when(deadlineService.getDeadlinesBySprintId(anyInt())).thenReturn(List.of(new Deadline()));


        this.mockMvc
                .perform(get("/project/1/sprint/1/accordion"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("listEvents", List.of(new Event())))
                .andExpect(model().attribute("listDeadlines", List.of(new Deadline())));
    }

    @Test
    void givenSprintDoesNotExist_whenGetSprintAccordion_thenErrorPage() throws Exception{
        when(sprintService.getSprint(anyInt())).thenThrow(new IncorrectDetailsException("Sprint doesn't exist"));

        this.mockMvc
            .perform(get("/project/1/sprint/1/accordion"))
            .andExpect(status().isNotFound());
    }
}
