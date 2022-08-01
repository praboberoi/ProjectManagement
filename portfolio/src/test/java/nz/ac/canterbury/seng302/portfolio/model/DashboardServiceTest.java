package nz.ac.canterbury.seng302.portfolio.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;

import nz.ac.canterbury.seng302.portfolio.service.DashboardService;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;

/**
 * Unit tests for methods in the sprintService class
 */
@SpringBootTest
@ActiveProfiles("test")
class DashboardServiceTest {
    
    @SpyBean
    private SprintRepository sprintRepository;

    @SpyBean
    private ProjectRepository projectRepository;

    @Autowired
    private DashboardService dashboardService;

    private Project.Builder projectBuilder;

    /**
     * Prepares tests by initilizing the sprint service and creating a valid project and sprint builder
     */
    @BeforeEach
    public void setup() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 1);

        projectBuilder = new Project.Builder()
        .projectName("Project 2020")
        .description("First Attempt")
        .startDate(new Date(Calendar.getInstance().getTimeInMillis()))
        .endDate(new Date(c.getTimeInMillis()));
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
    
    /**
     * Checks that all sample data projects are returned
     */
    @Test
    public void givenProjectDatabaseWithProjects_whenGetAllProjectsCalled_thenAllProjectReturned() {
        List<Project> returnList = dashboardService.getAllProjects();
        assertEquals(1, returnList.size());
    }

}