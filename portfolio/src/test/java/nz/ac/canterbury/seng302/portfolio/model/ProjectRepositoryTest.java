package nz.ac.canterbury.seng302.portfolio.model;


import org.junit.jupiter.api.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import java.sql.Date;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProjectRepositoryTest {
    @Autowired private ProjectRepository projectRepo;

    @Test
    public void givenProjectExists_FindByProjectName() {
        Project project1 = new Project.Builder()
                .projectName("Project 2020")
                .description("First Attempt")
                .startDate(new Date(2020, 3, 12))
                .endDate(new Date(2021, 1, 10))
                .build();

        Project project2 = new Project.Builder()
                .projectName("Project 2020")
                .description("Second Attempt")
                .startDate(new Date(2021, 1, 9))
                .endDate(new Date(2021, 11, 20))
                .build();

        List<Project> projList = Arrays.asList(project1, project2);
        projectRepo.save(project1);
        projectRepo.save(project2);
        assertArrayEquals(projList.toArray(), projectRepo.findByProjectName("Project 2020").toArray());
        projectRepo.delete(project1);
        projectRepo.delete(project2);
    }

    @Test
    public void givenProjectsExists_FindByProjectNameContaining() {
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

        projectRepo.delete(project1);
        projectRepo.delete(project2);
    }

    @Test
    public void givenDescriptionExists_findByDescription() {
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
        projectRepo.delete(project1);
        projectRepo.delete(project2);

    }

    @Test
    public void givenDescriptionExists_findByDescriptionContaining() {
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

        projectRepo.delete(project1);
        projectRepo.delete(project2);

    }

    @Test
    public void givenDateExists_FindByStartDate() {
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
    public void givenEndDateExists_FindByEndDate() {
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