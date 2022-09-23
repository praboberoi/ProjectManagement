package nz.ac.canterbury.seng302.portfolio.model.object;

import nz.ac.canterbury.seng302.portfolio.model.Milestone;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.utils.IncorrectDetailsException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the milestone entity
 */
 class MilestoneTest {
    private Project project;
    private Milestone milestone;

    /**
     * Initialises test projects and deadlines for the tests
     */
    @BeforeEach
     void setup() {
        Calendar startDate = Calendar.getInstance();
        startDate.set(2020, 3, 11);

        Calendar endDate = Calendar.getInstance();
        endDate.set(2021, 5, 11);

        project = new Project.Builder()
                .projectId(1)
                .projectName("Project 2020")
                .description("First Attempt")
                .startDate(new Date(startDate.getTime().getTime()))
                .endDate(new Date(endDate.getTime().getTime()))
                .build();

        milestone = new Milestone();
        milestone.setProject(project);
    }

    /**
     * Tests that a deadline cannot be created with a short name
     */
    @Test
    void givenProject_whenNameTooShort_thenExceptionThrown() {
        assertThrows(IncorrectDetailsException.class, () -> milestone.setName("T"));
    }

    /**
     * Tests that a deadline cannot be created with a long name
     */
    @Test
    void givenProject_whenNameTooLong_thenExceptionThrown() {
        assertThrows(IncorrectDetailsException.class, () -> milestone.setName("T".repeat(51)));
    }

    /**
     * Tests that a deadline can be created with a name 50 characters long
     */
    @Test
    void givenProject_whenName50Characters_thenNoExceptionThrown() {
        assertDoesNotThrow(() -> milestone.setName("T".repeat(50)));
    }

    /**
     * Tests that a deadline cannot be created with multiple spaces
     */
    @Test
    void givenProject_whenMultipleSpaces_thenExceptionThrown() {
        assertThrows(IncorrectDetailsException.class, () -> milestone.setName("T   S"));
    }

    /**
     * Tests that a deadline cannot be created outside of it's project
     */
    @Test
    void givenProject_whenDateAfterProject_thenExceptionThrown() {
        Calendar date = Calendar.getInstance();
        date.set(2021, 5, 12);
        assertThrows(IncorrectDetailsException.class, () -> milestone.setDate(date.getTime()));
    }

    /**
     * Tests that a deadline cannot be created before it's project starts
     */
    @Test
    void givenProject_whenDateBeforeProject_thenExceptionThrown() {
        Calendar date = Calendar.getInstance();
        date.set(2020, 3, 10);
        assertThrows(IncorrectDetailsException.class, () -> milestone.setDate(date.getTime()));
    }

    /**
     * Tests that a deadline cannot be created outside of it's project
     */
    @Test
    void givenProject_whenDateInsideProject_thenNoExceptionThrown() {
        Calendar date = Calendar.getInstance();
        date.set(2020, 8, 11);
        assertDoesNotThrow(() -> milestone.setDate(date.getTime()));
    }
}
