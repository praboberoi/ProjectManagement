package nz.ac.canterbury.seng302.portfolio;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.ProjectRepository;
import nz.ac.canterbury.seng302.portfolio.service.DashboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DashboardServiceTest {
    @Mock
    ProjectRepository projectRepository;

    @InjectMocks
    DashboardService dashboardService;

    private Project project1;
    private Project project2;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        project1 = new Project.Builder()
                .projectName("Test Project 1")
                .description("Test project description")
                .startDate(new Date(2021, 1, 1))
                .endDate(new Date(2021, 1, 8))
                .build();

        project2 = new Project.Builder()
                .projectName("Test Project 2")
                .description("Test project description")
                .startDate(new Date(2021, 1, 1))
                .endDate(new Date(2021, 1, 8))
                .build();
    }

    @Test
    public void givenProjectDatabaseWithProjects_whenGetAllProjects_thenReturnListProject() throws Exception {
        List<Project> projectList;
        projectList = new ArrayList<Project>();
        projectList.add(project1);
        projectList.add(project2);
        when(projectRepository.findAll()).thenReturn(projectList);
        List<Project> returnList = dashboardService.getAllProjects();
        assertEquals(returnList, projectList);

    }
}
