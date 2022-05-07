package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.PersistenceException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Service
public class DashboardService {
    @Autowired private ProjectRepository projectRepo;

    /**
     * Returns list of all Project objects in the database
     * @return list of project objects
     */
    public List<Project> getAllProjects() {
        List<Project> listProjects = (List<Project>) projectRepo.findAll();
        if (listProjects.isEmpty()) {
            saveProject(getNewProject());
            listProjects = (List<Project>) projectRepo.findAll();
        }
        return listProjects;
    }

    /**
     * Saves a project object to the database and returns an appropriate message depending upon if the project is created
     * or updated
     * @param project of type Project
     * @throws PersistenceException That has been passed by {@link ProjectRepository#save(Object) save}
     */
    public String saveProject(Project project) throws PersistenceException {
            String message;
            if (project.getProjectId() == 0)
                message = "Successfully Created " + project.getProjectName();
            else
                message = "Successfully Updated " + project.getProjectName();

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
        Date projectMinDate;
        Date projectMaxDate;
        if(project.getProjectId() == 0) {
            LocalDate now = LocalDate.now();
            projectMinDate = Date.valueOf(now.minusYears(1));
            projectMaxDate = Date.valueOf(now.plusYears(10));
            if(project.getStartDate().before(projectMinDate) || project.getEndDate().after(projectMaxDate))
                throw new IncorrectDetailsException("Project date range violation");

        } else {
            List<Date> dateRange = getProjectDateRange(getProject(project.getProjectId()));
            projectMinDate = dateRange.get(0);
            projectMaxDate = dateRange.get(1);
            if(project.getStartDate().before(projectMinDate) || project.getEndDate().after(projectMaxDate))
                throw new IncorrectDetailsException("Project date range violation");

            if(project.getStartDate().after(project.getEndDate()))
                throw new IncorrectDetailsException("Start date of the project cannot be after the end date of the project");

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

