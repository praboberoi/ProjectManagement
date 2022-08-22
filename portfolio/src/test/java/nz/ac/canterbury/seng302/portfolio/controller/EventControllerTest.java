package nz.ac.canterbury.seng302.portfolio.controller;


import com.google.protobuf.Timestamp;
import nz.ac.canterbury.seng302.portfolio.model.*;
import nz.ac.canterbury.seng302.portfolio.service.EventService;
import nz.ac.canterbury.seng302.portfolio.utils.IncorrectDetailsException;
import nz.ac.canterbury.seng302.portfolio.utils.IncorrectDetailsException;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

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

    /**
     * Tests to make sure an appropriate success message is displayed when a post request is made to delete the given event
     */
    @Test
    public void givenEventExists_whenDeleteEventIsRequested_thenEventDeletedSuccessfully() {
        try {
            when(eventService.deleteEvent(event.getEventId()))
                    .thenReturn("Successfully deleted " + event.getEventName());

            MockedStatic<PrincipalUtils> utilities = Mockito.mockStatic(PrincipalUtils.class);

            utilities.when(() -> PrincipalUtils.checkUserIsTeacherOrAdmin(any())).thenReturn(true);

            this.mockMvc.perform(MockMvcRequestBuilders
                            .post("/1/deleteEvent/" + event.getEventId())
                            .flashAttr("eventId", event.getEventId()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(model().attribute("listEvents", List.of()))
                    .andExpect(flash().attribute("messageSuccess", "Successfully deleted " + event.getEventName()))
                    .andExpect(view().name("redirect:/project/{projectId}"));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Asserts that the correct error message is returned when deleteEvent is called on a non-existent event
     */
    @Test
    public void givenEventDoesNotExist_whenDeleteEventCalled_thenExceptionIsThrown() {
        try {
            when(eventService.deleteEvent(99))
                    .thenThrow(new IncorrectDetailsException("Failure deleting Event"));

            this.mockMvc.perform(MockMvcRequestBuilders
                            .post("/1/deleteEvent/99")
                            .flashAttr("eventId", 99))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(flash().attribute("messageDanger", "Failure deleting Event"))
                    .andExpect(view().name("redirect:/project/{projectId}"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Tests that when an attempt is made to edit an event that doesn't exist then the appropriate error is returned.
      */
    @Test
    void givenProjectDetails_WhenNavigateToEditEventFormForNonExistentEvent_ThenAppropriateErrorReturned() {
        Event event = new Event.Builder()
                .eventName("New Event")
                .startDate(java.sql.Date.valueOf(LocalDate.now()))
                .endDate(java.sql.Date.valueOf(LocalDate.now().plusDays(1)))
                .build();
        try {
            when(projectService.getProjectById(1)).thenReturn(project);
            when(projectService.getProjectById(0)).thenThrow(new IncorrectDetailsException("Project not found"));
            when(eventService.getEvent(1)).thenReturn(event);
            when(eventService.getEvent(0)).thenThrow(new IncorrectDetailsException("Failed to locate the event in the database"));
            when(userAccountClientService.getUser(any())).thenReturn(userResponse.build());
            this.mockMvc
                    .perform(get("/project/1/editEvent/0"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(flash().attribute("messageDanger", "Failed to locate the event in the database"))
                    .andExpect(view().name("redirect:/project/{projectId}"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Tests that when an attempt is made to edit an event for a project that doesn't exist then the appropriate error
     * is returned.
     */
    @Test
    void givenProjectDetails_WhenNavigateToEditEventFormForNonExistentProject_ThenAppropriateErrorReturned() {
        Event event = new Event.Builder()
                .eventName("New Event")
                .startDate(java.sql.Date.valueOf(LocalDate.now()))
                .endDate(java.sql.Date.valueOf(LocalDate.now().plusDays(1)))
                .build();
        try {
            when(projectService.getProjectById(1)).thenReturn(project);
            when(projectService.getProjectById(0)).thenThrow(new IncorrectDetailsException("Project not found"));
            when(eventService.getEvent(1)).thenReturn(event);
            when(eventService.getEvent(0)).thenThrow(new IncorrectDetailsException("Failed to locate the event in the database"));
            when(userAccountClientService.getUser(any())).thenReturn(userResponse.build());
            this.mockMvc
                    .perform(get("/project/0/editEvent/1"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(flash().attribute("messageDanger", "Project not found"))
                    .andExpect(view().name("redirect:/project/{projectId}"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Tests that the edit event form is created with the required information
     */
    @Test
    void givenServer_WhenNavigateToEditEventForm_ThenAppropriateFormIsReturned() {
        Event event = new Event.Builder()
                .eventName("New Event")
                .startDate(java.sql.Date.valueOf(LocalDate.now()))
                .endDate(java.sql.Date.valueOf(LocalDate.now().plusDays(1)))
                .build();

        try {
            when(projectService.getProjectById(1)).thenReturn(project);
            when(projectService.getProjectById(0)).thenThrow(new IncorrectDetailsException("Project not found"));
            when(eventService.getEvent(1)).thenReturn(event);
            when(eventService.getEvent(0)).thenThrow(new IncorrectDetailsException("Failed to locate the event in the database"));
            when(userAccountClientService.getUser(any())).thenReturn(userResponse.build());
            this.mockMvc
                    .perform(get("/project/1/editEvent/1"))
                    .andExpect(status().isOk())
                    .andExpect(model().attribute("project", project))
                    .andExpect(model().attribute("event", event))
                    .andExpect(model().attribute("pageTitle", "Edit Event: " + event.getEventName()))
                    .andExpect(model().attribute("submissionName", "Save"))
                    .andExpect(model().attribute("user", userResponse.build()))
                    .andExpect(model().attribute("projectDateMin", project.getStartDate()))
                    .andExpect(model().attribute("projectDateMax", project.getEndDate()))
                    .andExpect(view().name("eventForm"));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    public static void afterAll() {
        utilities.close();
    }

}