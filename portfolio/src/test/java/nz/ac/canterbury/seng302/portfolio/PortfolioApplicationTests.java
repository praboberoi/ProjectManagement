package nz.ac.canterbury.seng302.portfolio;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PortfolioApplicationTests {

    @Test
    void contextLoads() {
    }

//    @Test
//    void testDeleteProject() throws Exception {
//        Project testProject = new Project("testProject", "project to test the delete method",
//                "12-12-2019", "08-08-2020");
//        int projId = testProject.getProjectId();
//        DashboardService dashboardService = new DashboardService();
//        dashboardService.deleteProject(projId);
//        assertEquals(null, dashboardService.getProject(projId), "Project should not be found");
//
//    }

}
