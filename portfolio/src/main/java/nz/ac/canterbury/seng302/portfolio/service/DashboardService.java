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
    private Project currentProject = null;

    public List<Project> listAll() {
        return (List<Project>) projectRepo.findAll();
    }

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
    
    public Project getProject(Long projectId) {
        Optional<Project> result = projectRepo.findById(projectId);
        currentProject = result.get();
        return currentProject;
    }

    public void deleteProject(Long projectId) throws Exception {
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

