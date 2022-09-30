package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.*;
import nz.ac.canterbury.seng302.portfolio.utils.IncorrectDetailsException;
import nz.ac.canterbury.seng302.portfolio.utils.SprintColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.PersistenceException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Unit tests for methods on DeadlineService class
 */
@SpringBootTest
@ActiveProfiles("test")
class DeadlineServiceTest {

    @MockBean
    private DeadlineRepository deadlineRepository;

    @MockBean
    private ProjectRepository projectRepository;

    @MockBean
    private SprintRepository sprintRepository;

    private DeadlineService deadlineService;

    private Deadline deadline;
    private Project project;
    private Sprint sprint;
    private Deadline deadline2;

    /**
     * Initialises a project, 2 deadlines, a sprint and values to be returned for mocking the DeadlineRepository and
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

        deadline = new Deadline.Builder()
                .deadlineId(1)
                .date(new Date())
                .name("Deadline 1")
                .project(project)
                .build();

        sprint = new Sprint.Builder()
                .sprintId(1)
                .sprintName("Test Sprint")
                .sprintLabel("Test Sprint")
                .startDate(new java.sql.Date(2020, 1, 1))
                .endDate(new java.sql.Date(2020, 5, 1))
                .build();

        deadline2 = new Deadline.Builder()
                .deadlineId(2)
                .date(new Date(2020, 4, 4))
                .name("Deadline 2")
                .project(project)
                .build();

        deadlineService = new DeadlineService(deadlineRepository, projectRepository, sprintRepository);

        when(sprintRepository.findById(1)).thenReturn(Optional.ofNullable(sprint));

        when(deadlineRepository.findById(1)).thenReturn(Optional.ofNullable(deadline));

        when(deadlineRepository.findDeadlinesBySprint(sprint)).thenReturn(List.of(deadline2));

        when(projectRepository.findById(1)).thenReturn(Optional.ofNullable(project));

        when(deadlineRepository.findByProject(project)).thenReturn(List.of(deadline));
    }



    /**
     * Test to make sure no exception is thrown when a deadline requested exists
     */
    @Test
     void givenDeadlineExists_whenDeadlineRequested_thenNoExceptionThrown() {
        assertDoesNotThrow(() -> {
            deadlineService.getDeadline(1);
        });
    }

    /**
     * Test to make sure that an exception is thrown when a requested deadline does not exist
     */
    @Test
     void givenDeadlineDoesNotExist_whenDeadlineRequested_thenExceptionThrown() {
        IncorrectDetailsException exception = assertThrows(IncorrectDetailsException.class, () ->
                deadlineService.getDeadline(2));
        assertEquals("Failed to locate deadline in the database" , exception.getMessage());

    }

    /**
     * Test to make sure an expected list of deadlines is returned when deadlines are requested by project id that exists
     */
    @Test
     void givenDeadlineAndProjectExist_whenDeadlineByProjectRequested_thenAListOfDeadlinesIsReturned() {
        assertEquals(List.of(deadline), deadlineService.getDeadlineByProject(1));
    }

    /**
     * Test to make sure an expected list of deadlines is returned when deadlines
     * are requested by a sprint id that exists
     */
    @Test
    void givenDeadlineAndSprintExist_whenDeadlineBySprintRequested_thenAListOfDeadlinesIsReturned() {
        assertEquals(List.of(deadline2), deadlineService.getDeadlinesBySprintId(1));
    }

    /**
     * Test to make sure an empty list is returned when deadlines are requested by a project id that does not exist
     */
    @Test
     void givenDeadlineAndProjectDoesNotExist_whenDeadlineByProjectRequested_thenAnEmptyListIsReturned() {
        assertEquals(List.of(), deadlineService.getDeadlineByProject(2));
    }

    /**
     * Test to make sure that no exception is thrown when a deadline is deleted and an appropriate message is received
     */
    @Test
     void givenDeadlineExists_whenDeleteDeadlineByIdRequested_thenAMessageIsReturned(){
        assertDoesNotThrow(() -> {
            assertEquals("Successfully deleted " + deadline.getName(), deadlineService.deleteDeadline(1));
        });
    }

    /**
     * Test to ensure an exception is thrown when a deadline that does not exist is requested.
     */
    @Test
     void givenDeadlineDoesNotExist_whenDeleteDeadlineByIdRequested_thenAnExceptionIsThrown() {
        IncorrectDetailsException exception = assertThrows(IncorrectDetailsException.class, () ->
                deadlineService.deleteDeadline(2));
        assertEquals("Could not find given Deadline" , exception.getMessage());

        when(deadlineRepository.findById(1)).thenThrow(PersistenceException.class);
        IncorrectDetailsException exception1 = assertThrows(IncorrectDetailsException.class, () ->
                deadlineService.deleteDeadline(1));
        assertEquals("Failure deleting Deadline" , exception1.getMessage());
    }

    /**
     * Test to make sure that an appropriate message is received when a deadline is saved after updating its details
     */
    @Test
     void givenDeadlineExist_whenSaveDeadlineRequested_successfulUpdateMessageDisplayed() {
        deadline.setName("Updated");
        try {
            assertEquals("Successfully Updated " + deadline.getName(), deadlineService.saveDeadline(deadline));
        } catch (IncorrectDetailsException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test to make sure an appropriate message is received when a new deadline is saved
     */
    @Test
     void givenNewDeadlineCreated_whenSaveDeadlineRequested_thenSuccessfullyCreatedMessageDisplayed() {
        Deadline deadline1 = new Deadline.Builder()
                                    .deadlineId(0)
                                    .date(new Date())
                                    .name("Deadline 1")
                                    .project(project)
                                    .build();

        try {
            assertEquals("Successfully Created " + deadline1.getName(), deadlineService.saveDeadline(deadline1));
        } catch (IncorrectDetailsException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test to make sure an error is thrown and an appropriate error message is reviewed when a null object is
     * sent for verifying a deadline
     */
    @Test
     void givenADeadlineDoesNotExist_whenVerifyDeadlineRequested_thenAnExceptionIsThrown() {
        IncorrectDetailsException exception = assertThrows(IncorrectDetailsException.class, () ->
                deadlineService.verifyDeadline(null));
        assertEquals("No deadline", exception.getMessage());
    }

    /**
     * Test to make sure deadline verification throws an error with appropriate error message for deadlines
     * with one of the fields left empty
     */
    @Test
     void givenDeadlineExistWithoutName_whenVerifyDeadlineRequested_thenAnExceptionIsThrown() {
        Deadline deadline1 = new Deadline.Builder()
                                    .deadlineId(0)
                                    .date(new Date())
                                    .project(project)
                                    .build();

        Deadline deadline2 = new Deadline.Builder()
                                    .deadlineId(0)
                                    .name("Missing date")
                                    .project(project)
                                    .build();

        Deadline deadline3 = new Deadline.Builder()
                                    .deadlineId(0)
                                    .name("Missing project")
                                    .date(new Date())
                                    .build();
        List<Deadline> deadlines = List.of(deadline1, deadline2, deadline3);
        deadlines.forEach(currentDeadline -> {
            IncorrectDetailsException exception = assertThrows(IncorrectDetailsException.class, () ->
                    deadlineService.verifyDeadline(currentDeadline));
            assertEquals("Deadline values cannot be null", exception.getMessage());
                });
    }

    /**
     * Test to make sure deadline verification throws an error with appropriate error message for deadlines with incorrect
     * names
     */
    @Test
     void givenDeadlineExistWithIncorrectName_whenVerifyDeadlineRequested_thenAnExceptionIsThrown() {
        Deadline deadline1 = new Deadline.Builder()
                .deadlineId(0)
                .date(new Date())
                .name("This is testing the character limit for setting the name of deadline and the limit is 20 characters")
                .project(project)
                .build();

        IncorrectDetailsException exception3 = assertThrows(IncorrectDetailsException.class, () ->
                deadlineService.verifyDeadline(deadline1));
        assertEquals("Deadline name cannot exceed 50 characters", exception3.getMessage());

    }

    /**
     * Test to make sure deadline verification throws an error with appropriate error message for deadlines with incorrect
     * dates
     */
    @Test
     void givenDeadlineExistWithIncorrectDate_whenVerifyDeadlineRequested_thenAnExceptionIsThrown() {
        SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            deadline.setDate(dateformat.parse("10/01/2024"));
            IncorrectDetailsException exception = assertThrows(IncorrectDetailsException.class, () ->
                    deadlineService.verifyDeadline(deadline));
            assertEquals("Deadline date cannot be after project end date", exception.getMessage());

            deadline.setDate(dateformat.parse("10/01/2000"));
            IncorrectDetailsException exception1 = assertThrows(IncorrectDetailsException.class, () ->
                    deadlineService.verifyDeadline(deadline));
            assertEquals("Deadline date cannot be before project start date", exception1.getMessage());

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test no error is thrown for deadline verification for a deadline with correct values
     */
    @Test
     void givenDeadlineExistWithCorrectDetail_whenVerifyDeadlineRequested_thenAnExceptionIsThrown() {
        assertDoesNotThrow(() -> deadlineService.verifyDeadline(deadline));
    }

    /**
     * Test to check if a new deadline is requested a new Deadline object is returned with an id of 0 and null values
     * for the remaining attributes.
     */
    @Test
     void givenDeadlineServiceExist_whenGetDeadlineRequested_thenANewDeadlineIsReturned() {
        Deadline newDeadline = deadlineService.getNewDeadline(project);
        LocalDate now = LocalDate.now();

        assertInstanceOf(Deadline.class, newDeadline);
        assertEquals(0, newDeadline.getDeadlineId());
        assertEquals(java.sql.Date.valueOf(now), newDeadline.getDate());
        assertEquals("New Deadline", newDeadline.getName());
        assertEquals(project,newDeadline.getProject());
    }

    /**
     * Test to check an appropriate error message is thrown when save deadline is requested due to JPA Persistence error
     */
    @Test
     void givenDeadlineExists_whenSaveDeadlineRequested_thenAnAppropriateExceptionIsThrown() {
        when(deadlineRepository.save(deadline)).thenThrow(PersistenceException.class);
        IncorrectDetailsException exception = assertThrows(IncorrectDetailsException.class, () ->
            deadlineService.saveDeadline(deadline));
        assertEquals("Failed to save the deadline", exception.getMessage());
    }

    /**
     * Tests that the correct sprint name is mapped the corresponding id of the deadline.
     */
    @Test
    void givenDeadlinesExist_whenGetSprintOccurringOnDeadlinesCalled_thenAppropriateDataReturned() {
        List<Deadline> deadlines = new ArrayList<Deadline>();
        deadlines.add(deadline);
        when(sprintRepository.findByDateAndProject(deadline.getProject(),
                 new java.sql.Date(deadline.getDate().getTime()))).thenReturn(sprint);
        Map<Integer, String> testData = deadlineService.getSprintOccurringOnDeadlines(deadlines);
        assertEquals("(Test Sprint)", testData.get(deadline.getDeadlineId()));
    }

    /**
     * Tests that if no sprint occurs on a deadline then this is mapped to the deadline.
     */
    @Test
    void givenNoSprintExistsForDeadline_whenGetSprintOccurringOnDeadlinesCalled_thenNoSprintIsMappedToDeadline() {
        List<Deadline> deadlines = new ArrayList<Deadline>();
        deadlines.add(deadline2);
        when(sprintRepository.findByDateAndProject(deadline2.getProject(),
                new java.sql.Date(deadline2.getDate().getTime()))).thenReturn(null);
        Map<Integer, String> testData = deadlineService.getSprintOccurringOnDeadlines(deadlines);
        assertEquals("(No Sprint)", testData.get(deadline2.getDeadlineId()));
    }


    /**
     * Test to check that when a deadline with an invalid name is verified an appropriate error is thrown
     */
    @Test
    void givenInvalidDeadlineWithNameLessThanThreeCharactersExists_whenVerifyDeadlineRequested_thenAnAppropriateExceptionIsThrown() {
        Deadline deadline2 = new Deadline.Builder()
                .deadlineId(1)
                .name("Te")
                .project(project)
                .date(project.getStartDate())
                .build();

        IncorrectDetailsException exception = assertThrows(IncorrectDetailsException.class, () ->
                deadlineService.verifyDeadline(deadline2));
        assertEquals("Deadline name must be at least 3 characters", exception.getMessage());
    }


    /**
     * Test to check when an invalid deadline with an emoji is passed for verification an inappropriate errror
     * thrown
     */
    @Test
    void givenInvalidDeadlineWithAnEmojiInName_whenVerifyDeadlineRequested_thenAnAppropriateExceptionIsThrown() {
        Deadline deadline2 = new Deadline.Builder()
                .deadlineId(1)
                .name("Test 😀")
                .project(project)
                .date(project.getStartDate())
                .build();

        IncorrectDetailsException exception = assertThrows(IncorrectDetailsException.class, () ->
                deadlineService.verifyDeadline(deadline2));
        assertEquals("Deadline name must not contain an emoji", exception.getMessage());
    }

    /**
     * Asserts that the colours for the given deadline is updated based on the given list of sprint
     */
    @Test
    void givenDeadlineExists_whenUpdateDeadlineColorsIsRequested_thenDeadlineColorsIsUpdatedWithSprint() {

        Sprint sprint1 = new Sprint.Builder().sprintName("Sprint 1").sprintId(1)
                .startDate(new java.sql.Date(2020 - 1900, 11, 1))
                .endDate(new java.sql.Date(2020 - 1900, 11, 2))
                .color(SprintColor.BLUE).build();

        when(sprintRepository.findSprintsByDeadline(deadline)).thenReturn(Optional.ofNullable(sprint1));
        deadlineService.updateDeadlineColors(deadline);

        assertEquals(SprintColor.BLUE, deadline.getColors());

    }
}
