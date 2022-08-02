package nz.ac.canterbury.seng302.portfolio.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Date;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
public class MilestoneRepositoryTest {

    @Autowired
    private MilestoneRepository milestoneRepository;
    @Autowired
    private ProjectRepository projectRepository;
    private Milestone milestone1;
    private Milestone milestone2;
    private Milestone milestone3;
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

        milestone1 = new Milestone.Builder()
                .milestoneId(2)
                .name("MilestoneTest 1")
                .date(new Date(2021,1,1))
                .project(project)
                .build();

        milestone2 = new Milestone.Builder()
                .milestoneId(999)
                .name("MilestoneTest 2")
                .date(new Date(2021,2,1))
                .project(project)
                .build();


        milestone1 = milestoneRepository.save(milestone1);
        milestone2 = milestoneRepository.save(milestone2);
    }

    @AfterEach
    public void tearDown() {
        milestoneRepository.delete(milestone1);
        milestoneRepository.delete(milestone2);
        projectRepository.delete(project);
    }

    @Test
    public void givenMilestoneExists_FindByMilestoneName() {
        List<Milestone> milestoneList = Arrays.asList(milestone2);
        List<Milestone> testMilestone = milestoneRepository.findByName("MilestoneTest 2");

        assertArrayEquals(milestoneList.toArray(), testMilestone.toArray());

        List<Milestone> emptyList = Collections.<Milestone>emptyList();
        assertArrayEquals(emptyList.toArray(), milestoneRepository.findByName("No").toArray());
    }


    @Test
    public void givenMilestoneExists_FindByDate() {
        List<Milestone> milestoneList = Arrays.asList(milestone2);
        assertArrayEquals(milestoneList.toArray(), milestoneRepository.findByDate(new Date(2021,2,1)).toArray());
    }

    @Test
    public void givenMilestoneExists_FindById() {
        List<Milestone> milestoneList = Arrays.asList(milestone2);
        assertArrayEquals(milestoneList.toArray(), milestoneRepository.findByMilestoneId(milestone2.getMilestoneId()).toArray());
    }

    @Test
    public void givenMilestoneExists_CountByProject() {
        assertEquals(2, milestoneRepository.countByProject(project));
    }

    @Test
    public void givenMilestoneExists_FindByProject() {
        List<Milestone> milestoneList = Arrays.asList(milestone1, milestone2);
        assertArrayEquals(milestoneList.toArray(), milestoneRepository.findByProject(project).toArray());

        Project project2 = new Project.Builder()
                .projectName("Project 2021")
                .description("Second Attempt")
                .startDate(new Date(2021, 1, 9))
                .endDate(new Date(2021, 11, 20))
                .build();
        projectRepository.save(project2);

        List<Milestone> emptyList = Collections.<Milestone>emptyList();
        assertArrayEquals(emptyList.toArray(), milestoneRepository.findByProject(project2).toArray());

        projectRepository.delete(project2);
    }
}
