package nz.ac.canterbury.seng302.portfolio.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;

public class DeadlineTest {
    private Deadline deadline1;
    private Deadline deadline2;
    private Project project;

    /**
     * Initialises test projects and deadlines for the tests
     */
    @BeforeEach
    public void setup() {
        project = new Project.Builder()
                .projectId(1)
                .projectName("Project 2020")
                .description("First Attempt")
                .startDate(new Date(2020, 3, 12))
                .endDate(new Date(2021, 1, 10))
                .build();

        deadline1 = new Deadline.Builder()
                .deadlineId(1)
                .name("DeadlineTest 1")
                .date(new java.util.Date(2021 - 1900, Calendar.FEBRUARY,1))
                .project(project)
                .build();

        deadline2 = new Deadline.Builder()
                .deadlineId(1)
                .name("DeadlineTest 2")
                .date(new java.util.Date(2021 - 1900, Calendar.MARCH,1))
                .project(project)
                .build();
    }

    /**
     * Asserts that when getDeadlineId() is called then the correct ID is returned
     */
    @Test
    public void givenDeadlineExists_whenDeadlineIdIsRequested_thenIdIsReturned(){
        assertEquals(1, deadline1.getDeadlineId());
        assertNotEquals(2, deadline1.getDeadlineId());
    }

    /**
     * Asserts that when getProject() is called on a deadline then the correct project is returned
     */
    @Test
    public void givenDeadlineExists_whenProjectIsRequested_thenAssociatedProjectIsReturned() {
        Project project2 = new Project.Builder()
                .projectName("Project 2021")
                .description("Second Attempt")
                .startDate(new Date(2021, 1, 9))
                .endDate(new Date(2021, 11, 20))
                .build();

        assertEquals(project, deadline1.getProject());
        assertNotEquals(project2, deadline1.getProject());
    }

    /**
     * Asserts that when setProject() is called on a deadline then the deadline's project is correctly updated
     */
    @Test
    public void givenDeadlineExists_whenChangeInProjectIsRequested_thenAssociatedProjectIsUpdated() {
        Project proj = new Project();
        deadline1.setProject(proj);
        assertEquals(proj, deadline1.getProject());
        assertNotEquals(project, deadline1.getProject());
    }

    /**
     * Asserts that when getName() is called on an existing deadline then the correct name is returned
     */
    @Test
    public void givenDeadlineExists_whenDeadlineNameIsRequested_thenDeadlineNameIsReturned() {
        assertEquals("DeadlineTest 1", deadline1.getName());
        assertNotEquals("DeadlineTest 2", deadline1.getName());
    }

    /**
     * Asserts that when setName() is called on a deadline then the deadline's name is correctly updated
     */
    @Test
    public void givenDeadlineExists_whenChangeInDeadlineNameIsRequested_thenDeadlineNameIsUpdated() {
        deadline1.setName("Test");
        assertEquals("Test", deadline1.getName());
        assertNotEquals("DeadlineTest 1", deadline1.getName());
    }

    /**
     * Asserts that when getDate() is called on an existing deadline then the correct date is returned
     */
    @Test
    public void givenDeadlineExists_whenDeadlineDateIsRequested_thenDeadlineDateIsReturned() {
        assertEquals(new java.util.Date(2021 -1900, Calendar.FEBRUARY,1), deadline1.getDate());
        assertNotEquals(new java.util.Date(2021 -1900, Calendar.FEBRUARY,1), deadline2.getDate());
    }

    /**
     * Asserts that given some deadlines exist, when the overrided object equals method is called then the correct
     * response is returned
     */
    @Test
    public void givenDeadlinesExist_whenTestEqualsMethodIsCalled_AppropriateResultsAreReceived() {
        Deadline deadline3 = new Deadline.Builder()
                .deadlineId(1)
                .name("DeadlineTest 2")
                .date(new java.util.Date(2021 - 1900, Calendar.MARCH,1))
                .project(project)
                .build();

        assertNotEquals(deadline1, deadline2);
        assertEquals(deadline1, deadline1);
        assertEquals(deadline3, deadline2);
        deadline3.setDeadlineId(2);
        assertNotEquals(deadline3, deadline2);
        deadline3.setDeadlineId(1);
        deadline3.setName("DeadlineTest 3");
        assertNotEquals(deadline3, deadline2);
        deadline3.setName("DeadlineTest 2");
        deadline3.setDate(new java.util.Date(2022 - 1900, Calendar.MARCH,1));
        assertNotEquals(deadline3, deadline2);
        assertNotEquals(deadline1, new Object());
        assertNotEquals(deadline1, new Deadline());
    }

    /**
     * Asserts that when getTime() is called on an existing deadline then the correct time is returned
     */
    @Test
    public void givenDeadlineExists_whenDeadlineTimeIsRequested_thenDeadlineTimeIsReturned() {
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");
        java.util.Date date = new java.util.Date();
        deadline1.setDate(date);
        assertEquals(formatter.format(date), deadline1.getTime());
        assertNotEquals("13:26:18", deadline1.getTime());
    }

    /**
     * Asserts that when getDateOnly() is called on an existing deadline then the correct date is returned
     */
    @Test
    public void givenDeadlineExists_whenOnlyDeadlineDateIsRequested_thenOnlyDeadlineDateIsReturned() {
        assertEquals("01/02/2021", deadline1.getDateOnly());
        assertNotEquals("02/02/2021", deadline1.getDateOnly());
    }

    /**
     * Asserts that when setDate() is called on a deadline then the deadline's date is correctly updated
     */
    @Test
    public void givenDeadlineExists_whenChangeInDeadlineDateIsRequested_thenDeadlineDateIsUpdated() {
        java.util.Date date = new java.util.Date(2023 - 1900, Calendar.FEBRUARY,1);
        deadline1.setDate(date);
        assertEquals(date, deadline1.getDate());
        deadline1.setDate(new java.util.Date(2021 - 1900, Calendar.FEBRUARY,1));
        assertNotEquals(date, deadline1.getDate());
    }

    /**
     * Asserts that when setDeadlineId() is called on a deadline then the deadline's ID is correctly updated
     */
    @Test
    public void givenDeadlineExists_whenChangeInDeadlineIdIsRequested_thenDeadlineIdIsUpdated() {
        deadline1.setDeadlineId(10);
        assertEquals(10, deadline1.getDeadlineId());
        assertNotEquals(1, deadline1.getDeadlineId());
    }

    /**
     * Asserts that when the toString() method is called on an existing deadline the correct string is returned.
     */
    @Test
    public void givenDeadlineExists_whenToStringOfDeadlineIsRetested_thenStringRepresentationOfDeadlineIsReturned() {
        String expectedString ="Deadline{" +
                "deadlineId=" + deadline1.getDeadlineId() +
                ", projectId=" + project.getProjectId() +
                ", name='" + deadline1.getName() +  "'" +
                ", date=" + deadline1.getDate() +
                '}';

        assertEquals(expectedString, deadline1.toString());
        assertNotEquals("", deadline1.toString());
        assertNotEquals(deadline2.toString(), deadline1.toString());
    }

    /**
     * Asserts that given a deadline exists when hasCode() is called an integer is returned
     */
    @Test
    public void givenDeadlineExists_whenHashCodeIsRequested_thenAnIntegerIsReturned() {
        assertInstanceOf(Integer.class, deadline1.hashCode());
    }



}
