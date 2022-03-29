package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
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
    public List<Project> getAllProjects() throws Exception {
        List<Project> listProjects = (List<Project>) projectRepo.findAll();
        if (listProjects.isEmpty()) {

            LocalDate now = LocalDate.now();
            Project defaultProject = new Project();
            defaultProject.setProjectName("Project " + now.getYear()); // Project {year}
            defaultProject.setStartDate(Date.valueOf(now)); // Current date

            defaultProject.setEndDate(Date.valueOf(now.plusMonths(8))); // 8 months from start date
            saveProject(defaultProject);
            listProjects.add(defaultProject);
        }

        return listProjects;
    }

    /**
     * Saves a project object to the database
     * @param project
     */
    public String saveProject(Project project) throws Exception {
            String message;
            if (currentProject == null) {
                currentProject = project;
                message = "Successfully Created Project: " + project.getProjectName();

            } else {
                currentProject.setProjectName(project.getProjectName());
                currentProject.setDescription(project.getDescription());
                currentProject.setStartDate(project.getStartDate());
                currentProject.setEndDate(project.getEndDate());
                message = "Successfully Saved Project: " + project.getProjectName();

            }
            try {
                projectRepo.save(currentProject);
                currentProject = null;
                return message;
            } catch (Exception e) {
                throw new Exception("Failure Saving Project");
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
    public void deleteProject(int projectId) {
        projectRepo.deleteById(projectId);
    }
}

