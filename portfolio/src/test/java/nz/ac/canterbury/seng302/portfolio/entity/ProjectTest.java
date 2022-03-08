package nz.ac.canterbury.seng302.portfolio.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProjectTest {
    Project project1, project2;

    @BeforeEach
    public void setup() {
         project1 = new Project.Builder()
                .projectName("Project 2020")
                .description("First Attempt")
                .startDate("1-1-2020")
                .endDate("1-8-2020")
                .build();

        project2 = new Project.Builder()
                .projectName("Project 2021")
                .description("Second Attempt")
                .startDate("1-1-2021")
                .endDate("1-8-2021")
                .build();
    }

    @Test
    public void testEquals_null(){
        assertEquals(false, project1.equals(null));
    }
    @Test
    public void testGetProjectName() {
        assertEquals("Project 2020", project1.getProjectName());
    }

    @Test
    public void testGetDescription() {
        assertEquals("First Attempt", project1.getDescription());
    }

    @Test
    public void TestGetStartDate() {
        assertEquals("1-1-2020", project1.getStartDate());
    }

    @Test
    public void testGetEndDate() {
        assertEquals("1-8-2020", project1.getEndDate());
    }

    @Test
    public void testSetProjectName() {
        project1.setProjectName("Project_2020");
        assertEquals("Project_2020", project1.getProjectName());
    }

    @Test
    public void testSetDescription() {
        project1.setDescription("Updated");
        assertEquals("Updated", project1.getDescription());
    }

    @Test
    public void testSetStartDate() {
        project1.setStartDate("1-4-2020");
        assertEquals("1-4-2020", project1.getStartDate());
    }

    @Test
    public void testSetEndDate() {
        project1.setEndDate("1-12-2020");
        assertEquals("1-12-2020", project1.getEndDate());
    }

    @Test
    public void testEquals() {
        assertEquals(false, project1.equals(project2));
    }

}