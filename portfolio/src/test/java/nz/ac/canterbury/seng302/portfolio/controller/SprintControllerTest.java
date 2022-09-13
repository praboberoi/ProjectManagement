package nz.ac.canterbury.seng302.portfolio.controller;


import com.google.protobuf.Timestamp;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.model.User;
import nz.ac.canterbury.seng302.portfolio.model.dto.SprintDTO;
import nz.ac.canterbury.seng302.portfolio.service.EventService;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import nz.ac.canterbury.seng302.portfolio.service.SprintService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.portfolio.utils.IncorrectDetailsException;
import nz.ac.canterbury.seng302.portfolio.utils.PrincipalUtils;
import nz.ac.canterbury.seng302.portfolio.utils.SprintColor;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.sql.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(controllers = SprintController.class)
@AutoConfigureMockMvc(addFilters = false)
class SprintControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectService projectService;

    @MockBean
    private SprintService sprintService;

    @MockBean
    private static UserAccountClientService userAccountClientService;

    @MockBean
    private EventService eventService;

    @MockBean
    private SimpMessagingTemplate template;


    private static MockedStatic<PrincipalUtils> utilities;

    private static Project project;

    private static Sprint sprint1;

    private static Sprint sprint2;

    private static User user;

    private static UserResponse.Builder reply;

    private MvcResult result;


    @BeforeAll
    private static void initialise() {
        utilities = Mockito.mockStatic(PrincipalUtils.class);
        utilities.when(() -> PrincipalUtils.checkUserIsTeacherOrAdmin(any())).thenReturn(true);

         user = new User.Builder()
                .userId(0)
                .username("TimeTester")
                .firstName("Time")
                .lastName("Tester")
                .email("Test@tester.nz")
                .creationDate(new java.util.Date())
                .build();

        reply = UserResponse.newBuilder();
        reply.setUsername(user.getUsername());
        reply.setFirstName(user.getFirstName());
        reply.setLastName(user.getLastName());
        reply.setEmail(user.getEmail());
        reply.setCreated(Timestamp.newBuilder()
                .setSeconds(user.getDateCreated().getTime())
                .build());

        project = new Project.Builder()
                .description("This is a test project")
                .startDate(new Date(2020 - 1900, 3, 12))
                .endDate(new Date(2023 - 1900, 1, 10))
                .projectName("Project 2020")
                .projectId(1)
                .build();

        sprint1 = new Sprint.Builder()
                .sprintId(1)
                .sprintLabel("Sprint 1")
                .sprintName("Sprint 1")
                .description("Attempt 1")
                .project(project)
                .startDate(new Date(2021,1,1))
                .endDate(new Date(2021, 3, 1))
                .color(SprintColor.BLUE)
                .build();

        sprint2 = new Sprint.Builder()
                .sprintLabel("Sprint 2")
                .sprintName("Sprint 2")
                .description("Attempt 2")
                .project(project)
                .startDate(new Date(2021,3,1))
                .endDate(new Date(2021, 6, 1))
                .color(SprintColor.SYYBLUE)
                .build();

    }

    /**
     * Test to make sure when new sprint is requested appropriate data is passed to the sprint form
     */
    @Test
    void givenAProjectExists_whenNewSprintIsRequested_thenSprintFormIsReturnedWithAppropriateData() {
        try {
            when(projectService.getProjectById(1)).thenReturn(project);
            when(sprintService.getNewSprint(project)).thenReturn(sprint1);
            when(sprintService.getSprintDateRange(project, sprint1)).thenReturn(List.of("2020-03-12", "2023-01-10"));
            when(userAccountClientService.getUser(any())).thenReturn(reply.build());

            this.mockMvc.perform(MockMvcRequestBuilders
                    .get("/project/1/newSprint")
                    .flashAttr("sprint", sprint1)
                    .flashAttr("project", project))
                    .andExpect(status().isOk());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Test to make sure that no message is returned when verify Sprint is requested for a valid sprint
     */
    @Test
    void givenSprintExists_whenVerifySprintIsRequested_thenNoMessageIsReturned() {
        SprintDTO sprintDTO = new SprintDTO(1, project, "Sprint 1", "Sprint 1","Attempt 1",
                new Date(2021,1,1), new Date(2021, 3, 1),SprintColor.BLUE);
        try {
            when(projectService.getProjectById(1)).thenReturn(project);
            when(sprintService.verifySprint(sprint1)).thenReturn(true);
            result = this.mockMvc.perform(MockMvcRequestBuilders
                    .post("/project/1/verifySprint")
                    .flashAttr("sprintDTO", sprintDTO))
                    .andExpect(status().isOk())
                    .andReturn();
            Assertions.assertEquals("", result.getResponse().getContentAsString());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Test to make sure that an error message is returned when verify Sprint is requested for an invalid sprint sprint
     */
    @Test
    void givenSprintExists_whenVerifySprintIsRequested_thenErrorMessageIsReturned() {
        SprintDTO sprintDTO = new SprintDTO(1, project, "Sprint 100", "Sprint 1","Attempt 1",
                new Date(2021,1,1), new Date(2021, 3, 1),SprintColor.BLUE);
        try {
            when(projectService.getProjectById(1)).thenReturn(project);
            when(sprintService.verifySprint(any())).thenThrow(new IncorrectDetailsException("Sprint label can not be modified"));
            result = this.mockMvc.perform(MockMvcRequestBuilders
                            .post("/project/1/verifySprint")
                            .flashAttr("sprintDTO", sprintDTO))
                    .andExpect(status().isOk())
                    .andReturn();
            Assertions.assertEquals("Sprint label can not be modified", result.getResponse().getContentAsString());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Test to make sure when editing of sprint is requested sprint form is returned with valid information
     */
    @Test
    void givenSprintExists_whenEditIsRequested_ThenSprintFormIsReturnedWithValidDetails() {
        try {
            when(projectService.getProjectById(1)).thenReturn(project);
            when(sprintService.getSprint(1)).thenReturn(sprint1);
            when(userAccountClientService.getUser(any())).thenReturn(reply.build());

            this.mockMvc.perform(MockMvcRequestBuilders
                            .get("/project/1/editSprint/1")
                            .flashAttr("sprint", sprint1)
                            .flashAttr("project", project))
                    .andExpect(status().isOk())
                    .andExpect(view().name("sprintForm"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Test to make sure after deleting the sprint user is redirected to project page
     */
    @Test
    void givenSprintExists_whenDeleteSprintIsRequested_thenUserIsRedirectedToProjectPage() {
        try {
            when(sprintService.deleteSprint(1)).thenReturn("Successfully deleted Sprint 1");
            when(sprintService.getSprintByProject(1)).thenReturn(List.of());
            when(eventService.getEventByProjectId(1)).thenReturn(List.of());

            this.mockMvc.perform(MockMvcRequestBuilders
                            .post("/1/deleteSprint/1"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(view().name("redirect:/project/{projectId}"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Test to make sure that the user is redirected to the project page after saving the sprint
     */
    @Test
    void givenSprintExists_whenSaveSprintIsRequested_thenUserIsRedirectedToProjectPage(){
        try {
            when(projectService.getProjectById(1)).thenReturn(project);
            when(sprintService.verifySprint(any())).thenReturn(true);

            this.mockMvc.perform(MockMvcRequestBuilders
                            .post("/project/1/saveSprint"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(view().name("redirect:/project/{projectId}"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @AfterAll
    private static void tearDown() {
        utilities.close();
    }

}
