package nz.ac.canterbury.seng302.portfolio.controller;


import com.google.protobuf.Timestamp;
import nz.ac.canterbury.seng302.portfolio.model.*;
import nz.ac.canterbury.seng302.portfolio.service.EventService;
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
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
    private SimpMessagingTemplate template; 

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
    void givenEventExists_whenDeleteEventIsRequested_thenEventDeletedSuccessfully() {
        try {
            when(eventService.deleteEvent(event.getEventId()))
                    .thenReturn("Successfully deleted " + event.getEventName());

            utilities.when(() -> PrincipalUtils.checkUserIsTeacherOrAdmin(any())).thenReturn(true);

            this.mockMvc.perform(MockMvcRequestBuilders
                            .delete("/project/1/event/" + event.getEventId() + "/delete"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Successfully deleted " + event.getEventName()));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Asserts that the correct error message is returned when deleteEvent is called on a non-existent event
     * @throws Exception
     */
    @Test
    void givenEventDoesNotExist_whenDeleteEventCalled_thenExceptionIsThrown() throws Exception {
        when(eventService.deleteEvent(99))
                .thenThrow(new IncorrectDetailsException("Failure deleting Event"));

        this.mockMvc.perform(MockMvcRequestBuilders
            .delete("/project/1/event/99/delete")
            .flashAttr("eventId", 99))
            .andExpect(status().is4xxClientError())
            .andExpect(content().string("Failure deleting Event"));
    }

    /**
     * Test get events and check that it returns the correct response.
     * @throws Exception Thrown during mockmvc run time
     */
    @Test
    void givenServer_WhenGetEvents_ThenEventsReturnedSuccessfully() throws Exception{
        when(eventService.getEventByProjectId(anyInt())).thenReturn(List.of());
        this.mockMvc
            .perform(get("/project/1/events"))
            .andExpect(status().isOk());
    }

    @AfterAll
    public static void afterAll() {
        utilities.close();
    }

}
