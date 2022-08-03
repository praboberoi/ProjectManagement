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

/**
 * Tests for the milestone repository
 */
@DataJpaTest
@ActiveProfiles("test")
public class MilestoneRepositoryTest {

    @Autowired
    private MilestoneRepository milestoneRepository;
    @Autowired
    private ProjectRepository projectRepository;
    private Milestone milestone1;
    private Milestone milestone2;
    private Project project;

    /**
     * Initialises a test project, and two test milestones
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

    /**
     * Removes the test project and milestones from the repository
     */
    @AfterEach
    public void tearDown() {
        milestoneRepository.delete(milestone1);
        milestoneRepository.delete(milestone2);
        projectRepository.delete(project);
    }

    /**
     * Asserts that the correct milestone is returned when findByName is called on the milestone repository
     */
    @Test
    public void givenMilestoneExists_FindByMilestoneName() {
        List<Milestone> milestoneList = Arrays.asList(milestone2);
        List<Milestone> testMilestone = milestoneRepository.findByName("MilestoneTest 2");

        assertArrayEquals(milestoneList.toArray(), testMilestone.toArray());

        List<Milestone> emptyList = Collections.<Milestone>emptyList();
        assertArrayEquals(emptyList.toArray(), milestoneRepository.findByName("No").toArray());
    }

    /**
     * Asserts that the correct milestone is returned when findByDate is called on the milestone repository
     */
    @Test
    public void givenMilestoneExists_FindByDate() {
        List<Milestone> milestoneList = Arrays.asList(milestone2);
        assertArrayEquals(milestoneList.toArray(), milestoneRepository.findByDate(new Date(2021,2,1)).toArray());
    }

    /**
     * Asserts that the correct milestone is returned when findByMilestoneId is called on the milestone repository
     */
    @Test
    public void givenMilestoneExists_FindById() {
        List<Milestone> milestoneList = Arrays.asList(milestone2);
        assertArrayEquals(milestoneList.toArray(), milestoneRepository.findByMilestoneId(milestone2.getMilestoneId()).toArray());
    }

    /**
     * Asserts that the correct number of milestones is returned when countByProject is called on the milestone
     * repository
     */
    @Test
    public void givenMilestoneExists_CountByProject() {
        assertEquals(2, milestoneRepository.countByProject(project));
    }

    /**
     * Asserts that the correct milestones are returned when findByProject is called on the milestone repository
     */
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
