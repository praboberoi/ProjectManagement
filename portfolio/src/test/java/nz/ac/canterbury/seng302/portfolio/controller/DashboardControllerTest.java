package nz.ac.canterbury.seng302.portfolio.controller;

import com.google.protobuf.Timestamp;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.User;
import nz.ac.canterbury.seng302.portfolio.service.DashboardService;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import nz.ac.canterbury.seng302.portfolio.service.SprintService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.portfolio.utils.PrincipalUtils;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = DashboardController.class)
@AutoConfigureMockMvc(addFilters = false)
public class DashboardControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SprintService sprintService;

    @MockBean
    private ProjectService projectRepository;

    @MockBean
    private DashboardService dashboardService;

    @MockBean
    private UserAccountClientService userAccountClientService;

    private List<Project> listProjects;

    private User user;

    private UserResponse.Builder userResponse;

    private List<String> testList;

    private Project defaultProject;

    private static MockedStatic<PrincipalUtils> mockedPrincipalUtils;

    @BeforeAll
    private static void beforeAllInit() {
        mockedPrincipalUtils = mockStatic(PrincipalUtils.class);
    }


    @BeforeEach
    void beforeEach() {
        when(PrincipalUtils.checkUserIsTeacherOrAdmin(any())).thenReturn(true);
        testList = new ArrayList<String>();
        testList.add("testRole");
        when(PrincipalUtils.getUserRole(any())).thenReturn(testList);
        LocalDate now = LocalDate.now();
        listProjects = new ArrayList<Project>();
        defaultProject = new Project.Builder()
                .projectName("Project " + now.getYear())
                .startDate(Date.valueOf(now))
                .endDate(Date.valueOf(now.plusMonths(8)))
                .build();;

        listProjects.add(defaultProject);

        user = new User.Builder()
                .userId(0)
                .username("Username")
                .firstName("Time")
                .lastName("Tester")
                .email("Test@tester.nz")
                .creationDate(new java.util.Date())
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
    public static void afterAll() {
        mockedPrincipalUtils.close();
    }

    /**
     * Test that the dashboard with a list of projects is returned when showProjectList called.
     * @throws Exception Thrown during mockmvc run time
     */
    @Test
    void givenServer_WhenShowProjectList_ThenDashboardWithProjectListReturned() throws Exception{
        when(dashboardService.getAllProjects()).thenReturn(listProjects);
        this.mockMvc
                .perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("listProjects", listProjects));
    }    
}
