package nz.ac.canterbury.seng302.portfolio.services;

import nz.ac.canterbury.seng302.portfolio.model.Milestone;
import nz.ac.canterbury.seng302.portfolio.model.MilestoneRepository;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.ProjectRepository;
import nz.ac.canterbury.seng302.portfolio.service.IncorrectDetailsException;
import nz.ac.canterbury.seng302.portfolio.service.MilestoneService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@SpringBootTest
public class MilestoneServiceTest {

    @MockBean
    private MilestoneRepository milestoneRepository;

    @MockBean
    private ProjectRepository projectRepository;

    private MilestoneService milestoneService;

    private Milestone milestone;
    private Project project;

    @BeforeEach
    public void setUp() {
        project = new Project.Builder()
                .startDate(new java.sql.Date(2020 - 1900, 3, 12))
                .endDate(new java.sql.Date(2023 - 1900, 1, 10))
                .projectName("Project 2020")
                .projectId(1)
                .build();

        milestone = new Milestone.Builder()
                .milestoneId(1)
                .date(new java.sql.Date(2020 - 1900, 4, 12))
                .project(project)
                .build();

        milestoneService = new MilestoneService();
        when(milestoneRepository.findById(1)).thenReturn(Optional.ofNullable(milestone));
        when(projectRepository.findById(1)).thenReturn(Optional.ofNullable(project));
        when(milestoneRepository.findByProject(project)).thenReturn(List.of(milestone));
    }

    @Test
    public void givenMilestoneExists_whenMilestoneRequested_thenNoExceptionThrown() {
        assertDoesNotThrow(()-> {
            milestoneService.getMilestone(1);
        });
    }

    @Test
    public void givenMilestoneDoesNotExist_whenMilestoneRequested_thenExceptionThrown() {
        IncorrectDetailsException exception = assertThrows(IncorrectDetailsException.class, () ->
                milestoneService.getMilestone(99));
        assertEquals("Failed to locate milestone in the database", exception.getMessage());
    }

    @Test
    public void givenMilestoneAndProjectExist_whenMilestoneByProjectRequested_thenAListOfMilestoneIsReturned() {
        try {
            assertEquals(List.of(milestone), milestoneService.getMilestoneByProject(1));
        } catch (IncorrectDetailsException e) {
            fail("Should not have thrown an exception");
        }
    }
}
