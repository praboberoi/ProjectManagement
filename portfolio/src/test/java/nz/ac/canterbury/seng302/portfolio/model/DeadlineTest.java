package nz.ac.canterbury.seng302.portfolio.model;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import java.sql.Date;

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
}
