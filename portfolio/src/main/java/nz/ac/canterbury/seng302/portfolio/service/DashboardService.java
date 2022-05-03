package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Service
public class DashboardService {
    @Autowired private ProjectRepository projectRepo;
    private Project currentProject;
    private Date projectMinDate;
    private Date projectMaxDate;
    private Boolean isNew = false;

    /**
     * Returns list of all Project objects in the database
     * @return list of project objects
     * @throws Exception caught from underlining method calls
     */
    public List<Project> getAllProjects() throws Exception {
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
     * @throws Exception is thrown if unable to save the object in the database
     */
    public String saveProject(Project project) throws Exception {
            String message;
            if (isNew) {
                currentProject = project;
                message = "Successfully Created Project: " + project.getProjectName();
                isNew = false;

            } else {
                currentProject.setProjectName(project.getProjectName());
                currentProject.setDescription(project.getDescription());
                currentProject.setStartDate(project.getStartDate());
                currentProject.setEndDate(project.getEndDate());
                message = "Successfully Saved Project " + project.getProjectName();

            }
            try {
                projectRepo.save(currentProject);
                return message;
            } catch (Exception e) {
                throw new Exception("Failure Saving Project");
            }
    }

    /**
     * Gets project object from the database
     * @param id of type int
     * @return of type Project
     * @throws Exception If the given project ID does not exist
     */
    public Project getProject(int id) throws Exception {
        Optional<Project> result = projectRepo.findById(id);
        if(result.isPresent()) {
            currentProject = result.get();
            projectMinDate = Date.valueOf(currentProject.getStartDate().toLocalDate().minusYears(1));
            projectMaxDate = Date.valueOf(currentProject.getEndDate().toLocalDate().plusYears(10));
            return currentProject;
        }
        else
            throw new Exception("Failed to locate the project in the database");
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
        Project newProject = new Project();
        newProject.setProjectName("Project " + now.getYear());
        newProject.setStartDate(Date.valueOf(now));
        newProject.setEndDate(Date.valueOf(now.plusMonths(8)));
        projectMinDate = Date.valueOf(LocalDate.now().minusYears(1));
        projectMaxDate = Date.valueOf(LocalDate.now().plusYears(10));
        isNew = true;
        return newProject;
    }

    /**
     * Verifies the project dates to make sure the date ranges have not been changed at the client.
     * @param project of type Project
     * @throws Exception indicating page values of the HTML page are manually changed.
     */
    public void verifyProject(Project project) throws Exception {
        if(project.getStartDate().before(projectMinDate) || project.getEndDate().after(projectMaxDate))
            throw new Exception("HTML page values manually changed. Cannot save the given project");
    }

    /**
     * To get the minimum date value the current project can have.
     * @return of type Date
     */
    public Date getProjectMinDate() {
        return projectMinDate;
    }

    /**
     * To get the maximum date value the current project can have.
     * @return of type Date
     */
    public Date getProjectMaxDate() {
        return projectMaxDate;
    }

    /**
     * Clear the cached data by setting the currentProject and isNew to default
     */
    public void clearCache() {
        currentProject = null;
        isNew = false;
    }

}

