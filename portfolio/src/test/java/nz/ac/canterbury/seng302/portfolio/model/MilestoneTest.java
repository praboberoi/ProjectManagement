package nz.ac.canterbury.seng302.portfolio.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.sql.Date;

import static org.junit.jupiter.api.Assertions.*;

public class MilestoneTest {
    private Milestone milestone1;
    private Milestone milestone2;
    private Project project;


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

    @Test
    public void givenMilestoneExists_GetMilestoneId(){
        assertEquals(1, milestone1.getMilestoneId());
        assertNotEquals(2, milestone1.getMilestoneId());
    }

    @Test
    public void givenMilestoneExists_GetProject() {
        Project project2 = new Project.Builder()
                .projectName("Project 2021")
                .description("Second Attempt")
                .startDate(new Date(2021, 1, 9))
                .endDate(new Date(2021, 11, 20))
                .build();

        assertEquals(project, milestone1.getProject());
        assertNotEquals(project2, milestone1.getProject());
    }

    @Test
    public void givenMilestoneExists_SetProject() {
        Project proj = new Project();
        milestone1.setProject(proj);
        assertEquals(proj, milestone1.getProject());
        assertNotEquals(project, milestone1.getProject());
    }

    @Test
    public void givenMilestoneExists_GetMilestoneName() {
        assertEquals("MilestoneTest 1", milestone1.getName());
        assertNotEquals("MilestoneTest 2", milestone1.getName());
    }

    @Test
    public void givenMilestoneExists_SetMilestoneName() {
        milestone1.setName("Test");
        assertEquals("Test", milestone1.getName());
        assertNotEquals("MilestoneTest 1", milestone1.getName());
    }

    @Test
    public void givenMilestoneExists_GetDate() {
        assertEquals(new Date(2021,1,1), milestone1.getDate());
        assertNotEquals(new Date(2021,1,1), milestone2.getDate());
    }

    @Test
    public void givenMilestoneExists_SetDate() {
        milestone1.setDate(new Date(2021, 3, 12));
        assertEquals(new Date(2021, 3, 12), milestone1.getDate());
        assertNotEquals(new Date(2021,1,1), milestone1.getDate());
    }

    @Test
    public void testEquals() {
        assertEquals(false, milestone1.equals(milestone2));
        assertEquals(true, milestone1.equals(milestone1));
    }

}
