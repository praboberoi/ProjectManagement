package nz.ac.canterbury.seng302.portfolio.controller;


import com.google.protobuf.Timestamp;
import nz.ac.canterbury.seng302.portfolio.model.*;
import nz.ac.canterbury.seng302.portfolio.service.EventService;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.portfolio.utils.PrincipalUtils;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
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

    @SpyBean
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

    User user;

    Project project;

    UserResponse.Builder userResponse;

    private static MockedStatic<PrincipalUtils> mockedStaticDigiGateway;

    @BeforeAll
    private static void initStaticMocks() {
        mockedStaticDigiGateway = mockStatic(PrincipalUtils.class);
    }

    @BeforeEach
    public void init() {
        when(PrincipalUtils.checkUserIsTeacherOrAdmin(any())).thenReturn(true);
        LocalDate now = LocalDate.now();
        project = new Project(1, "Test Project", "test", java.sql.Date.valueOf(now), java.sql.Date.valueOf(now.plusDays(50)));
        event = new Event.Builder()
                .project(project)
                .eventName("New Event")
                .startDate(java.sql.Date.valueOf(now))
                .endDate(java.sql.Date.valueOf(now.plusDays(1)))
                .startTime("00:00")
                .endTime("00:00")
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

    @AfterAll
    public static void after() {
        mockedStaticDigiGateway.close();
    }

    /**
     * Tests that the new event form is created with the required information
     * @throws Exception Thrown during mockmvc run time 
     */
    @Test
    void givenServer_WhenNavigateToNewEventForm_ThenEventFormReturned() throws Exception{
        when(projectService.getProjectById(1)).thenReturn(project);
        when(userAccountClientService.getUser(any())).thenReturn(userResponse.build());
        this.mockMvc
                .perform(get("/project/1/newEvent"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("project", project))
                .andExpect(model().attribute("event", event))
                .andExpect(model().attribute("pageTitle", "Add New Event"))
                .andExpect(model().attribute("user", userResponse.build()))
                .andExpect(model().attribute("projectDateMin", project.getStartDate()))
                .andExpect(model().attribute("projectDateMax", project.getEndDate()));
    }

    /**
     * Test verification of event object and check that it redirect the user to the project page.
     * @throws Exception Thrown during mockmvc run time
     */
    @Test
    void givenServer_WhenSaveValidEvent_ThenEventVerifiedSuccessfully() throws Exception{
        this.mockMvc
                .perform(post("/project/1/saveEvent").flashAttr("event", event))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("messageDanger", nullValue()))
                .andExpect(flash().attribute("messageSuccess", "Successfully Created " + event.getEventName()));
    }
}
