package nz.ac.canterbury.seng302.portfolio.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

/**
 * Repository class that extends Crud repository and provides methods for making query calls to the database;
 */
@Repository
public interface SprintRepository extends CrudRepository<Sprint, Integer> {



    /**
     * Obtains a list of sprints with the given sprint name.
     * @param sprintName of type String.
     * @return a list of type Sprint.
     */
    public List<Sprint> findBySprintName(String sprintName);

    /**
     * Obtains a list of sprints with the name containing the given string
     * @param name of type String
     * @return a list of type Sprint
     */
    public List<Sprint> findBySprintNameContaining(String name);

    /**
     * Obtains a list of sprints with the given description.
     * @param description of type String.
     * @return a list of type Sprint.
     */
    public List<Sprint> findByDescription(String description);

    /**
     * Obtains a list of sprints with description containing the give string
     * @param description of type String
     * @return a list of type Sprint.
     */
    public List<Sprint> findByDescriptionContaining(String description);

    /**
     * Obtains a list of sprints that start on the given date.
     * @param startDate of type Date.
     * @return a list of type Sprint.
     */
    public List<Sprint> findByStartDate(Date startDate);

    /**
     * Obtains a list of sprints that end on the given date.
     * @param endDate of type Date.
     * @return a list of type Sprint.
     */
    public List<Sprint> findByEndDate(Date endDate);

    /**
     * Obtains a list of sprints containing the given project.
     * @param project of type Project.
     * @return list of type Sprint.
     */
    public List<Sprint> findByProject(Project project);

    /**
     * Counts the sprints based on the given project.
     * @param project of type Project.
     * @return an int.
     */
    public int countByProject(Project project);

}
