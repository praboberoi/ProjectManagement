package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.entity.Project;
import nz.ac.canterbury.seng302.portfolio.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DashboardService {
    @Autowired private ProjectRepository projectRepo;

    /**
     * Returns list of all Project objecs in the database
     * @return list of project objects
     */
    public List<Project> listAll() {
        return (List<Project>) projectRepo.findAll();
    }

    /**
     * Saves a project object to the database
     * @param project
     */
    public void saveProject(Project project) {
        projectRepo.save(project);
    }

    /**
     * Gets project object from the database
     * @param projectId
     * @return
     */
    public Project getProject(Integer projectId) {
        Optional<Project> result = projectRepo.findById(projectId);
        System.out.print(result.get().getProjectId() + " " + result.get().getProjectName() + " " + result.get().getDescription());
        return result.get();
    }

    /**
     * Deletes a project object from the database
     * @param projectId
     * @throws Exception
     */
    public void deleteProject(Integer projectId) throws Exception {
/*
        Long count = projectRepo.countByProjectName(projectId);
        if (count == null || count == 0) {
            throw new Exception("Could not find any projects with project Id" + projectId);
//          throw new ProjectNotFoundException("Could not find any projects with projectName" + projectName);
        }
*/
        projectRepo.deleteById(projectId);
    }
}

