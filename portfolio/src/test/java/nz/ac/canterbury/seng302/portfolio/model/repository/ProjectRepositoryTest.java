package nz.ac.canterbury.seng302.portfolio.model.repository;


import nz.ac.canterbury.seng302.portfolio.model.EvidenceRepository;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import javax.transaction.Transactional;
import java.sql.Date;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ProjectRepositoryTest {
    @Autowired private ProjectRepository projectRepo;

    @MockBean
    private EvidenceRepository evidenceRepository;

    @Test
    @Transactional
    void givenProjectExists_FindByProjectName() {
        Project project1 = new Project.Builder()
                .projectName("Project 2020")
                .description("First Attempt")
                .startDate(new Date(2020, 3, 12))
                .endDate(new Date(2021, 1, 10))
                .build();

        Project project2 = new Project.Builder()
                .projectName("Project 2021")
                .description("Second Attempt")
                .startDate(new Date(2021, 1, 9))
                .endDate(new Date(2021, 11, 20))
                .build();

        projectRepo.save(project1);
        projectRepo.save(project2);

        assertEquals(project1, projectRepo.findByProjectName("Project 2020"));

        assertNull(projectRepo.findByProjectName("No"));

        projectRepo.delete(project1);
        projectRepo.delete(project2);
    }

    @Test
    @Transactional
    void givenProjectsExists_FindByProjectNameContaining() {
        Project project1 = new Project.Builder()
                .projectName("Project 2020")
                .description("First Attempt")
                .startDate(new Date(2020, 3, 12))
                .endDate(new Date(2021, 1, 10))
                .build();

        Project project2 = new Project.Builder()
                .projectName("Project 2021")
                .description("Second Attempt")
                .startDate(new Date(2021, 1, 9))
                .endDate(new Date(2021, 11, 20))
                .build();


        projectRepo.save(project1);
        projectRepo.save(project2);

        List<Project> projList= Arrays.asList(project1, project2);
        assertArrayEquals(projList.toArray(), projectRepo.findByProjectNameContaining("Project").toArray());

        List<Project> emptyList = Collections.<Project>emptyList();
        assertArrayEquals(emptyList.toArray(), projectRepo.findByProjectNameContaining("No").toArray());

        projectRepo.delete(project1);
        projectRepo.delete(project2);
    }

    @Test
    @Transactional
    void givenDescriptionExists_findByDescription() {
        Project project1 = new Project.Builder()
                .projectName("Project 2020")
                .description("First Attempt")
                .startDate(new Date(2020, 3, 12))
                .endDate(new Date(2021, 1, 10))
                .build();

        Project project2 = new Project.Builder()
                .projectName("Project 2021")
                .description("First Attempt")
                .startDate(new Date(2021, 1, 9))
                .endDate(new Date(2021, 11, 20))
                .build();

        projectRepo.save(project1);
        projectRepo.save(project2);

        List<Project> projList= Arrays.asList(project1, project2);
        assertArrayEquals(projList.toArray(), projectRepo.findByDescription("First Attempt").toArray());

        List<Project> emptyList = Collections.<Project>emptyList();
        assertArrayEquals(emptyList.toArray(), projectRepo.findByDescription("No").toArray());

        projectRepo.delete(project1);
        projectRepo.delete(project2);

    }

    @Test
    @Transactional
    void givenDescriptionExists_findByDescriptionContaining() {
        Project project1 = new Project.Builder()
                .projectName("Project 2020")
                .description("First Attempt")
                .startDate(new Date(2020, 3, 12))
                .endDate(new Date(2021, 1, 10))
                .build();

        Project project2 = new Project.Builder()
                .projectName("Project 2021")
                .description("Second Attempt")
                .startDate(new Date(2021, 1, 9))
                .endDate(new Date(2021, 11, 20))
                .build();


        projectRepo.save(project1);
        projectRepo.save(project2);

        List<Project> projList= Arrays.asList(project1, project2);
        assertArrayEquals(projList.toArray(), projectRepo.findByDescriptionContaining("Attempt").toArray());

        List<Project> emptyList = Collections.<Project>emptyList();
        assertArrayEquals(emptyList.toArray(), projectRepo.findByDescriptionContaining("No").toArray());

        projectRepo.delete(project1);
        projectRepo.delete(project2);

    }

    @Test
    @Transactional
    void givenDateExists_FindByStartDate() {
        Project project1 = new Project.Builder()
                .projectName("Project 2020")
                .description("First Attempt")
                .startDate(new Date(2020, 3, 12))
                .endDate(new Date(2021, 1, 10))
                .build();


        projectRepo.save(project1);
        assertEquals(project1, projectRepo.findByStartDate(new Date(2020, 3, 12)));
        assertNotEquals(project1, projectRepo.findByStartDate(new Date(2021, 1, 9)));
        projectRepo.delete(project1);

    }

    @Test
    @Transactional
    void givenEndDateExists_FindByEndDate() {
        Project project1 = new Project.Builder()
                .projectName("Project 2020")
                .description("First Attempt")
                .startDate(new Date(2020, 3, 12))
                .endDate(new Date(2021, 1, 10))
                .build();


        projectRepo.save(project1);
        assertEquals(project1, projectRepo.findByEndDate(new Date(2021, 1, 10)));
        assertNotEquals(project1, projectRepo.findByEndDate(new Date(2021, 1, 9)));
        projectRepo.delete(project1);
    }
}