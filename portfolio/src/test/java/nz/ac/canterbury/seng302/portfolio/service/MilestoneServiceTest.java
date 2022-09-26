package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.*;
import nz.ac.canterbury.seng302.portfolio.utils.IncorrectDetailsException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.persistence.PersistenceException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Unit tests for methods on MilestoneService class
 */
@SpringBootTest
class MilestoneServiceTest {

    @MockBean
    private MilestoneRepository milestoneRepository;

    @MockBean
    private SprintRepository sprintRepository;

    private MilestoneService milestoneService;

    private Milestone milestone;
    private Project project;
    private Sprint sprint;
    private Milestone milestone2;

    /**
     * Initialises a project, 2 milestones, a sprint and values to be returned for mocking the MilestoneRepository and
     * ProjectRepository.
     */
    @BeforeEach
    public void setUp() {
        project = new Project.Builder()
                .description("This is a test project")
                .startDate(new java.sql.Date(2020 - 1900, 3, 12))
                .endDate(new java.sql.Date(2023 - 1900, 1, 10))
                .projectName("Project 2020")
                .projectId(1)
                .build();

        milestone = new Milestone.Builder()
                .milestoneId(1)
                .date(new Date(2022, 4, 4))
                .name("Milestone 1")
                .project(project)
                .build();

        milestone2 = new Milestone.Builder()
                .milestoneId(2)
                .date(new Date(2020, 4, 4))
                .name("Milestone 2")
                .project(project)
                .build();

        sprint = new Sprint.Builder()
                .sprintId(1)
                .startDate(new java.sql.Date(2020, 1, 1))
                .endDate(new java.sql.Date(2020, 5, 1))
                .build();

        milestoneService = new MilestoneService(milestoneRepository, sprintRepository);

        when(sprintRepository.findById(1)).thenReturn(Optional.ofNullable(sprint));

        when(milestoneRepository.findById(1)).thenReturn(Optional.ofNullable(milestone));

        when(milestoneRepository.findMilestonesBySprint(sprint)).thenReturn(List.of(milestone2));

        when(milestoneRepository.findByProject(any())).thenReturn(List.of(milestone2, milestone));
    }

    /**
     * Test to make sure that an appropriate message is received when a milestone is saved after updating its details
     */
    @Test
     void givenMilestoneExist_whenSaveMilestoneRequested_successfulUpdateMessageDisplayed() throws IncorrectDetailsException {
        milestone.setName("Updated");
        assertDoesNotThrow(() -> milestoneService.saveMilestone(milestone));
    }

    /**
     * Test to make sure an appropriate message is received when a new milestone is saved
     */
    @Test
     void givenNewMilestoneCreated_whenSaveMilestoneRequested_thenSuccessfullyCreatedMessageDisplayed() {
        Milestone milestone1 = new Milestone.Builder()
            .milestoneId(0)
            .date(new Date())
            .name("Milestone 1")
            .project(project)
            .build();

        assertDoesNotThrow(() -> milestoneService.saveMilestone(milestone1));

    }

    /**
     * Test to check if a new milestone is requested a new Milestone object is returned with an id of 0 and null values
     * for the remaining attributes.
     */
    @Test
     void givenMilestoneServiceExist_whenGetMilestoneRequested_thenANewMilestoneIsReturned() {
        Milestone newMilestone = milestoneService.getNewMilestone(project);
        LocalDate now = LocalDate.now();

        assertInstanceOf(Milestone.class, newMilestone);
        assertEquals(0, newMilestone.getMilestoneId());
        assertEquals(java.sql.Date.valueOf(now), newMilestone.getDate());
        assertEquals("New Milestone", newMilestone.getName());
        assertEquals(project,newMilestone.getProject());
    }

    /**
     * Test to check an appropriate error message is thrown when save milestone is requested due to JPA Persistence error
     */
    @Test
     void givenMilestoneExists_whenSaveMilestoneRequested_thenAnAppropriateExceptionIsThrown() {
        when(milestoneRepository.save(milestone)).thenThrow(PersistenceException.class);
        IncorrectDetailsException exception = assertThrows(IncorrectDetailsException.class, () ->
            milestoneService.saveMilestone(milestone));
        Assertions.assertEquals("Failed to save the milestone", exception.getMessage());
    }

    /**
     * Test to check an appropriate error message is thrown when save milestone is requested due to JPA Persistence error
     */
    @Test
     void givenMilestonesExists_whenMilestonesByProjectRequested_thenMilestonesInChronologicalOrder() {
        List<Milestone> milestones = milestoneService.getMilestonesByProject(project);

        assertEquals(milestone2, milestones.get(0));
    }

    /**
     * Test to make sure an error is thrown with an appropriate error message when a null object
     * is sent for verifying a milestone
     */
    @Test
    void givenMilestoneDoesNotExist_whenVerifyMilestoneRequested_thenExceptionIsThrown() {
        IncorrectDetailsException exception = assertThrows(IncorrectDetailsException.class, () ->
                milestoneService.verifyMileStone(null));
        Assertions.assertEquals("No milestone", exception.getMessage());
    }

    /**
     * Test to make sure an error is thrown with an appropriate error message shown when a milestone
     * has null values
     */
    @Test
    void givenMilestoneExistWithNullValues_whenVerifyMilestone_thenExceptionIsThrown() {
        Milestone newMilestone = new Milestone.Builder()
                .milestoneId(0)
                .date(new Date())
                .project(project)
                .build();

        Milestone newMilestone1 = new Milestone.Builder()
                .milestoneId(0)
                .name("name")
                .project(project)
                .build();

        Milestone newMilestone2 = new Milestone.Builder()
                .milestoneId(0)
                .name("name")
                .date(new Date())
                .build();

        List<Milestone> milestones = List.of(newMilestone1, newMilestone2, newMilestone);
        milestones.forEach(currentMilestone -> {
            IncorrectDetailsException exception = assertThrows(IncorrectDetailsException.class, () ->
                    milestoneService.verifyMileStone(currentMilestone));
            Assertions.assertEquals("Milestone values cannot be null", exception.getMessage());
        });
    }

    /**
     * Test to make sure an error is thrown with appropriate error message when a milestone object
     * with empty name is sent in for validation
     */
    @Test
    void givenMilestoneWithEmptyName_whenVerifyMilestone_thenExceptionIsThrown() {
        Milestone newMilestone3 = new Milestone.Builder()
                .name("")
                .milestoneId(0)
                .date(new Date())
                .project(project)
                .build();

        IncorrectDetailsException exception = assertThrows(IncorrectDetailsException.class, () ->
                milestoneService.verifyMileStone(newMilestone3));
        assertEquals("Milestone Name must not be empty", exception.getMessage());
    }

    /**
     * Test to make sure an error is thrown with appropriate error message when a milestone object
     * with name outside the character limit is sent for verifying a milestone
     */
    @Test
    void givenMilestoneNameOutsideCharacterLimit_whenVerifyMilestone_thenExceptionIsThrown() {
        Milestone newMilestone4 = new Milestone.Builder()
                .name("hi")
                .milestoneId(0)
                .date(new Date())
                .project(project)
                .build();
        IncorrectDetailsException exception = assertThrows(IncorrectDetailsException.class, () ->
                milestoneService.verifyMileStone(newMilestone4));
        assertEquals("Milestone name must be at least 3 characters", exception.getMessage());

        Milestone newMilestone5 = new Milestone.Builder()
                .name("A Milestone with a reallllyyyy looonnggg name to test that it the name cannot exceed the character limit of 50 characters")
                .milestoneId(0)
                .date(new Date())
                .project(project)
                .build();
        IncorrectDetailsException exception2 = assertThrows(IncorrectDetailsException.class, () ->
                milestoneService.verifyMileStone(newMilestone5));
        assertEquals("Milestone Name cannot exceed 50 characters", exception2.getMessage());
    }

    /**
     * Test to make sure an error is thrown with appropriate error message when a milestone object
     * with dates outside the project is sent for verifying a milestone
     */
    @Test
    void givenMilestoneWithDatesOutsideProject_whenVerifyMilestone_thenExceptionIsThrown() {
        Milestone newMilestone6 = new Milestone.Builder()
                .name("new milestone")
                .milestoneId(0)
                .date(new java.sql.Date(2024, 11, 12))
                .project(project)
                .build();
            IncorrectDetailsException exception = assertThrows(IncorrectDetailsException.class, () ->
                    milestoneService.verifyMileStone(newMilestone6));
            assertEquals("Milestone date cannot be after project end date", exception.getMessage());

        Milestone newMilestone7 = new Milestone.Builder()
                .name("new milestone")
                .milestoneId(0)
                .date(new java.sql.Date(100, 11, 12))
                .project(project)
                .build();
        IncorrectDetailsException exception2 = assertThrows(IncorrectDetailsException.class, () ->
                milestoneService.verifyMileStone(newMilestone7));
        assertEquals("Milestone date cannot be before project start date", exception2.getMessage());

    }

}
