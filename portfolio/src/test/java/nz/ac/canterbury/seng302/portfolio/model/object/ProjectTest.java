package nz.ac.canterbury.seng302.portfolio.model.object;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.utils.IncorrectDetailsException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ProjectTest {
    private Project project1;
    private Project project2;

    @BeforeEach
    public void setup() {
        project1 = new Project.Builder()
                .projectId(1)
                .projectName("Project 2020")
                .description("First Attempt")
                .startDate(new Date(2020, 3, 12))
                .endDate(new Date(2021, 1, 10))
                .build();

        project2 = new Project.Builder()
                .projectId(2)
                .projectName("Project 2021")
                .description("Second Attempt")
                .startDate(new Date(2021, 1, 9))
                .endDate(new Date(2021, 11, 20))
                .build();
    }

    @Test
    void givenProjectsExits_Equals(){
        assertEquals(false, project1.equals(null));
        assertEquals(false, project1.equals(project2));
    }

    @Test
    void givenProjectExists_GetProjectId() {
        assertEquals(1, project1.getProjectId());
        assertNotEquals(2, project1.getProjectId());
    }

    @Test
    void givenProjectExists_GetProjectName() {
        assertEquals("Project 2020", project1.getProjectName());
        assertNotEquals("Project 2021", project1.getProjectName());
    }

    @Test
    void givenProjectExists_GetDescription() {
        assertEquals("First Attempt", project1.getDescription());
        assertNotEquals("Testing", project1.getDescription());
    }

    @Test
    void givenProjectExists_GetStartDate() {
        assertEquals(new Date(2020, 3, 12), project1.getStartDate());
        assertNotEquals(new Date(2023, 1,1), project1.getStartDate());
    }

    @Test
    void givenProjectExists_GetEndDate() {
        assertEquals(new Date(2021, 1, 10), project1.getEndDate());
        assertNotEquals(new Date(2023, 2,1), project1.getEndDate());
    }

    @Test
    void givenProjectExists_SetProjectName() throws IncorrectDetailsException{
        project1.setProjectName("Project 2025");
        assertEquals("Project 2025", project1.getProjectName());
        assertNotEquals("Project 2020", project1.getProjectName());
    }

    @Test
    void givenProjectExists_SetDescription() {
        project1.setDescription("Updated");
        assertEquals("Updated", project1.getDescription());
        assertNotEquals("First Attempt", project1.getDescription());
    }

    @Test
    void givenProjectExists_SetStartDate() {

        project1.setStartDate(new Date(2020, 4, 1));
        assertEquals(new Date(2020, 4, 1), project1.getStartDate());
        assertNotEquals(new Date(2020, 3, 12), project1.getStartDate());
    }

    @Test
    void givenProjectExists_SetEndDate() {
        project1.setEndDate(new Date(2020, 12,1));
        assertEquals(new Date(2020, 12,1), project1.getEndDate());
        assertNotEquals(new Date(2021, 1, 10), project1.getEndDate());
    }

}
