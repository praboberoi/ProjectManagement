package nz.ac.canterbury.seng302.portfolio.model;

import nz.ac.canterbury.seng302.portfolio.service.IncorrectDetailsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import nz.ac.canterbury.seng302.portfolio.service.SprintService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.sql.Date;
import java.util.Arrays;
import java.util.Calendar;

/**
 * Unit tests for methods in the sprintService class
 */
@SpringBootTest
class SprintServiceTest {
    Project project;
    
    @MockBean
    private SprintRepository sprintRepository;

    @MockBean
    private ProjectRepository projectRepository;

    @Autowired
    private SprintService sprintService;

    private Sprint.Builder sprintBuilder;

    /**
     * Prepares tests by initilizing the sprint service and creating a valid project and sprint builder
     */
    @BeforeEach
    public void setup() {
        sprintService = new SprintService(projectRepository, sprintRepository);

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 1);

        project = new Project.Builder()
        .projectName("Project 2020")
        .description("First Attempt")
        .startDate(new Date(Calendar.getInstance().getTimeInMillis()))
        .endDate(new Date(c.getTimeInMillis()))
        .build();

        sprintBuilder = new Sprint.Builder()
        .sprintLabel("Sprint 1")
        .sprintName("Sprint 1")
        .description("This is a sprint description")
        .project(project)
        .startDate(new Date(Calendar.getInstance().getTimeInMillis()))
        .endDate(new Date(Calendar.getInstance().getTimeInMillis()));
        when(sprintRepository.findByProject(any())).thenReturn(Arrays.asList());
    }
    
    /**
     * Tests that a sprint with normal inputs will be valid
     */
    @Test
    public void givenValidSprint_whenSprintValidated_thenSucceedsValidation() {
        Sprint sprint = sprintBuilder.build();
        assertDoesNotThrow(() -> {
            assertTrue(sprintService.verifySprint(sprint));
        });
    }

    /**
     * Tests that a sprint with too long a description will not be valid
     */
    @Test
    public void givenInvalidSprintDescription_whenSprintValidated_thenFailsValidation() {
        Sprint sprint = sprintBuilder
            .description("0123456789".repeat(26)) //260 characters
            .build();
            assertThrows(IncorrectDetailsException.class, () -> {sprintService.verifySprint(sprint);});
    }

    /**
     * Tests that a sprint with the maximum character count will be valid
     */
    @Test
    public void givenValidLargeSprintDescription_whenSprintValidated_thenFailsValidation() {
        Sprint sprint = sprintBuilder
            .description("0123456789".repeat(25)) //250 characters
            .build();
            assertDoesNotThrow(() -> {
                assertTrue(sprintService.verifySprint(sprint));
            });
    }

}