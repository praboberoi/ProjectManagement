package nz.ac.canterbury.seng302.portfolio.controller;


import com.google.protobuf.Timestamp;
import nz.ac.canterbury.seng302.portfolio.model.*;
import nz.ac.canterbury.seng302.portfolio.model.notifications.EventNotification;
import nz.ac.canterbury.seng302.portfolio.service.EventService;
import nz.ac.canterbury.seng302.portfolio.utils.IncorrectDetailsException;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.portfolio.utils.PrincipalUtils;
import nz.ac.canterbury.seng302.portfolio.utils.WebSocketPrincipal;
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
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.StompSubProtocolHandler;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.HexFormat;
import java.util.List;
import java.util.Set;

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

    @Autowired
    private EventController eventController;

    Event event;

    Event event1;

    User user;

    Project project;

    UserResponse.Builder userResponse;

    private static MockedStatic<PrincipalUtils> utilities;

    private WebSocketPrincipal mockedWebSocketPrincipal;

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

        mockedWebSocketPrincipal = mock(WebSocketPrincipal.class);
    }

    /**
     * Test verification of event object and check that it redirect the user to the project page.
     * @throws IncorrectDetailsException
     */
    @Test
    void givenServer_WhenSaveValidEvent_ThenEventSuccessful() throws Exception {
        when(eventService.saveEvent(any())).thenReturn("Successfully Created " + event.getEventName());

        this.mockMvc
                .perform(post("/project/1/saveEvent").flashAttr("eventDTO", event))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfully Created " + event.getEventName()));

    }

    /**
     * Test verification of event object and check that it redirect the user to the project page.
     * @throws IncorrectDetailsException
     */
    @Test
    void givenServer_WhenSaveValidEvent_ThenEventFailsSuccessfully() throws Exception {
        when(eventService.saveEvent(any())).thenThrow(new IncorrectDetailsException("Failure Saving Event"));
        this.mockMvc
                .perform(post("/project/1/saveEvent").flashAttr("eventDTO", event1))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string("Failure Saving Event"));
    }

    /**
     * Test that request returns failure when an event fails to save.
     * @throws IncorrectDetailsException
     */
    @Test
    void givenServer_WhenSaveValidEvent_ThenEventFailedSuccessfully() throws Exception {
        when(eventService.saveEvent(any())).thenThrow(new IncorrectDetailsException("Failure Saving Event"));

        this.mockMvc
                .perform(post("/project/1/saveEvent").flashAttr("eventDTO", event1))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string("Failure Saving Event"));

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

    /**
     * Tests that the user is added to the list of editing users when they start editing
     * @throws Exception Thrown during mockmvc run time
     */
    @Test
    void whenAUserStartsEditing_thenNotificationIsPresent() throws Exception {
        Set<EventNotification> expectedNotifications = new HashSet<>(Arrays.asList(new EventNotification(1, 1, "Tester", true, "0")));
        
        when(mockedWebSocketPrincipal.getName()).thenReturn("Tester");

        eventController.editing(new EventNotification(1, 1, "Tester", true, "0"), mockedWebSocketPrincipal, "0");
        
        this.mockMvc
            .perform(get("/project/1/events"))
            .andExpect(status().isOk())
            .andExpect(model().attribute("editNotifications", expectedNotifications));
    }

    /**
     * Check that the user is removed from the list of editing users when they finish editing
     * @throws Exception Thrown during mockmvc run time
     */
    @Test
    void givenAUserIsEditing_whenTheyFinishEditing_thenUserIsNotEditing() throws Exception {
        Set<EventNotification> expectedNotifications = new HashSet<>();
        
        when(mockedWebSocketPrincipal.getName()).thenReturn("Tester");

        eventController.editing(new EventNotification(1, 1, "Tester", true, "0"), mockedWebSocketPrincipal, "0");
        eventController.editing(new EventNotification(1, 1, "Tester", false, "0"), mockedWebSocketPrincipal, "0");
        

        this.mockMvc
            .perform(get("/project/1/events"))
            .andExpect(status().isOk())
            .andExpect(model().attribute("editNotifications", expectedNotifications));
    }

    /**
     * Check that the user is removed from the list of editing users when they are disconnected
     * @throws Exception Thrown during mockmvc run time
     */
    @Test
    void givenAUserIsEditing_whenDisconnectEvent_thenUserIsNotEditing() throws Exception {
        Set<EventNotification> expectedNotifications = new HashSet<>();

        StompSubProtocolHandler testSource = new StompSubProtocolHandler();
        GenericMessage<byte[]> testMessage = new GenericMessage<byte[]>(HexFormat.of().parseHex("FF"));

        SessionDisconnectEvent disconnectEvent = new SessionDisconnectEvent(testSource, testMessage, "0", CloseStatus.TLS_HANDSHAKE_FAILURE);
        
        when(mockedWebSocketPrincipal.getName()).thenReturn("Tester");

        eventController.editing(new EventNotification(1, 1, "Tester", true, "0"), mockedWebSocketPrincipal, "0");
        
        eventController.onApplicationEvent(disconnectEvent);

        this.mockMvc
            .perform(get("/project/1/events"))
            .andExpect(status().isOk())
            .andExpect(model().attribute("editNotifications", expectedNotifications));
    }

    @AfterAll
    public static void afterAll() {
        utilities.close();
    }

}
