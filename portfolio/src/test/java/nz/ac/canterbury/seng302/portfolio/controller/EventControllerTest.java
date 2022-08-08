package nz.ac.canterbury.seng302.portfolio.controller;


import com.google.protobuf.Timestamp;
import nz.ac.canterbury.seng302.portfolio.model.*;
import nz.ac.canterbury.seng302.portfolio.service.EventService;
import nz.ac.canterbury.seng302.portfolio.service.IncorrectDetailsException;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.portfolio.utils.PrincipalUtils;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;

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
import java.util.Date;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = EventController.class)
@AutoConfigureMockMvc(addFilters = false)
public class EventControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    @MockBean
    private ProjectService projectService;

    @MockBean
    private UserAccountClientService userAccountClientService;

    @MockBean
    private EventRepository eventRepository;
    @MockBean
    private ProjectRepository projectRepository;

    Event event;

    Event event1;

    User user;

    Project project;

    UserResponse.Builder userResponse;

    private static MockedStatic<PrincipalUtils> utilities;

    @BeforeAll
    private static void beforeAllInit() {
        utilities = Mockito.mockStatic(PrincipalUtils.class);
        utilities.when(() -> PrincipalUtils.checkUserIsTeacherOrAdmin(any())).thenReturn(true);
    }

    @BeforeEach
    public void init() {
        LocalDate now = LocalDate.now();
        project = new Project(1, "Test Project", "test", java.sql.Date.valueOf(now), java.sql.Date.valueOf(now.plusDays(50)));
        event = new Event.Builder()
                .project(project)
                .eventName("New Event")
                .startDate(java.sql.Date.valueOf(now))
                .endDate(java.sql.Date.valueOf(now.plusDays(1)))
                .build();

        event1 = new Event.Builder()
                .project(project)
                .eventName("New Event 1")
                .startDate(java.sql.Date.valueOf(now))
                .endDate(java.sql.Date.valueOf(now.plusDays(1)))
                .build();

        user = new User.Builder()
                .userId(0)
                .username("TimeTester")
                .firstName("Time")
                .lastName("Tester")
                .email("Test@tester.nz")
                .creationDate(new Date())
                .build();


        userResponse = UserResponse.newBuilder()
                .setUsername(user.getUsername())
                .setFirstName(user.getFirstName())
                .setLastName(user.getLastName())
                .setEmail(user.getEmail())
                .setCreated(Timestamp.newBuilder()
                .setSeconds(user.getDateCreated().getTime())
                .build());
    }

    /**
     * Tests that the new event form is created with the required information
     * @throws Exception Thrown during mockmvc run time 
     */
    @Test
    void givenServer_WhenNavigateToNewEventForm_ThenAppropriateFormIsReturned() {
        Event event = new Event.Builder()
                .eventName("New Event")
                        .startDate(java.sql.Date.valueOf(LocalDate.now()))
                        .endDate(java.sql.Date.valueOf(LocalDate.now().plusDays(1)))
                        .build();

        try {
            when(projectService.getProjectById(1)).thenReturn(project);

            when(projectService.getProjectById(0)).thenThrow(new IncorrectDetailsException("Project not found"));
            when(eventService.getNewEvent(project)).thenReturn(event);
            when(userAccountClientService.getUser(any())).thenReturn(userResponse.build());
            this.mockMvc
                    .perform(get("/project/1/newEvent"))
                    .andExpect(status().isOk())
                    .andExpect(model().attribute("project", project))
                    .andExpect(model().attribute("event", event))
                    .andExpect(model().attribute("pageTitle", "Add New Event"))
                    .andExpect(model().attribute("user", userResponse.build()))
                    .andExpect(model().attribute("projectDateMin", project.getStartDate()))
                    .andExpect(model().attribute("projectDateMax", project.getEndDate()))
                    .andExpect(view().name("eventForm"));

            this.mockMvc
                    .perform(get("/project/0/newEvent"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(flash().attribute("messageDanger", "Project not found"))
                    .andExpect(view().name("redirect:/project/{projectId}"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Test verification of event object and check that it redirect the user to the project page.
     * @throws Exception Thrown during mockmvc run time
     */
    @Test
    void givenServer_WhenSaveValidEvent_ThenEventVerifiedSuccessfully() {

        try {
            when(eventService.saveEvent(event)).thenReturn("Successfully Created " + event.getEventName());
            when(eventService.saveEvent(event1)).thenThrow(new IncorrectDetailsException("Failure Saving Event"));

            this.mockMvc
                    .perform(post("/project/1/saveEvent").flashAttr("event", event))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(flash().attribute("messageDanger", nullValue()))
                    .andExpect(flash().attribute("messageSuccess", "Successfully Created " + event.getEventName()));

            this.mockMvc
                    .perform(post("/project/1/saveEvent").flashAttr("event", event1))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(flash().attribute("messageDanger",("Failure Saving Event")))
                    .andExpect(flash().attribute("messageSuccess",  nullValue()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    public static void afterAll() {
        utilities.close();
    }

}
