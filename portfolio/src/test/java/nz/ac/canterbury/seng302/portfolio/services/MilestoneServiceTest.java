package nz.ac.canterbury.seng302.portfolio.services;

import nz.ac.canterbury.seng302.portfolio.model.Milestone;
import nz.ac.canterbury.seng302.portfolio.model.MilestoneRepository;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.ProjectRepository;
import nz.ac.canterbury.seng302.portfolio.service.IncorrectDetailsException;
import nz.ac.canterbury.seng302.portfolio.service.MilestoneService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.text.SimpleDateFormat;
import java.sql.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@SpringBootTest
public class MilestoneServiceTest {

    @MockBean
    private MilestoneRepository milestoneRepository;

    @MockBean
    private ProjectRepository projectRepository;

    private MilestoneService milestoneService;

    private Milestone milestone;
    private Project project;

    /**
     * Set up creation of test milestone, project as well as relevant mockings
     */
    @BeforeEach
    public void setUp() {
        project = new Project.Builder()
                .startDate(new java.sql.Date(2020 - 1900, 3, 12))
                .endDate(new java.sql.Date(2023 - 1900, 1, 10))
                .projectName("Project 2020")
                .projectId(1)
                .build();

        milestone = new Milestone.Builder()
                .milestoneId(1)
                .name("Milestone Test")
                .date(new Date(2020 - 1900, 4, 12))
                .project(project)
                .build();

        milestoneService = new MilestoneService(milestoneRepository, projectRepository);
        when(milestoneRepository.findById(1)).thenReturn(Optional.ofNullable(milestone));
        when(projectRepository.findById(1)).thenReturn(Optional.ofNullable(project));
        when(milestoneRepository.findByProject(project)).thenReturn(List.of(milestone));
    }

    /**
     * Asserts that the correct Milestone object is returned when getMilestone() is called with the ID
     */
    @Test
    public void givenMilestoneExists_whenMilestoneRequested_thenNoExceptionThrown() {
        assertDoesNotThrow(()-> {
            milestoneService.getMilestone(1);
        });
    }

    /**
     * Asserts that the correct exception is thrown when getMilestone is called with an invalid ID
     */
    @Test
    public void givenMilestoneDoesNotExist_whenMilestoneRequested_thenExceptionThrown() {
        IncorrectDetailsException exception = assertThrows(IncorrectDetailsException.class, () ->
                milestoneService.getMilestone(99));
        assertEquals("Failed to locate milestone in the database", exception.getMessage());
    }

    /**
     * Asserts that the correct list of milestones is returned when getMilestoneByProject() is called with a valid project
     */
    @Test
    public void givenMilestoneAndProjectExist_whenMilestoneByProjectRequested_thenAListOfMilestoneIsReturned() {
        assertDoesNotThrow(() ->
                assertEquals(List.of(milestone), milestoneService.getMilestoneByProject(1)));
    }

    /**
     * Asserts that the correct exception is thrown when getMilestoneByProject() is called with an invalid project
     */
    @Test
    public void givenMilestoneAndProjectDoesNotExist_whenMilestoneByProjectRequested_thenExceptionThrown() {
        IncorrectDetailsException exception = assertThrows(IncorrectDetailsException.class, () ->
                milestoneService.getMilestoneByProject(99));
        assertEquals("Failed to locate a project with ID: 99", exception.getMessage());
    }

    /**
     * Asserts that a successful deletion message is returned when deleteMilestone() is called with an existing ID
     */
    @Test
    public void givenMilestoneExists_whenDeleteMilestoneByIdRequested_thenCorrectMessageIsReturned() {
        assertDoesNotThrow(() ->
                assertEquals("Successfully deleted Milestone Test", milestoneService.deleteMilestone(1))
        );
    }

    /**
     * Asserts that the correct exception is thrown when deleteMilestone() is called with an invalid ID
     */
    @Test
    public void givenMilestoneDoesNotExist_whenDeleteMilestoneByIdRequested_thenAnExceptionIsThrown() {
        IncorrectDetailsException exception = assertThrows(IncorrectDetailsException.class, () ->
                milestoneService.deleteMilestone(99));
        assertEquals("Could not find the given milestone", exception.getMessage());
    }

    /**
     * Asserts that the successfully updated message is returned when updating existing milestone
     */
    @Test
    public void givenMilestoneExists_whenSaveMilestoneRequested_returnsUpdatedMessage() {
        milestone.setName("Updated test name");
        assertDoesNotThrow(() ->
                assertEquals("Successfully updated Updated test name", milestoneService.saveMilestone(milestone))
        );
    }

    /**
     * Asserts that the successfully created message is returned when saving a new milestone
     */
    @Test
    public void givenMilestoneCreated_whenSaveMilestoneCalled_returnsCreationMessage() {
        Milestone milestone1 = new Milestone.Builder()
                .milestoneId(0)
                .date(new Date(2022-1900, 10, 2))
                .name("Milestone 2")
                .project(project)
                .build();

        assertDoesNotThrow(() ->
                assertEquals("Successfully created Milestone 2", milestoneService.saveMilestone(milestone1))
        );
    }

    /**
     * Asserts that the correct exception is thrown when verifyMilestone() is called with no milestone
     */
    @Test
    public void givenAMilestoneDoesNotExist_whenVerifyMilestoneCalled_thenAnExceptionIsThrown() {
        IncorrectDetailsException exception = assertThrows(IncorrectDetailsException.class, () ->
                milestoneService.verifyMilestone(null));
        assertEquals("No milestone", exception.getMessage());
    }

    /**
     * Asserts that the correct exception is thrown when verifyMilestone() is called with a milestone with no name
     */
    @Test
    public void givenMilestoneExistsWithoutName_whenVerifyMilestoneCalled_thenAnExceptionIsThrown() {
        Milestone milestone1 = new Milestone.Builder()
                .milestoneId(0)
                .date(new Date(2022-1900, 10, 2))
                .project(project)
                .build();

        IncorrectDetailsException exception = assertThrows(IncorrectDetailsException.class, () ->
                milestoneService.verifyMilestone(milestone1));
        assertEquals("Milestone values cannot be null", exception.getMessage());
    }

    /**
     * Asserts that the correct exception is thrown when verifyMilestone() is called with incorrect names
     */
    @Test
    public void givenMilestoneExistsWithIncorrectName_whenVerifyMilestoneCalled_thenAnExceptionIsThrown() {
        Milestone milestone1 = new Milestone.Builder()
                .milestoneId(0)
                .date(new Date(2022-1900, 10, 2))
                .project(project)
                .name(" incorrect name")
                .build();

        IncorrectDetailsException exception = assertThrows(IncorrectDetailsException.class, () ->
                milestoneService.verifyMilestone(milestone1));
        Assertions.assertEquals("Milestone name must not start or end with space characters", exception.getMessage());

        milestone1.setName("incorrect name ");
        IncorrectDetailsException exception2 = assertThrows(IncorrectDetailsException.class, () ->
                milestoneService.verifyMilestone(milestone1));
        Assertions.assertEquals("Milestone name must not start or end with space characters", exception2.getMessage());

        milestone1.setName("This is testing the character limit for setting the name of a milestone of 20 characters");
        IncorrectDetailsException exception3 = assertThrows(IncorrectDetailsException.class, () ->
                milestoneService.verifyMilestone(milestone1));
        Assertions.assertEquals("Milestone name cannot exceed 20 characters", exception3.getMessage());

    }

    /**
     * Asserts that the correct exception is thrown when verifyMilestone() is called with invalid dates
     */
    @Test
    public void givenMilestoneExistsWithIncorrectDate_whenVerifyMilestoneRequested_thenAnExceptionIsThrown() {
        Date date = new Date(2024-1900, 10, 2);
        assertDoesNotThrow(() -> {
            milestone.setDate(date);
            IncorrectDetailsException exception = assertThrows(IncorrectDetailsException.class, () ->
                    milestoneService.verifyMilestone(milestone)
            );
            assertEquals("The milestone's date cannot be after the project end date", exception.getMessage());

            milestone.setDate( new Date(1989-1900, 10, 2));
            IncorrectDetailsException exception1 = assertThrows(IncorrectDetailsException.class, () ->
                    milestoneService.verifyMilestone(milestone)
            );
            assertEquals("The milestone's date cannot be before the project start date", exception1.getMessage());
        });
    }

    /**
     * Asserts that no exception is thrown when verifying a correct milestone
     */
    @Test
    public void givenMilestoneExistsWithCorrectDetails_whenVerifyMilestoneCalled_thenNoExceptionThrown() {
        assertDoesNotThrow(() -> milestoneService.verifyMilestone(milestone));
    }
}
