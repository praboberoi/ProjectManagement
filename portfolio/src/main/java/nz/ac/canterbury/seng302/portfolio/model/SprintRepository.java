package nz.ac.canterbury.seng302.portfolio.model;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
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
    List<Sprint> findBySprintName(String sprintName);

    /**
     * Obtains a list of sprints with the name containing the given string
     * @param name of type String
     * @return a list of type Sprint
     */
    List<Sprint> findBySprintNameContaining(String name);

    /**
     * Obtains a list of sprints with the given description.
     * @param description of type String.
     * @return a list of type Sprint.
     */
    List<Sprint> findByDescription(String description);

    /**
     * Obtains a list of sprints with description containing the give string
     * @param description of type String
     * @return a list of type Sprint.
     */
    List<Sprint> findByDescriptionContaining(String description);

    /**
     * Obtains a list of sprints that start on the given date.
     * @param startDate of type Date.
     * @return a list of type Sprint.
     */
    List<Sprint> findByStartDate(Date startDate);

    /**
     * Obtains a list of sprints that end on the given date.
     * @param endDate of type Date.
     * @return a list of type Sprint.
     */
    List<Sprint> findByEndDate(Date endDate);

    /**
     * Obtains a list of sprints containing the given project.
     * @param project of type Project.
     * @return list of type Sprint.
     */
    List<Sprint> findByProject(Project project);

    /**
     * Finds a sprint occurring within a project on a certain date
     * @param project The project the sprint is from
     * @param date The date the sprint must occur during
     * @return The sprint
     */
    @Query(value = "select distinct sprint from Sprint sprint where :#{#date} between sprint.startDate and sprint" +
            ".endDate and sprint.project.projectId = :#{#project.projectId}")
    Sprint findByDateAndProject(Project project, Date date);

    /**
     * Counts the sprints based on the given project.
     * @param project of type Project.
     * @return an int.
     */
    int countByProject(Project project);

    /**
     * Obtains a list of sprint that fall under the given Deadline
     * @param deadline of type Deadline
     * @return a list of Sprint
     */
    @Query(value = "SELECT DISTINCT sprint from Sprint sprint where :#{#deadline.date} <= sprint.endDate and :#{#deadline.date} >= sprint.startDate")
    List<Sprint> findSprintsByDeadline(@Param("deadline") Deadline deadline);
}
