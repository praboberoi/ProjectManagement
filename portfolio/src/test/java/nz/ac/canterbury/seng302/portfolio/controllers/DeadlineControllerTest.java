package nz.ac.canterbury.seng302.portfolio.controllers;

import nz.ac.canterbury.seng302.portfolio.controller.DeadlineController;
import nz.ac.canterbury.seng302.portfolio.model.Deadline;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.service.DeadlineService;
import nz.ac.canterbury.seng302.portfolio.service.IncorrectDetailsException;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import nz.ac.canterbury.seng302.portfolio.utils.ControllerAdvisor;
import nz.ac.canterbury.seng302.portfolio.utils.PrincipalUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
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

    private Project project;

    private Deadline deadline;

    private Deadline deadline2;

    private Deadline deadline3;

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

            MockedStatic<PrincipalUtils> utilities = Mockito.mockStatic(PrincipalUtils.class);

            utilities.when(() -> PrincipalUtils.checkUserIsTeacherOrAdmin(any())).thenReturn(true);

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
}
