package nz.ac.canterbury.seng302.portfolio.controllers;

import com.google.protobuf.Timestamp;
import nz.ac.canterbury.seng302.portfolio.controller.DeadlineController;
import nz.ac.canterbury.seng302.portfolio.model.Deadline;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.User;
import nz.ac.canterbury.seng302.portfolio.service.DeadlineService;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.portfolio.utils.ControllerAdvisor;
import nz.ac.canterbury.seng302.portfolio.utils.IncorrectDetailsException;
import nz.ac.canterbury.seng302.portfolio.utils.PrincipalUtils;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.util.Date;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Unit tests for methods in DeadlineController Class
 */
@WebMvcTest(controllers = DeadlineController.class)
@AutoConfigureMockMvc(addFilters = false)
public class DeadlineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeadlineService deadlineService;

    @MockBean
    private ProjectService projectService;

    @MockBean
    private ControllerAdvisor controllerAdvisor;

    @MockBean
    private UserAccountClientService userAccountClientService;

    @Value("${apiPrefix}")
    private String apiPrefix;

    private Project project;

    private Deadline deadline;

    private Deadline deadline2;

    private Deadline deadline3;

    private static MockedStatic<PrincipalUtils> utilities;

    private User user;

    private UserResponse.Builder userResponse;


    @BeforeAll
    private static void initialise() {
        utilities = Mockito.mockStatic(PrincipalUtils.class);
        utilities.when(() -> PrincipalUtils.checkUserIsTeacherOrAdmin(any())).thenReturn(true);
    }

    /**
     * Initialises project and three deadlines before running each test
     */
    @BeforeEach
    public void setup(){
        project = new Project.Builder()
                .description("This is a test project")
                .startDate(new java.sql.Date(2020 - 1900, 3, 12))
                .endDate(new java.sql.Date(2023 - 1900, 1, 10))
                .projectName("Project 2020")
                .projectId(1)
                .build();

        deadline = new Deadline.Builder()
                .deadlineId(1)
                .date(new Date())
                .name("Deadline 1")
                .project(project)
                .build();

        deadline2 = new Deadline.Builder()
                .date(new Date())
                .name("Deadline 2")
                .project(project)
                .build();

        deadline3 = new Deadline.Builder()
                .date(new Date())
                .deadlineId(3)
                .name("Deadline 3")
                .project(project)
                .build();

        user = new User.Builder()
                .userId(0)
                .username("TimeTester")
                .firstName("Time")
                .lastName("Tester")
                .email("Test@tester.nz")
                .creationDate(new Date())
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

    /**
     * Tests to make sure an appropriate message is displayed when a post request is made to save the deadline.
     */
    @Test
    public void givenDeadlineExists_whenSaveDeadlineIsRequested_anAppropriateMessageIsDisplayed()  {
        try {
            when(deadlineService.saveDeadline(deadline)).thenReturn("Successfully Updated " + deadline.getName());

            when(deadlineService.saveDeadline(deadline2)).thenReturn("Successfully Created " + deadline2.getName());

            when(deadlineService.saveDeadline(deadline3)).thenThrow(new IncorrectDetailsException("Failure to save the deadline"));

            this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/project/1/saveDeadline")
                        .flashAttr("deadline", deadline))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(flash().attribute("messageSuccess", "Successfully Updated " + deadline.getName()))
                    .andExpect(view().name("redirect:/project/{projectId}"));

            this.mockMvc.perform(MockMvcRequestBuilders
                            .post("/project/1/saveDeadline")
                            .flashAttr("deadline", deadline2))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(flash().attribute("messageSuccess", "Successfully Created " + deadline2.getName()))
                    .andExpect(view().name("redirect:/project/{projectId}"));

            this.mockMvc.perform(MockMvcRequestBuilders
                            .post("/project/1/saveDeadline")
                            .flashAttr("deadline", deadline3))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(flash().attribute("messageDanger", "Failure to save the deadline"))
                    .andExpect(view().name("redirect:/project/{projectId}"));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Tests to make sure an appropriate message is displayed when a post request is made to delete the given deadline
     */
    @Test
    public void givenDeadlineExists_whenDeleteDeadlineIsRequested_thenAnAppropriateMessageIsDisplayed() {
        try {
            when(deadlineService.deleteDeadline(1))
                    .thenReturn("Successfully deleted " + deadline.getName());

            when(deadlineService.deleteDeadline(3))
                    .thenThrow(new IncorrectDetailsException("Failure deleting Deadline"));

                this.mockMvc.perform(MockMvcRequestBuilders
                                .post("/1/deleteDeadline/1")
                                .flashAttr("deadlineId", 1))
                        .andExpect(status().is3xxRedirection())
                        .andExpect(flash().attribute("messageSuccess", "Successfully deleted " + deadline.getName()))
                        .andExpect(view().name("redirect:/project/{projectId}"));

                this.mockMvc.perform(MockMvcRequestBuilders
                                .post("/1/deleteDeadline/3")
                                .flashAttr("deadlineId", 3))
                        .andExpect(status().is3xxRedirection())
                        .andExpect(flash().attribute("messageDanger", "Failure deleting Deadline"))
                        .andExpect(view().name("redirect:/project/{projectId}"));

            } catch (Exception e) {
                e.printStackTrace();
        }

    }

    /**
     * Tests to make sure the appropriate attributes are added and the correct redirection is made when calling the deadlineEditForm
     */
    @Test
    public void givenDeadlineExists_whenDeadlineEditFormRequested_thenAnAppropriateMessageIsDisplayed() {
        try {
            when(projectService.getProjectById(1)).thenReturn(project);
            when(deadlineService.getDeadline(1)).thenReturn(deadline);


            this.mockMvc.perform(MockMvcRequestBuilders
                    .get("/project/1/editDeadline/1"))
                    .andExpect(status().isOk())
                    .andExpect(model().attribute("deadline", deadline))
                    .andExpect(model().attribute("project", project))
                    .andExpect(model().attribute("deadlineMin", project.getStartDate()))
                    .andExpect(model().attribute("deadlineMax", project.getEndDate()))
                    .andExpect(model().attribute("submissionName", "Save"))
                    .andExpect(model().attribute("image", apiPrefix + "/icons/save-icon.svg"))
                    .andExpect(model().attribute("pageTitle", "Edit Deadline: " + deadline.getName()))
                    .andExpect(view().name("deadlineForm"));



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Test call to deadlineEditForm to assert exception redirects to the project page with the correct message
     */
    @Test
    public void givenProjectDoesNotExist_whenDeadlineEditFormRequested_thenErrorIsHandledAppropriately() {
        try {
            when(projectService.getProjectById(1)).thenReturn(project);
            when(deadlineService.getDeadline(1)).thenReturn(deadline);
            when(projectService.getProjectById(2)).thenThrow(IncorrectDetailsException.class);

            this.mockMvc.perform(MockMvcRequestBuilders
                            .get("/project/1/editDeadline/2"))
                    .andExpect(flash().attribute("messageDanger", "Project not found"))
                    .andExpect(view().name("redirect:/project/{projectId}"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Test call to newDeadline redirects to the new deadline form with the correct data
     */
    @Test
    public void givenDeadlineDoesNotExist_whenNewDeadlineFormRequested_thenAppropriateFormAndFormDataIsReturned() {
        try {
            when(projectService.getProjectById(1)).thenReturn(project);
            when(deadlineService.getNewDeadline(project)).thenReturn(deadline);
            when(userAccountClientService.getUser(any())).thenReturn(userResponse.build());
            this.mockMvc.perform(MockMvcRequestBuilders
                    .get("/project/1/newDeadline"))
                    .andExpect(model().attribute("project", project))
                    .andExpect(model().attribute("deadline", deadline))
                    .andExpect(model().attribute("pageTitle", "Add New Deadline"))
                    .andExpect(model().attribute("submissionName", "Create"))
                    .andExpect(model().attribute("image",  "/icons/create-icon.svg"))
                    .andExpect(model().attribute("user", userResponse.build()))
                    .andExpect(model().attribute("projectDateMin", project.getStartDate().toString() + "T00:00"))
                    .andExpect(model().attribute("projectDateMax", project.getEndDate().toString() + "T00:00"))
                    .andExpect(view().name("deadlineForm"));

            when(projectService.getProjectById(2)).thenThrow(new IncorrectDetailsException("Project not found"));
            this.mockMvc.perform(MockMvcRequestBuilders
                    .get("/project/2/newDeadline"))
                    .andExpect(view().name("redirect:/project/{projectId}"));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    private static void tearDown() {
        utilities.close();
    }
}
