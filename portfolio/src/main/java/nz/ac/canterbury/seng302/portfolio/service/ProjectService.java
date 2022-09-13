package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.ProjectRepository;
import nz.ac.canterbury.seng302.portfolio.utils.IncorrectDetailsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// more info here https://codebun.com/spring-boot-crud-application-using-thymeleaf-and-spring-data-jpa/

/**
 * Client service used to communicate to the database and perform business logic for projects
 */
@Service
public class ProjectService {
    @Autowired private ProjectRepository repository;

    /**
     * Get list of all projects
     */
    public List<Project> getAllProjects() {
        return (List<Project>) repository.findAll();
    }

    /**
     * Get project by id
     */
    public Project getProjectById(int id) throws IncorrectDetailsException {

        Optional<Project> project = repository.findById(id);
        if(project.isPresent()) {
            return project.get();
        }
        else
        {
            throw new IncorrectDetailsException("Project not found");
        }
    }
}
