package nz.ac.canterbury.seng302.portfolio.controller;

import com.google.protobuf.Timestamp;
import nz.ac.canterbury.seng302.portfolio.controller.DeadlineController;
import nz.ac.canterbury.seng302.portfolio.model.Deadline;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.User;
import nz.ac.canterbury.seng302.portfolio.model.notifications.DeadlineNotification;
import nz.ac.canterbury.seng302.portfolio.model.notifications.EventNotification;
import nz.ac.canterbury.seng302.portfolio.service.DeadlineService;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.portfolio.utils.ControllerAdvisor;
import nz.ac.canterbury.seng302.portfolio.utils.IncorrectDetailsException;
import nz.ac.canterbury.seng302.portfolio.utils.PrincipalUtils;
import nz.ac.canterbury.seng302.portfolio.utils.WebSocketPrincipal;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.StompSubProtocolHandler;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Unit tests for methods in DeadlineController Class
 */
@WebMvcTest(controllers = DeadlineController.class)
@AutoConfigureMockMvc(addFilters = false)
class DeadlineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeadlineService deadlineService;

    @MockBean
    private SimpMessagingTemplate template;

    @MockBean
    private ProjectService projectService;

    @MockBean
    private ControllerAdvisor controllerAdvisor;

    @MockBean
    private UserAccountClientService userAccountClientService;

    @Autowired
    private DeadlineController deadlineController;

    @Value("${apiPrefix}")
    private String apiPrefix;

    private Project project;

    private Deadline deadline;

    private Deadline deadline2;

    private Deadline deadline3;

    private static MockedStatic<PrincipalUtils> utilities;

    private WebSocketPrincipal mockedWebSocketPrincipal;

    private User user;

    private UserResponse.Builder userResponse;

    private MvcResult result;


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
                .username("Tester")
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

        mockedWebSocketPrincipal = mock(WebSocketPrincipal.class);
    }

    /**
     * Tests to make sure an appropriate message is displayed when a post request is made to save the deadline.
     */
    @Test
     void givenDeadlineExists_whenSaveDeadlineIsRequested_anAppropriateMessageIsDisplayed()  {
        try {
            when(deadlineService.saveDeadline(deadline)).thenReturn("Successfully Updated " + deadline.getName());

            when(deadlineService.saveDeadline(deadline2)).thenReturn("Successfully Created " + deadline2.getName());

            when(deadlineService.saveDeadline(deadline3)).thenThrow(new IncorrectDetailsException("Failure to save the deadline"));

            result = this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/project/1/saveDeadline")
                        .flashAttr("deadline", deadline))
                    .andExpect(status().isOk())
                    .andReturn();

            Assertions.assertEquals("Successfully Updated Deadline 1", result.getResponse().getContentAsString());


            result = this.mockMvc.perform(MockMvcRequestBuilders
                            .post("/project/1/saveDeadline")
                            .flashAttr("deadline", deadline2))
                    .andExpect(status().isOk())
                    .andReturn();

            Assertions.assertEquals("Successfully Created Deadline 2", result.getResponse().getContentAsString());


            result = this.mockMvc.perform(MockMvcRequestBuilders
                            .post("/project/1/saveDeadline")
                            .flashAttr("deadline", deadline3))
                            .andExpect(status().isBadRequest())
                            .andReturn();

            Assertions.assertEquals("Failure to save the deadline", result.getResponse().getContentAsString());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Tests to make sure an appropriate message is displayed when a post request is made to delete the given deadline
     */
    @Test
     void givenDeadlineExists_whenDeleteDeadlineIsRequested_thenAnAppropriateMessageIsDisplayed() {
        try {
            when(deadlineService.deleteDeadline(1))
                    .thenReturn("Successfully deleted " + deadline.getName());

            when(deadlineService.deleteDeadline(3))
                    .thenThrow(new IncorrectDetailsException("Failure deleting Deadline"));

            result = this.mockMvc.perform(MockMvcRequestBuilders
                            .delete("/1/deleteDeadline/1")
                            .flashAttr("deadlineId", 1))
                        .andExpect(status().isOk())
                        .andReturn();

            Assertions.assertEquals("Successfully deleted Deadline 1", result.getResponse().getContentAsString());

            result = this.mockMvc.perform(MockMvcRequestBuilders
                                .delete("/1/deleteDeadline/3")
                            .flashAttr("deadlineId", 3))
                        .andExpect(status().isBadRequest())
                        .andReturn();

            Assertions.assertEquals("Failure deleting Deadline", result.getResponse().getContentAsString());

            } catch (Exception e) {
                e.printStackTrace();
        }

    }

    /**
     * Tests that the user is added to the list of editing users when they start editing
     * @throws Exception Thrown during mockmvc run time
     */
    @Test
    void givenUserDecidesToEditADeadline_whenDeadlineListIsRequested_thenNotificationIsPresent() throws Exception {
        Set<DeadlineNotification> expectedNotifications = new HashSet<>(List.of(new DeadlineNotification(1, 1, "Tester", true,
                "0")));

        when(mockedWebSocketPrincipal.getName()).thenReturn("Tester");

        deadlineController.editing(new DeadlineNotification(1, 1, "Tester", true, "0"), mockedWebSocketPrincipal, "0");

        this.mockMvc
                .perform(get("/project/1/deadlines"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("editDeadlineNotifications", expectedNotifications));
    }

    /**
     * Check that the user is removed from the list of editing users when they finish editing
     * @throws Exception Thrown during mockmvc run time
     */
    @Test
    void givenAUserIsEditing_whenTheyFinishEditing_thenUserIsNotEditing() throws Exception {
        Set<DeadlineNotification> expectedNotifications = new HashSet<>();

        when(mockedWebSocketPrincipal.getName()).thenReturn("Tester");

        deadlineController.editing(new DeadlineNotification(1, 1, "Tester", true, "0"), mockedWebSocketPrincipal, "0");
        deadlineController.editing(new DeadlineNotification(1, 1, "Tester", false, "0"), mockedWebSocketPrincipal, "0");


        this.mockMvc
                .perform(get("/project/1/deadlines"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("editDeadlineNotifications", expectedNotifications));
    }

    /**
     * Check that the user is removed from the list of editing users when they are disconnected
     * @throws Exception Thrown during mockmvc run time
     */
    @Test
    void givenAUserIsEditing_whenDisconnectEvent_thenUserIsNotEditing() throws Exception {
        Set<DeadlineNotification> expectedNotifications = new HashSet<>();

        StompSubProtocolHandler testSource = new StompSubProtocolHandler();
        GenericMessage<byte[]> testMessage = new GenericMessage<byte[]>(HexFormat.of().parseHex("FF"));

        SessionDisconnectEvent disconnectEvent = new SessionDisconnectEvent(testSource, testMessage, "0", CloseStatus.TLS_HANDSHAKE_FAILURE);

        when(mockedWebSocketPrincipal.getName()).thenReturn("Tester");

        deadlineController.editing(new DeadlineNotification(1, 1, "Tester", true, "0"), mockedWebSocketPrincipal, "0");

        deadlineController.onApplicationEvent(disconnectEvent);

        this.mockMvc
                .perform(get("/project/1/deadlines"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("editDeadlineNotifications", expectedNotifications));
    }

    @AfterAll
    private static void tearDown() {
        utilities.close();
    }
}
