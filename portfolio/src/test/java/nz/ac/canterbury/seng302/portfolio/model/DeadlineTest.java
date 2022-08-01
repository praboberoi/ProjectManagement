package nz.ac.canterbury.seng302.portfolio.model;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import java.sql.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

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
}
