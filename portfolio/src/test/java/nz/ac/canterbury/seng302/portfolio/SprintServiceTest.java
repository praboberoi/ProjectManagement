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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SprintServiceTest {
    /**
     * Use mocking support provided by Spring Boot Test
     * to test Service layer code
     * @return
     */
    @Bean
    public SprintService sprintService(){
        return new SprintService();
    }
    /**
     * Creates a Mock for the SprintRepository, which can be used to bypass the call
     * to actual SprintRepository
     */
    @MockBean
    private SprintRepository sprintRepository;
    @Autowired
    private SprintService sprintService;
    @Autowired
    private ProjectRepository projectRepository;
    private Sprint sprint1;
    private Sprint sprint2;
    private Sprint sprint3;
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
                .startDate(new Date(2021, 3, 1))
                .endDate(new Date(2021, 6, 1))
                .build();

        projectRepository.save(project);
        sprintRepository.save(sprint1);
        sprintRepository.save(sprint2);
    }

    /**
     * Tests that all sprints in a project get deleted
     * @throws Exception
     */
    @Test
    public void givenSprintExists_DeleteAllSprints() throws Exception {
        sprintService.deleteAllSprints(project.getProjectId());
        sprintService.getSprintByProject(project.getProjectId());
        assertEquals(null,sprintService.getSprintByProject(project.getProjectId()));
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
    public void givenNewSprint_AddNewSprint() {
        assertEquals(sprint3, sprintService.getNewSprint(project));
        assertNotEquals(sprint1, sprintService.getNewSprint(project));

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
        assertEquals(2, sprintService.countByProjectId(project.getProjectId()));
        assertNotEquals(1, sprintService.countByProjectId(project.getProjectId()));
    }

    /**
     * Tests the list of sprints returned is correct
     */
    @Test
    public void givenProjectId_GetListOfSprints() {
        List<Sprint> sprintList = Arrays.asList(sprint1,sprint2);
        assertArrayEquals(sprintList.toArray(), sprintService.getSprintByProject(project.getProjectId()).toArray());
    }
}
