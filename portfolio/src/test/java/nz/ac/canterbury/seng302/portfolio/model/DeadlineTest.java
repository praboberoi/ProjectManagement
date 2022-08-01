package nz.ac.canterbury.seng302.portfolio.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.sql.Date;

import static org.junit.jupiter.api.Assertions.*;

public class DeadlineTest {
    private Deadline deadline1;
    private Deadline deadline2;
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

        deadline1 = new Deadline.Builder()
                .deadlineId(1)
                .name("DeadlineTest 1")
                .date(new Date(2021,1,1))
                .project(project)
                .build();

        deadline2 = new Deadline.Builder()
                .deadlineId(1)
                .name("DeadlineTest 2")
                .date(new Date(2021,2,1))
                .project(project)
                .build();
    }

    @Test
    public void givenDeadlineExists_GetDeadlineId(){
        assertEquals(1, deadline1.getDeadlineID());
        assertNotEquals(2, deadline1.getDeadlineID());
    }

    @Test
    public void givenDeadlineExists_GetProject() {
        Project project2 = new Project.Builder()
                .projectName("Project 2021")
                .description("Second Attempt")
                .startDate(new Date(2021, 1, 9))
                .endDate(new Date(2021, 11, 20))
                .build();

        assertEquals(project, deadline1.getProject());
        assertNotEquals(project2, deadline1.getProject());
    }

    @Test
    public void givenDeadlineExists_SetProject() {
        Project proj = new Project();
        deadline1.setProject(proj);
        assertEquals(proj, deadline1.getProject());
        assertNotEquals(project, deadline1.getProject());
    }

    @Test
    public void givenDeadlineExists_GetSprintName() {
        assertEquals("DeadlineTest 1", deadline1.getName());
        assertNotEquals("DeadlineTest 2", deadline1.getName());
    }

    @Test
    public void givenDeadlineExists_SetSprintName() {
        deadline1.setName("Test");
        assertEquals("Test", deadline1.getName());
        assertNotEquals("DeadlineTest 1", deadline1.getName());
    }

    @Test
    public void givenDeadlineExists_GetDate() {
        assertEquals(new Date(2021,1,1), deadline1.getDate());
        assertNotEquals(new Date(2021,1,1), deadline2.getDate());
    }

    @Test
    public void givenDeadlineExists_SetDate() {
        deadline1.setDate(new Date(2021, 3, 12));
        assertEquals(new Date(2021, 3, 12), deadline1.getDate());
        assertNotEquals(new Date(2021,1,1), deadline1.getDate());
    }

    @Test
    public void testEquals() {
        assertEquals(false, deadline1.equals(deadline2));
        assertEquals(true, deadline1.equals(deadline1));
    }

}
