package nz.ac.canterbury.seng302.portfolio.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import nz.ac.canterbury.seng302.portfolio.service.DashboardService;
import nz.ac.canterbury.seng302.portfolio.service.SprintService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Date;
import java.util.Arrays;
import java.util.Calendar;

/**
 * Unit tests for methods in the sprintService class
 */
@SpringBootTest
class DashboardServiceTest {
    
    @MockBean
    private SprintRepository sprintRepository;

    @MockBean
    private ProjectRepository projectRepository;

    @Mock
    private SprintService mockSprintService = mock(SprintService.class);

    @Autowired
    private DashboardService dashboardService;

    private Project.Builder projectBuilder;

    /**
     * Prepares tests by initilizing the sprint service and creating a valid project and sprint builder
     */
    @BeforeEach
    public void setup() {
        dashboardService = new DashboardService(projectRepository, mockSprintService);

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 1);

        projectBuilder = new Project.Builder()
        .projectName("Project 2020")
        .description("First Attempt")
        .startDate(new Date(Calendar.getInstance().getTimeInMillis()))
        .endDate(new Date(c.getTimeInMillis()));
        when(sprintRepository.findByProject(any())).thenReturn(Arrays.asList());
        when(mockSprintService.getSprintByProject(anyInt())).thenReturn(Arrays.asList());
    }
    
    /**
     * Tests that a sprint with normal inputs will be valid
     */
    @Test
    public void givenValidProject_whenProjectValidated_thenSucceedsValidation() {
        Project project = projectBuilder.build();
        assertDoesNotThrow(() -> {
            dashboardService.verifyProject(project);
        });
    }

    /**
     * Tests that a sprint with too long a description will not be valid
     */
    @Test
    public void givenInvalidSprintDescription_whenSprintValidated_thenFailsValidation() {
        Project project = projectBuilder
            .description("0123456789".repeat(26)) //260 characters
            .build();
            assertThrows(Exception.class, () -> {dashboardService.verifyProject(project);});
    }

    /**
     * Tests that a sprint with the maximum character count will be valid
     */
    @Test
    public void givenValidLargeSprintDescription_whenSprintValidated_thenFailsValidation() {
        Project project = projectBuilder
            .description("0123456789".repeat(25)) //250 characters
            .build();
            assertDoesNotThrow(() -> {
                dashboardService.verifyProject(project);
            });
    }

}