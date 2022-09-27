package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.ProjectRepository;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.utils.IncorrectDetailsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

// more info here https://codebun.com/spring-boot-crud-application-using-thymeleaf-and-spring-data-jpa/

/**
 * Client service used to communicate to the database and perform business logic for projects
 */
@Service
public class ProjectService {
    @Autowired 
    private ProjectRepository projectRepository;

    @Autowired 
    private SprintService sprintService;

    /**
     * Returns list of all Project objects in the database
     * @return list of project objects
     */
    public List<Project> getAllProjects() {
        List<Project> listProjects = (List<Project>) projectRepository.findAll();
        if (listProjects.isEmpty()) {
            projectRepository.save(getNewProject());
            listProjects = (List<Project>) projectRepository.findAll();
        }
        return listProjects;
    }

    /**
     * Get project by id
     */
    public Project getProjectById(int id) throws IncorrectDetailsException {

        Optional<Project> project = projectRepository.findById(id);
        if(project.isPresent()) {
            return project.get();
        }
        else
        {
            throw new IncorrectDetailsException("Project not found");
        }
    }


    /**
     * Saves a project object to the database and returns an appropriate message depending upon if the project is created
     * or updated
     * @param project of type Project
     */
    public String saveProject(Project project) {
        String message;
        if (project.getProjectId() == 0) {
            message = "Successfully Created " + project.getProjectName();
        } else {
            message = "Successfully Updated " + project.getProjectName();
        }
        projectRepository.save(project);
        return message;
    }

    /**
     * Gets project object from the database
     * @param id of type int
     * @return of type Project
     * @throws IncorrectDetailsException If the given project ID does not exist
     */
    public Project getProject(int id) throws IncorrectDetailsException {
        Optional<Project> result = projectRepository.findById(id);
        if(result.isPresent())
            return result.get();
        else
            throw new IncorrectDetailsException("Failed to locate the project in the database");
    }

    /**
     * Deletes a project object from the database
     * @param projectId of type int
     */
    public void deleteProject(int projectId) {
        projectRepository.deleteById(projectId);
    }

    /**
     * Creates a new project with a name(i.e. Project {year}), start date as the current date and end date as the date
     * eight months after the current date.
     * @return of type Project
     */
    public Project getNewProject() {
        LocalDate now = LocalDate.now();
        return new Project.Builder()
                .projectName("Project " + now.getYear())
                .startDate(Date.valueOf(now))
                .endDate(Date.valueOf(now.plusMonths(8)))
                .build();
    }

    /**
     * Verifies the project dates to make sure the date ranges have not been changed at the client.
     * @param project of type Project
     * @throws IncorrectDetailsException indicating page values of the HTML page are manually changed.
     */
    public void verifyProject(Project project) throws IncorrectDetailsException {
        List<Sprint> sprints = sprintService.getSprintByProject(project.getProjectId());
        Project projectName = projectRepository.findByProjectName(project.getProjectName());

        if (projectName != null && projectName.getProjectId() != project.getProjectId()) {
            throw new IncorrectDetailsException("A project already exists with that name.");
        }
        if (sprints.stream().anyMatch(sprint -> sprint.getStartDate().before(project.getStartDate()))) {
            throw new IncorrectDetailsException("You are trying to start the project after you have started a sprint.");
        }
        if (sprints.stream().anyMatch(sprint -> sprint.getEndDate().after(project.getEndDate()))) {
            throw new IncorrectDetailsException("You are trying to end the project before the last sprint has ended.");
        }
        if(project.getStartDate().before(Date.valueOf(LocalDate.now().minusYears(1)))) {
            throw new IncorrectDetailsException("A project cannot start more than a year ago.");
        }
        if ( project.getEndDate().after(Date.valueOf(LocalDate.now().plusYears(10))) ) {
            throw new IncorrectDetailsException("A project cannot end more than ten years from now.");
        }
        if (project.getDescription().length() > 250) {
            throw new IncorrectDetailsException("Project description cannot exceed 250 characters");
        }
    }
}
