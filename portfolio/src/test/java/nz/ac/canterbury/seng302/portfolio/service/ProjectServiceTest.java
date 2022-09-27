package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.EvidenceRepository;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.ProjectRepository;
import nz.ac.canterbury.seng302.portfolio.model.SprintRepository;
import nz.ac.canterbury.seng302.portfolio.utils.IncorrectDetailsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Unit tests for methods in the projectService class
 */
@SpringBootTest
@ActiveProfiles("test")
class ProjectServiceTest {
    
    @SpyBean
    private SprintRepository sprintRepository;

    @MockBean
    private EvidenceRepository evidenceRepository;
    @MockBean
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectService projectService;

    private Project.Builder projectBuilder;

    private Project defaultProject;

    /**
     * Prepares tests by initilizing the sprint service and creating a valid project and sprint builder
     */
    @BeforeEach
    public void setup() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 1);

        projectBuilder = new Project.Builder()
                .projectName("Project 2020")
                .description("First Attempt")
                .startDate(new Date(Calendar.getInstance().getTimeInMillis()))
                .endDate(new Date(c.getTimeInMillis()));

        defaultProject = projectBuilder.build();
    }
    
    /**
     * Tests that a sprint with normal inputs will be valid
     */
    @Test
    void givenValidProject_whenProjectValidated_thenSucceedsValidation() {
        Project project = projectBuilder.build();
        assertDoesNotThrow(() -> {
            projectService.verifyProject(project);
        });
    }

    /**
     * Tests that a sprint with too long a description will not be valid
     */
    @Test
    void givenInvalidSprintDescription_whenSprintValidated_thenFailsValidation() {
        Project project = projectBuilder
            .description("0123456789".repeat(26)) //260 characters
            .build();
            assertThrows(Exception.class, () -> {projectService.verifyProject(project);});
    }

    /**
     * Tests that a sprint with too long a description will not be valid
     */
    @Test
    void givenDuplicateName_whenProjectValidated_thenFailsValidation() {
        Project project = projectBuilder
            .projectId(1)
            .projectName("Test") //260 characters
            .build();
        when(projectRepository.findByProjectName("Test")).thenReturn(new Project());
        assertThrows(Exception.class, () -> {projectService.verifyProject(project);});
    }

    /**
     * Tests that a sprint with the maximum character count will be valid
     */
    @Test
    void givenValidLargeSprintDescription_whenSprintValidated_thenFailsValidation() {
        Project project = projectBuilder
            .description("0123456789".repeat(25)) //250 characters
            .build();
            assertDoesNotThrow(() -> {
                projectService.verifyProject(project);
            });
    }

    /**
     * Checks that all sample data projects are returned
     */
    @Test
    void givenProjectDatabaseWithProjects_whenGetAllProjectsCalled_thenAllProjectReturned() {
        List<Project> list = new ArrayList<Project>();
        list.add(defaultProject);
        Iterable<Project> iterable = list;
        when(projectRepository.findAll()).thenReturn(iterable);
        List<Project> returnList = projectService.getAllProjects();
        assertEquals(1, returnList.size());
    }

    /**
     * Checks that correct message is returned when new project successfully saved
     * @throws IncorrectDetailsException
     */
    @Test
    void givenNewProject_whenSaveProject_thenProjectSaved() throws IncorrectDetailsException {
        Project testProject = projectBuilder.build();
        when(projectRepository.save(any())).thenReturn(testProject);
        String returnMessage = projectService.saveProject(testProject);
        assertEquals("Successfully Created " + testProject.getProjectName(), returnMessage);
    }

    /**
     * Checks that correct message is returned when edited project successfully saved
     * @throws IncorrectDetailsException
     */
    @Test
    void givenEditedProject_whenSaveProject_thenProjectSaved() throws IncorrectDetailsException {
        Project testProject = projectBuilder.projectId(1).build();
        when(projectRepository.save(any())).thenReturn(testProject);
        String returnMessage = projectService.saveProject(testProject);
        assertEquals("Successfully Updated " + testProject.getProjectName(), returnMessage);
    }
}