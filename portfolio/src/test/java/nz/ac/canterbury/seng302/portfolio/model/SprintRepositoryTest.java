package nz.ac.canterbury.seng302.portfolio.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

@SpringBootTest
class SprintRepositoryTest {
    @Autowired private SprintRepository sprintRepository;
    @Autowired private ProjectRepository projectRepository;
    private Sprint sprint1;
    private Sprint sprint2;
    private Project project;

    @BeforeEach
    public void setUp() {
        project = new Project.Builder()
                .projectName("Project 2020")
                .description("First Attempt")
                .startDate(new Date(2020, 3, 12))
                .endDate(new Date(2021, 1, 10))
                .build();

        sprint1 = new Sprint.Builder()
                .sprintName("Sprint 1")
                .description("Attempt 1")
                .project(project)
                .startDate(new Date(2021,1,1))
                .endDate(new Date(2021, 3, 1))
                .build();

        sprint2 = new Sprint.Builder()
                .sprintName("Sprint 2")
                .description("Attempt 2")
                .project(project)
                .startDate(new Date(2021,3,1))
                .endDate(new Date(2021, 6, 1))
                .build();
        projectRepository.save(project);
        sprintRepository.save(sprint1);
        sprintRepository.save(sprint2);
    }

    @AfterEach
    public void tearDown() {
        sprintRepository.delete(sprint1);
        sprintRepository.delete(sprint2);
        projectRepository.delete(project);
    }

    @Test
    public void givenSprintExists_FindBySprintName() {
        List<Sprint> sprintList = Arrays.asList(sprint1);
        assertArrayEquals(sprintList.toArray(), sprintRepository.findBySprintName("Sprint 1").toArray());
    }

    @Test
    public void givenSprintExists_FindBySprintNameContaining() {
        List<Sprint> sprintList = Arrays.asList(sprint1, sprint2);
        assertArrayEquals(sprintList.toArray(),sprintRepository.findBySprintNameContaining("Sprint").toArray());
    }

    @Test
    public void givenSprintExists_FindByDescription() {
        List<Sprint> sprintList = Arrays.asList(sprint1);
        assertArrayEquals(sprintList.toArray(), sprintRepository.findByDescription("Attempt 1").toArray());
    }

    @Test
    public void givenSprintExists_FindByDescriptionContaining() {
        List<Sprint> sprintList = Arrays.asList(sprint1, sprint2);
        assertArrayEquals(sprintList.toArray(), sprintRepository.findByDescriptionContaining("Attempt").toArray());
    }

    @Test
    public void givenSprintExists_FindByStartDate() {
        List<Sprint> sprintList = Arrays.asList(sprint1);
        assertArrayEquals(sprintList.toArray(), sprintRepository.findByStartDate(new Date(2021,1,1)).toArray());
    }

    @Test
    public void givenSprintExists_FindByEndDate() {
        List<Sprint> sprintList = Arrays.asList(sprint1);
        assertArrayEquals(sprintList.toArray(), sprintRepository.findByEndDate(new Date(2021, 3, 1)).toArray());
    }

    @Test
    public void givenSprintExists_FindByProject() {
        List<Sprint> sprintList = Arrays.asList(sprint1, sprint2);
        assertArrayEquals(sprintList.toArray(), sprintRepository.findByProject(project).toArray());
    }

    @Test
    public void givenSprintExists_CountByProject() {
        assertEquals(2, sprintRepository.countByProject(project));
    }
}