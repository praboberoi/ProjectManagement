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

    public List<Project> listAll() {
        return (List<Project>) projectRepo.findAll();
    }

    public void saveProject(Project project) {
        projectRepo.save(project);
    }
    
    public Project getProject(String projectName) {
        Optional<Project> result = projectRepo.findById(projectName);
        return result.get();


    }
}

