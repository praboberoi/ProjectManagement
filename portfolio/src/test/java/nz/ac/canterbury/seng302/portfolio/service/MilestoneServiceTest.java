package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.*;
import nz.ac.canterbury.seng302.portfolio.utils.IncorrectDetailsException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.PersistenceException;
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
@ActiveProfiles("test")
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
     * Test to make sure that no exception is thrown when a milestone is deleted and an appropriate message is received
     */
    @Test
    void givenMilestoneExists_whenDeleteMilestoneByIdRequested_thenAMessageIsReturned(){
        assertDoesNotThrow(() -> {
            Assertions.assertEquals("Successfully deleted " + milestone.getName(), milestoneService.deleteMilestone(1));
        });
    }

    /**
     * Test to ensure an exception is thrown when a milestone that does not exist is requested.
     */
    @Test
    void givenMilestoneDoesNotExist_whenDeleteMilestoneByIdRequested_thenAnExceptionIsThrown() {
        IncorrectDetailsException exception = assertThrows(IncorrectDetailsException.class, () ->
                milestoneService.deleteMilestone(2));
        Assertions.assertEquals("Could not find given Milestone" , exception.getMessage());

        when(milestoneRepository.findById(1)).thenThrow(PersistenceException.class);
        IncorrectDetailsException exception1 = assertThrows(IncorrectDetailsException.class, () ->
                milestoneService.deleteMilestone(1));
        Assertions.assertEquals("Failure deleting Milestone" , exception1.getMessage());
    }
}
