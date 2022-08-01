package nz.ac.canterbury.seng302.portfolio.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.sql.Date;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class DeadlineRepositoryTest {

    @Autowired
    private DeadlineRepository deadlineRepository;
    @Autowired
    private ProjectRepository projectRepository;
    private Deadline deadline1;
    private Deadline deadline2;
    private Deadline deadline3;
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

        project = projectRepository.save(project);

        deadline1 = new Deadline.Builder()
                .deadlineId(2)
                .name("DeadlineTest 1")
                .date(new Date(2021,1,1))
                .project(project)
                .build();

        deadline2 = new Deadline.Builder()
                .deadlineId(999)
                .name("DeadlineTest 2")
                .date(new Date(2021,2,1))
                .project(project)
                .build();


        deadline1 = deadlineRepository.save(deadline1);
        deadline2 = deadlineRepository.save(deadline2);
    }

    @AfterEach
    public void tearDown() {
        deadlineRepository.delete(deadline1);
        deadlineRepository.delete(deadline2);
        projectRepository.delete(project);
    }

    @Test
    public void givenDeadlineExists_FindByDeadlineName() {
        List<Deadline> deadlineList = Arrays.asList(deadline2);
        List<Deadline> testDeadline = deadlineRepository.findByName("DeadlineTest 2");

        assertArrayEquals(deadlineList.toArray(), testDeadline.toArray());

        List<Deadline> emptyList = Collections.<Deadline>emptyList();
        assertArrayEquals(emptyList.toArray(), deadlineRepository.findByName("No").toArray());
    }


    @Test
    public void givenDeadlineExists_FindByDate() {
        List<Deadline> deadlineList = Arrays.asList(deadline2);
        assertArrayEquals(deadlineList.toArray(), deadlineRepository.findByDate(new Date(2021,2,1)).toArray());
    }

    @Test
    public void givenDeadlineExists_FindById() {
        List<Deadline> deadlineList = Arrays.asList(deadline2);
        assertArrayEquals(deadlineList.toArray(), deadlineRepository.findByDeadlineID(deadline2.getDeadlineID()).toArray());
    }

    @Test
    public void givenDeadlineExists_CountByProject() {
        assertEquals(2, deadlineRepository.countByProject(project));
    }

    @Test
    public void givenDeadlineExists_FindByProject() {
        System.out.println("DeadlineTest 1: " + deadline1.getDeadlineID());
        List<Deadline> deadlineList = Arrays.asList(deadline1, deadline2);
        assertArrayEquals(deadlineList.toArray(), deadlineRepository.findByProject(project).toArray());

        Project project2 = new Project.Builder()
                .projectName("Project 2021")
                .description("Second Attempt")
                .startDate(new Date(2021, 1, 9))
                .endDate(new Date(2021, 11, 20))
                .build();
        projectRepository.save(project2);

        List<Deadline> emptyList = Collections.<Deadline>emptyList();
        assertArrayEquals(emptyList.toArray(), deadlineRepository.findByProject(project2).toArray());

        projectRepository.delete(project2);
    }
}
