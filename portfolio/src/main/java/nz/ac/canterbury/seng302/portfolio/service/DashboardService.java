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

    private boolean verifyProject(Project project) {
        if (project.getProjectName().isEmpty() || project.getStartDate() == null || project.getEndDate() == null)
            return false;
        else
            return true;
    }
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
    public void saveProject(Project project) throws Exception {
        if (!verifyProject(project))
            throw new Exception("Project Details Incomplete");
        else {
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

