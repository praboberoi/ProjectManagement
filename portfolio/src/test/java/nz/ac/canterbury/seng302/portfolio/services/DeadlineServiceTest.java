package nz.ac.canterbury.seng302.portfolio.services;

import nz.ac.canterbury.seng302.portfolio.model.Deadline;
import nz.ac.canterbury.seng302.portfolio.model.DeadlineRepository;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.ProjectRepository;
import nz.ac.canterbury.seng302.portfolio.service.DeadlineService;
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
import static org.mockito.Mockito.when;

/**
 * Unit tests for methods on DeadlineService class
 */
@SpringBootTest
public class DeadlineServiceTest {

    @MockBean
    private DeadlineRepository deadlineRepository;

    @MockBean
    private ProjectRepository projectRepository;


    private DeadlineService deadlineService;

    private Deadline deadline;
    private Project project;

    /**
     * Initialises a project, deadline and values to be returned for mocking the DeadlineRepository and ProjectRepository.
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

        deadlineService = new DeadlineService(deadlineRepository, projectRepository);

        when(deadlineRepository.findById(1)).thenReturn(Optional.ofNullable(deadline));

        when(projectRepository.findById(1)).thenReturn(Optional.ofNullable(project));

        when(deadlineRepository.findByProject(project)).thenReturn(List.of(deadline));
    }

    /**
     * Test to make sure no exception is thrown when a deadline requested exists
     */
    @Test
    public void givenDeadlineExists_whenDeadlineRequested_thenNoExceptionThrown() {
        assertDoesNotThrow(() -> {
            deadlineService.getDeadline(1);
        });
    }

    /**
     * Test to make sure that an exception is thrown when a requested deadline does not exist
     */
    @Test
    public void givenDeadlineDoesNotExist_whenDeadlineRequested_thenExceptionThrown() {
        IncorrectDetailsException exception = assertThrows(IncorrectDetailsException.class, () ->
                deadlineService.getDeadline(2));
        Assertions.assertEquals("Failed to locate deadline in the database" , exception.getMessage());

    }

    /**
     * Test to make sure an expected list of deadlines is returned when deadlines are requested by project id that exists
     */
    @Test
    public void givenDeadlineAndProjectExist_whenDeadlineByProjectRequested_thenAListOfDeadlinesIsReturned() {
        Assertions.assertEquals(List.of(deadline), deadlineService.getDeadlineByProject(1));
    }

    /**
     * Test to make sure an empty list is returned when deadlines are requested by a project id that does not exist
     */
    @Test
    public void givenDeadlineAndProjectDoesNotExist_whenDeadlineByProjectRequested_thenAnEmptyListIsReturned() {
        Assertions.assertEquals(List.of(), deadlineService.getDeadlineByProject(2));
    }

    /**
     * Test to make sure that no exception is thrown when a deadline is deleted and an appropriate message is received
     */
    @Test
    public void givenDeadlineExists_whenDeleteDeadlineByIdRequested_thenAMessageIsReturned(){
        assertDoesNotThrow(() -> {
            Assertions.assertEquals("Successfully deleted " + deadline.getName(), deadlineService.deleteDeadline(1));
        });
    }

    /**
     * Test to ensure an exception is thrown when a deadline that does not exist is requested.
     */
    @Test
    public void givenDeadlineDoesNotExist_whenDeleteDeadlineByIdRequested_thenAnExceptionIsThrown() {
        IncorrectDetailsException exception = assertThrows(IncorrectDetailsException.class, () ->
                deadlineService.deleteDeadline(2));
        Assertions.assertEquals("Could not find given Deadline" , exception.getMessage());

        when(deadlineRepository.findById(1)).thenThrow(PersistenceException.class);
        IncorrectDetailsException exception1 = assertThrows(IncorrectDetailsException.class, () ->
                deadlineService.deleteDeadline(1));
        Assertions.assertEquals("Failure deleting Deadline" , exception1.getMessage());
    }

    /**
     * Test to make sure that an appropriate message is received when a deadline is saved after updating its details
     */
    @Test
    public void givenDeadlineExist_whenSaveDeadlineRequested_successfulUpdateMessageDisplayed() {
        deadline.setName("Updated");
        try {
            Assertions.assertEquals("Successfully Updated " + deadline.getName(), deadlineService.saveDeadline(deadline));
        } catch (IncorrectDetailsException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test to make sure an appropriate message is received when a new deadline is saved
     */
    @Test
    public void givenNewDeadlineCreated_whenSaveDeadlineRequested_thenSuccessfullyCreatedMessageDisplayed() {
        Deadline deadline1 = new Deadline.Builder()
                                    .deadlineId(0)
                                    .date(new Date())
                                    .name("Deadline 1")
                                    .project(project)
                                    .build();

        try {
            Assertions.assertEquals("Successfully Created " + deadline1.getName(), deadlineService.saveDeadline(deadline1));
        } catch (IncorrectDetailsException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test to make sure an error is thrown and an appropriate error message is reviewed when a null object is
     * sent for verifying a deadline
     */
    @Test
    public void givenADeadlineDoesNotExist_whenVerifyDeadlineRequested_thenAnExceptionIsThrown() {
        IncorrectDetailsException exception = assertThrows(IncorrectDetailsException.class, () ->
                deadlineService.verifyDeadline(null));
        Assertions.assertEquals("No deadline", exception.getMessage());
    }

    /**
     * Test to make sure deadline verification throws an error with appropriate error message for deadlines
     * with one of the fields left empty
     */
    @Test
    public void givenDeadlineExistWithoutName_whenVerifyDeadlineRequested_thenAnExceptionIsThrown() {
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
            Assertions.assertEquals("Deadline values cannot be null", exception.getMessage());
                });
    }

    /**
     * Test to make sure deadline verification throws an error with appropriate error message for deadlines with incorrect
     * names
     */
    @Test
    public void givenDeadlineExistWithIncorrectName_whenVerifyDeadlineRequested_thenAnExceptionIsThrown() {
        Deadline deadline1 = new Deadline.Builder()
                .deadlineId(0)
                .date(new Date())
                .name(" incorrect ")
                .project(project)
                .build();
        IncorrectDetailsException exception = assertThrows(IncorrectDetailsException.class, () ->
                deadlineService.verifyDeadline(deadline1));
        Assertions.assertEquals("Deadline name must not start or end with space characters", exception.getMessage());

        deadline1.setName(" incorrect");

        IncorrectDetailsException exception1 = assertThrows(IncorrectDetailsException.class, () ->
                deadlineService.verifyDeadline(deadline1));
        Assertions.assertEquals("Deadline name must not start or end with space characters", exception1.getMessage());

        deadline1.setName("incorrect ");

        IncorrectDetailsException exception2 = assertThrows(IncorrectDetailsException.class, () ->
                deadlineService.verifyDeadline(deadline1));
        Assertions.assertEquals("Deadline name must not start or end with space characters", exception2.getMessage());

        deadline1.setName("This is testing the character limit for setting the name of deadline and the limit is 20 characters");

        IncorrectDetailsException exception3 = assertThrows(IncorrectDetailsException.class, () ->
                deadlineService.verifyDeadline(deadline1));
        Assertions.assertEquals("Deadline name cannot exceed 50 characters", exception3.getMessage());

    }

    /**
     * Test to make sure deadline verification throws an error with appropriate error message for deadlines with incorrect
     * dates
     */
    @Test
    public void givenDeadlineExistWithIncorrectDate_whenVerifyDeadlineRequested_thenAnExceptionIsThrown() {
        SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            deadline.setDate(dateformat.parse("10/01/2024"));
            IncorrectDetailsException exception = assertThrows(IncorrectDetailsException.class, () ->
                    deadlineService.verifyDeadline(deadline));
            Assertions.assertEquals("Deadline date cannot be after project end date", exception.getMessage());

            deadline.setDate(dateformat.parse("10/01/2000"));
            IncorrectDetailsException exception1 = assertThrows(IncorrectDetailsException.class, () ->
                    deadlineService.verifyDeadline(deadline));
            Assertions.assertEquals("Deadline date cannot be before project start date", exception1.getMessage());

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test no error is thrown for deadline verification for a deadline with correct values
     */
    @Test
    public void givenDeadlineExistWithCorrectDetail_whenVerifyDeadlineRequested_thenAnExceptionIsThrown() {
        assertDoesNotThrow(() -> deadlineService.verifyDeadline(deadline));
    }

    /**
     * Test to check if a new deadline is requested a new Deadline object is returned with an id of 0 and null values
     * for the remaining attributes.
     */
    @Test
    public void givenDeadlineServiceExist_whenGetDeadlineRequested_thenANewDeadlineIsReturned() {
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
    public void givenDeadlineExists_whenSaveDeadlineRequested_thenAnAppropriateExceptionIsThrown() {
        when(deadlineRepository.save(deadline)).thenThrow(PersistenceException.class);
        IncorrectDetailsException exception = assertThrows(IncorrectDetailsException.class, () ->
            deadlineService.saveDeadline(deadline));
        Assertions.assertEquals("Failure to save the deadline", exception.getMessage());
    }



}
