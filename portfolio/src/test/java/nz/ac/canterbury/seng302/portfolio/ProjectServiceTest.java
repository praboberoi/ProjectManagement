package nz.ac.canterbury.seng302.portfolio;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.ProjectRepository;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.Date;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProjectServiceTest {
    @Mock
    ProjectRepository projectRepository;

    @InjectMocks
    ProjectService projectService;

    private Project project1;
    private Project project2;
//    private ArrayList<Project> projectList;

    @BeforeEach
    void setup() {
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
    public void getProjectByIdTestNull() throws Exception {
        assertThrows(Exception.class, () -> {
            when(projectRepository.findById(1)).thenReturn(null);
            Project project = projectService.getProjectById(1);
        });
    }
}
