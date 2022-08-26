package nz.ac.canterbury.seng302.portfolio.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class SprintTest {
    private Sprint sprint1;
    private Sprint sprint2;
    private Project project;

    @BeforeEach
    public void setup() {
         project = new Project.Builder()
                 .projectId(1)
                .projectName("Project 2020")
                .description("First Attempt")
                .startDate(new Date(2020, 3, 12))
                .endDate(new Date(2021, 1, 10))
                .build();

         sprint1 = new Sprint.Builder()
                .sprintId(1)
                .sprintName("Sprint 1")
                .description("Attempt 1")
                .project(project)
                .startDate(new Date(2021,1,1))
                .endDate(new Date(2021, 3, 1))
                .build();

        sprint2 = new Sprint.Builder()
                .sprintId(2)
                .sprintName("Sprint 2")
                .description("Attempt 2")
                .project(project)
                .startDate(new Date(2021,3,1))
                .endDate(new Date(2021, 6, 1))
                .build();
    }


    @Test
    void givenSprintExists_GetSprintId() {
        assertEquals(1,sprint1.getSprintId());
        assertNotEquals(2, sprint1.getSprintId());
    }

    @Test
    void givenSprintExists_GetProject() {
        Project project2 = new Project.Builder()
                .projectName("Project 2021")
                .description("Second Attempt")
                .startDate(new Date(2021, 1, 9))
                .endDate(new Date(2021, 11, 20))
                .build();

        assertEquals(project, sprint1.getProject());
        assertNotEquals(project2, sprint1.getProject());
    }

    @Test
    void givenSprintExists_SetProject() {
        Project proj = new Project();
        sprint1.setProject(proj);
        assertEquals(proj, sprint1.getProject());
        assertNotEquals(project, sprint1.getProject());
    }

    @Test
    void givenSprintExists_GetSprintName() {
        assertEquals("Sprint 1", sprint1.getSprintName());
        assertNotEquals("Sprint 2", sprint1.getSprintName());
    }

    @Test
    void givenSprintExists_SetSprintName() {
        sprint1.setSprintName("Test");
        assertEquals("Test", sprint1.getSprintName());
        assertNotEquals("Sprint 1", sprint1.getSprintName());
    }

    @Test
    void givenSprintExists_GetDescription() {
        assertEquals("Attempt 1", sprint1.getDescription());
        assertNotEquals("Attempt 2", sprint1.getDescription());
    }

    @Test
    void givenSprintExistsSetDescription() {
        sprint1.setDescription("Test");
        assertEquals("Test", sprint1.getDescription());
        assertNotEquals("Attempt 1", sprint1.getDescription());
    }

    @Test
    void givenSprintExists_GetStartDate() {
        assertEquals(new Date(2021,1,1), sprint1.getStartDate());
        assertNotEquals(new Date(2021,1,1), sprint2.getStartDate());
    }

    @Test
    void givenSprintExists_SetStartDate() {
        sprint1.setStartDate(new Date(2022, 1, 1));
        assertEquals(new Date(2022, 1, 1), sprint1.getStartDate());
        assertNotEquals(new Date(2021,1,1), sprint1.getStartDate());
    }

    @Test
    void givenSprintExists_GetEndDate() {
        assertEquals(new Date(2021, 3, 1), sprint1.getEndDate());
        assertNotEquals(new Date(2021, 10, 10),sprint1.getEndDate());
    }

    @Test
    void givenSprintExists_SetEndDate() {
        sprint1.setEndDate(new Date(2022, 3, 1));
        assertEquals(new Date(2022, 3, 1), sprint1.getEndDate());
        assertNotEquals(new Date(2021, 3, 1), sprint1.getEndDate());
    }


    @Test
    void testEquals() {
        assertEquals(false, sprint1.equals(sprint2));
    }

}