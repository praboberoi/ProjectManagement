package nz.ac.canterbury.seng302.portfolio.controller;


import com.google.protobuf.Timestamp;
import nz.ac.canterbury.seng302.portfolio.model.Event;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.User;
import nz.ac.canterbury.seng302.portfolio.service.EventService;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    Event event;

    User user;

    Project project;

    UserResponse.Builder userResponse;

    @BeforeEach
    public void init() {
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


    @Test
    public void givenServer_WhenNavigateToNewEventForm_ThenEventFormReturned() throws Exception{
        when(projectService.getProjectById(1)).thenReturn(project);
        when(userAccountClientService.getUser(any())).thenReturn(userResponse.build());
        this.mockMvc
                .perform(get("/project/1/newEvent"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("project", project))
                .andExpect(model().attribute("event", event))
                .andExpect(model().attribute("pageTitle", "Add New Event"))
                .andExpect(model().attribute("user", userResponse))
                .andExpect(model().attribute("projectDateMin", project.getStartDate()))
                .andExpect(model().attribute("projectDateMax", project.getEndDate()));
    }

}
