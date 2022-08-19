package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.*;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.PersistenceException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Unit tests for methods in the sprintService class
 */
@SpringBootTest
@ActiveProfiles("test")
class DashboardServiceTest {
    
    @SpyBean
    private SprintRepository sprintRepository;

    @MockBean
    private EvidenceRepository evidenceRepository;
    @MockBean
    private ProjectRepository projectRepository;

    @Autowired
    private DashboardService dashboardService;

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
    public void givenValidProject_whenProjectValidated_thenSucceedsValidation() {
        Project project = projectBuilder.build();
        assertDoesNotThrow(() -> {
            dashboardService.verifyProject(project);
        });
    }

    /**
     * Tests that a sprint with too long a description will not be valid
     */
    @Test
    public void givenInvalidSprintDescription_whenSprintValidated_thenFailsValidation() {
        Project project = projectBuilder
            .description("0123456789".repeat(26)) //260 characters
            .build();
            assertThrows(Exception.class, () -> {dashboardService.verifyProject(project);});
    }

    /**
     * Tests that a sprint with the maximum character count will be valid
     */
    @Test
    public void givenValidLargeSprintDescription_whenSprintValidated_thenFailsValidation() {
        Project project = projectBuilder
            .description("0123456789".repeat(25)) //250 characters
            .build();
            assertDoesNotThrow(() -> {
                dashboardService.verifyProject(project);
            });
    }

    /**
     * Checks that all sample data projects are returned
     */
    @Test
    public void givenProjectDatabaseWithProjects_whenGetAllProjectsCalled_thenAllProjectReturned() {
        List<Project> list = new ArrayList<Project>();
        list.add(defaultProject);
        Iterable<Project> iterable = list;
        when(projectRepository.findAll()).thenReturn(iterable);
        List<Project> returnList = dashboardService.getAllProjects();
        assertEquals(1, returnList.size());
    }

    /**
     * Checks that correct message is returned when new project successfully saved
     */
    @Test
    public void givenNewProject_whenSaveProject_thenProjectSaved() {
        Project testProject = projectBuilder.build();
        when(projectRepository.save(any())).thenReturn(testProject);
        String returnMessage = dashboardService.saveProject(testProject);
        assertEquals("Successfully Created " + testProject.getProjectName(), returnMessage);
    }

    /**
     * Checks that correct message is returned when edited project successfully saved
     */
    @Test
    public void givenEditedProject_whenSaveProject_thenProjectSaved() {
        Project testProject = projectBuilder.projectId(1).build();
        when(projectRepository.save(any())).thenReturn(testProject);
        String returnMessage = dashboardService.saveProject(testProject);
        assertEquals("Successfully Updated " + testProject.getProjectName(), returnMessage);
    }

    /**
     * Checks that error is thrown when edited project unsuccessfully saved
     */
    @Test
    public void givenIncorrectProject_whenSaveProject_thenErrorThrown() {
        Project testProject = projectBuilder.projectId(1).build();
        when(projectRepository.save(any())).thenThrow(PersistenceException.class);
        PersistenceException exception = assertThrows(PersistenceException.class, () -> {dashboardService.saveProject(testProject);});
        assertEquals("Failure Saving Project", exception.getMessage());
    }
}