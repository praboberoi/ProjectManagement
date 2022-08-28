package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.*;
import nz.ac.canterbury.seng302.portfolio.utils.IncorrectDetailsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Date;
import java.util.Calendar;

/**
 * Unit tests for methods in the sprintService class
 */
@SpringBootTest
@ActiveProfiles("test")
class SprintServiceTest {
    Project project;

    @MockBean
    private EvidenceRepository evidenceRepository;
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
    }
    
    /**
     * Tests that a sprint with normal inputs will be valid
     */
    @Test
    void givenValidSprint_whenSprintValidated_thenSucceedsValidation() {
        Sprint sprint = sprintBuilder.build();
        assertDoesNotThrow(() -> {
            assertTrue(sprintService.verifySprint(sprint));
        });
    }

    /**
     * Tests that a sprint with too long a description will not be valid
     */
    @Test
    void givenInvalidSprintDescription_whenSprintValidated_thenFailsValidation() {
        Sprint sprint = sprintBuilder
            .description("0123456789".repeat(26)) //260 characters
            .build();
            assertThrows(IncorrectDetailsException.class, () -> {sprintService.verifySprint(sprint);});
    }

    /**
     * Tests that a sprint with the maximum character count will be valid
     */
    @Test
    void givenValidLargeSprintDescription_whenSprintValidated_thenPassesValidation() {
        Sprint sprint = sprintBuilder
            .description("0123456789".repeat(25)) //250 characters
            .build();
            assertDoesNotThrow(() -> {
                assertTrue(sprintService.verifySprint(sprint));
            });
    }

    /**
     * Asserts that an exception is thrown when a sprint has too long of a name
     */
    @Test
    void givenInvalidLargeSprintName_whenSprintValidated_thenFailsValidation() {
        Sprint sprint = sprintBuilder.sprintName("this is going to be more than 50 characters so an error message should be thrown")
                .build();
        assertThrows(IncorrectDetailsException.class, () -> sprintService.verifySprint(sprint));
    }

}