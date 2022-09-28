package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.ProjectRepository;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.utils.IncorrectDetailsException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class DashboardService {
    @Autowired private ProjectRepository projectRepo;
    @Autowired private SprintService sprintService;

    public DashboardService(ProjectRepository projectRepo, SprintService sprintService) {
        this.projectRepo = projectRepo;
        this.sprintService = sprintService;
    }

    /**
     * Returns list of all Project objects in the database
     * @return list of project objects
     */
    public List<Project> getAllProjects() {
        List<Project> listProjects = (List<Project>) projectRepo.findAll();
        if (listProjects.isEmpty()) {
            projectRepo.save(getNewProject());
            listProjects = (List<Project>) projectRepo.findAll();
        }
        return listProjects;
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
        projectRepo.save(project);
        return message;
    }

    /**
     * Gets project object from the database
     * @param id of type int
     * @return of type Project
     * @throws IncorrectDetailsException If the given project ID does not exist
     */
    public Project getProject(int id) throws IncorrectDetailsException {
        Optional<Project> result = projectRepo.findById(id);
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
        projectRepo.deleteById(projectId);
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
        List<Sprint> sprints = sprintService.getSprintsByProject(project.getProjectId());
        Project projectName = projectRepo.findByProjectName(project.getProjectName());

        String emojiRex = "[^\\p{L}\\p{M}\\p{N}\\p{P}\\p{Z}\\p{Cf}\\p{Cs}\\p{Punct}]";

        String expectedName = String.join("", List.of(project.getProjectName().strip().split(emojiRex)));
        String expectedDescription = String.join("", List.of(project.getDescription().strip().split(emojiRex)));

        if (! expectedName.equals(project.getProjectName()))
            throw new IncorrectDetailsException("Project name must not contain an emoji");

        if (! expectedDescription.equals(project.getDescription()))
            throw new IncorrectDetailsException("Project description must not contain an emoji");

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

    /**
     * Obtains the date ranges of the project
     * @param project Of type project
     * @return List of project min and max dates
     */
    public List<Date> getProjectDateRange(Project project) {
        Date projectMinDate = Date.valueOf(project.getStartDate().toLocalDate().minusYears(1));
        Date projectMaxDate = Date.valueOf(project.getEndDate().toLocalDate().plusYears(10));
        return List.of(projectMinDate,projectMaxDate);
    }


}

