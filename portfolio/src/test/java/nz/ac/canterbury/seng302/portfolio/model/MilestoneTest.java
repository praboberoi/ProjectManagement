package nz.ac.canterbury.seng302.portfolio.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.sql.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Entity tests for the milestones
 */
public class MilestoneTest {
    private Milestone milestone1;
    private Milestone milestone2;
    private Project project;

    /**
     * This method provides the functionality for all tests to be run by creating a new project and 2 milestone
     * entities prior to each test being run.
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

        milestone1 = new Milestone.Builder()
                .milestoneId(1)
                .name("MilestoneTest 1")
                .date(new Date(2021,1,1))
                .project(project)
                .build();

        milestone2 = new Milestone.Builder()
                .milestoneId(1)
                .name("MilestoneTest 2")
                .date(new Date(2021,2,1))
                .project(project)
                .build();
    }

    /**
     * Asserts that the correct milestone ID is retrieved when getMilestoneId() is called.
     */
    @Test
    public void givenMilestoneExists_WhenGetMilestoneId_ThenMilestoneIdReturned(){
        assertEquals(1, milestone1.getMilestoneId());
        assertNotEquals(2, milestone1.getMilestoneId());
    }

    /**
     * This test asserts that when a milestone has a project and it's method is called asking for the project it
     * returns the correct project.
     */
    @Test
    public void givenMilestoneExists_WhenMilestoneGetProject_ThenCorrectProjectReturned() {
        Project project2 = new Project.Builder()
                .projectName("Project 2021")
                .description("Second Attempt")
                .startDate(new Date(2021, 1, 9))
                .endDate(new Date(2021, 11, 20))
                .build();

        assertEquals(project, milestone1.getProject());
        assertNotEquals(project2, milestone1.getProject());
    }

    /**
     * Asserts that given a milestone, the milestones project can be updated
     */
    @Test
    public void givenMilestoneExists_SetProject() {
        Project proj = new Project();
        milestone1.setProject(proj);
        assertEquals(proj, milestone1.getProject());
        assertNotEquals(project, milestone1.getProject());
    }

    /**
     * Asserts that given a milestone, the correct name is returned when getName() is called
     */
    @Test
    public void givenMilestoneExists_GetMilestoneName() {
        assertEquals("MilestoneTest 1", milestone1.getName());
        assertNotEquals("MilestoneTest 2", milestone1.getName());
    }

    /**
     * Asserts that given a milestone, the milestones name can be updated
     */
    @Test
    public void givenMilestoneExists_SetMilestoneName() {
        milestone1.setName("Test");
        assertEquals("Test", milestone1.getName());
        assertNotEquals("MilestoneTest 1", milestone1.getName());
    }

    /**
     * Asserts that given a milestone, the correct date is returned
     */
    @Test
    public void givenMilestoneExists_GetDate() {
        assertEquals(new Date(2021,1,1), milestone1.getDate());
        assertNotEquals(new Date(2021,1,1), milestone2.getDate());
    }

    /**
     * Asserts that given a milestone, the milestone's date can be updated
     */
    @Test
    public void givenMilestoneExists_SetDate() {
        milestone1.setDate(new Date(2021, 3, 12));
        assertEquals(new Date(2021, 3, 12), milestone1.getDate());
        assertNotEquals(new Date(2021,1,1), milestone1.getDate());
    }

    /**
     * Tests that the override equal method for milestones works correctly
     */
    @Test
    public void testEquals() {
        assertEquals(false, milestone1.equals(milestone2));
        assertEquals(true, milestone1.equals(milestone1));
    }

}
