package nz.ac.canterbury.seng302.portfolio.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface ProjectRepository extends CrudRepository<Project, Integer> {

    /**
     * Obtains a list of projects with the given name.
     * @param projectName of type String.
     * @return A project with the given name.
     */
    Project findByProjectName(String projectName);

    /**
     * Obtains a list of projects with the name containing the given string
     * @param name of type String
     * @return a list of type Project
     */
    List<Project> findByProjectNameContaining(String name);

    /**
     * Obtains a list of projects with the given description.
     * @param description of type String.
     * @return a list of type Sprint.
     */
    List<Project> findByDescription(String description);

    /**
     * Obtains a list of projects with description containing the give string
     * @param description of type String
     * @return a list of type Project.
     */
    List<Project> findByDescriptionContaining(String description);

    /**
     * Obtains a list of projects that start on the given date.
     * @param startDate of type Date.
     * @return a list of type Sprint.
     */
    Project findByStartDate(Date startDate);

    /**
     * Obtains a list of projects that end on the given date.
     * @param endDate of type Date.
     * @return a list of type Project.
     */
    Project findByEndDate(Date endDate);

}
