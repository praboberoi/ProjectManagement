package nz.ac.canterbury.seng302.portfolio.model;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface DeadlineRepository extends CrudRepository<Deadline, Integer> {

    /**
     * Get a list of deadlines that are stored with the given name
     * @param deadlineName Name of the deadline(s)
     * @return List of deadline(s) with the given name
     */
    public List<Deadline> findByName(String deadlineName);

    /**
     * Gets a list of deadlines that occur during a givent sprint
     * @param sprint the sprint to search for deadlines within
     * @return A list of deadlines occuring within the given sprint
     */

    @Query(value = "select deadlines from Deadline where date between :#{#sprint.startDate} and :#{#sprint.endDate}")
    List<Deadline> findDeadlinesBySprint(@Param("sprint") Sprint sprint);


    /**
     * Obtains a list of deadlines in the given project.
     * @param project Project containing deadlines
     * @return List of deadline(s) with the given project
     */
    public List<Deadline> findByProject(Project project);

    /**
     * Obtains a list of deadlines by the given ID
     * @param deadlineID ID of the deadline
     * @return List of a deadline with the given ID
     */
    List<Deadline> findByDeadlineId(int deadlineID);

    /**
     * Obtains a list of deadlines by the given Date
     * @param date Date of a deadline
     * @return A list of deadliens with the given Date
     */
    List<Deadline> findByDate(Date date);

    /**
     * Counts the deadlines based on the given project
     * @param project Project containing deadline(s)
     * @return A count of deadline(s) within the project
     */
    public int countByProject(Project project);

}
