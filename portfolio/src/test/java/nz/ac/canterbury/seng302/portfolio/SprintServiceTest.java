package nz.ac.canterbury.seng302.portfolio;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.ProjectRepository;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.model.SprintRepository;
import nz.ac.canterbury.seng302.portfolio.service.SprintService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SprintServiceTest {
    /**
     * Use Autowire to place instance of bean
     * returns an instance of class created with @Bean annotation
     */
    @Autowired
    private SprintRepository sprintRepository;
    @Autowired
    private SprintService sprintService;
    @Autowired
    private ProjectRepository projectRepository;
    private Sprint sprint1;
    private Sprint sprint2;
    private Sprint sprint3;
    private Sprint sprint4;
    private Project project;

    /**
     * setup data before each test
     */
    @BeforeEach
    public void setUp() {
        project = new Project.Builder()
                .projectName("Project 2020")
                .description("First Attempt")
                .startDate(new Date(2021, 1, 1))
                .endDate(new Date(2021, 12, 10))
                .build();

        sprint1 = new Sprint.Builder()
                .sprintName("Sprint 1")
                .description("Attempt 1")
                .project(project)
                .startDate(new Date(2021, 1, 1))
                .endDate(new Date(2021, 3, 1))
                .build();

        sprint2 = new Sprint.Builder()
                .sprintName("Sprint 2")
                .description("Attempt 2")
                .project(project)
                .startDate(new Date(2021, 3, 1))
                .endDate(new Date(2021, 6, 1))
                .build();

        sprint3 = new Sprint.Builder()
                .sprintName("Sprint 3")
                .description("Attempt 3")
                .project(project)
                .startDate(new Date(2021, 6, 1))
                .endDate(new Date(2021, 9, 1))
                .build();

        sprint4 = new Sprint.Builder()
                .sprintName("Sprint 4")
                .description("Attempt 4")
                .project(project)
                .startDate(new Date(2021, 9, 1))
                .endDate(new Date(2021, 12, 1))
                .build();


        projectRepository.save(project);
        try {
            sprintService.saveSprint(sprint1);
        } catch (Exception e) {
            System.out.println(sprint1);
        }
        try {
            sprintService.saveSprint(sprint2);
        } catch (Exception e) {
            System.out.println(sprint2);
        }
        try {
            sprintService.saveSprint(sprint3);
        } catch (Exception e) {
            System.out.println(sprint3);
        }

    }


    /**
     * Tests that all sprints in a project get deleted
     * @throws Exception
     */
    @Test
    public void givenSprintExists_DeleteAllSprints() {
        List<Sprint> sprintList = Arrays.asList();
        Project project1 = projectRepository.findByProjectName(project.getProjectName()).get(0);
        sprintService.deleteAllSprints(project1.getProjectId());
        sprintService.getSprintByProject(project1.getProjectId());
        assertEquals(sprintList,sprintService.getSprintByProject(project1.getProjectId()));
    }

    /**
     * Tests a sprint has been successfully deleted from database
     * @throws Exception
     */
    @Test
    public void givenSprintId_DeleteFromDatabase() throws Exception {
        assertEquals("Sprint Deleted Successfully", sprintService.deleteSprint(sprint1.getSprintId()));
    }

    /**
     * Tests that a new sprint got added to project
     */
    @Test
    public void givenNewSprint_GetSprintObject() {
        assertEquals(sprint4.getSprintName(), sprintService.getNewSprint(project).getSprintName());
        sprintRepository.delete(sprint4);
    }

    /**
     * Tests the correct sprint object is returned
     * when give the sprintId
     */
    @Test
    public void givenSprintId_GetCurrentSprint(){
        assertEquals(sprint1,sprintService.getSprint(sprint1.getSprintId()));
        assertNotEquals(sprint2,sprintService.getSprint(sprint1.getSprintId()));
    }

    /**
     * Tests the correct number of sprint has been returned
     * given a projectId
     */
    @Test
    public void givenSprintExists_CountByProject() {
        assertEquals(3, sprintService.countByProjectId(project.getProjectId()));
        assertNotEquals(1, sprintService.countByProjectId(project.getProjectId()));
    }

    /**
     * Tests the list of sprints returned is correct
     */
    @Test
    public void givenProjectId_GetListOfSprints() {
        List<Sprint> sprintList = Arrays.asList(sprint1,sprint2,sprint3);
        assertArrayEquals(sprintList.toArray(), sprintService.getSprintByProject(project.getProjectId()).toArray());
    }

    /**
     * Tests a sprint is saved to database
     */
    @Test
    public void givenSprint_SaveToDatabase() throws Exception {
        Sprint sprint5 = new Sprint.Builder()
                .sprintName("Sprint 5")
                .description("Attempt 5")
                .project(project)
                .startDate(new Date(2022, 9, 1))
                .endDate(new Date(2022, 12, 1))
                .build();
        assertEquals("Sprint created successfully", sprintService.saveSprint(sprint5));
        sprintRepository.delete(sprint5);
    }
}
