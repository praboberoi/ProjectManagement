package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DashboardService {
    @Autowired private ProjectRepository projectRepo;
    private Project currentProject = null;

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
        if (currentProject == null) {
            projectRepo.save(project);
        } else {
            currentProject.setProjectName(project.getProjectName());
            currentProject.setDescription(project.getDescription());
            currentProject.setStartDate(project.getStartDate());
            currentProject.setEndDate(project.getEndDate());
            projectRepo.save(currentProject);
            currentProject = null;
        }

    }

    /**
     * Gets project object from the database
     * @param id
     * @return
     */
    public Project getProject(int id) {
        Optional<Project> result = projectRepo.findById(id);
        currentProject = result.get();
        return currentProject;
    }

    /**
     * Deletes a project object from the database
     * @param projectId
     * @throws Exception
     */
    public void deleteProject(int projectId) throws Exception {
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

