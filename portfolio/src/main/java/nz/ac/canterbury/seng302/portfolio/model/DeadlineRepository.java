package nz.ac.canterbury.seng302.portfolio.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

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
     * Obtains a list of deadlines in the given project.
     * @param project Project containing deadlines
     * @return List of deadline(s) with the given project
     */
    public List<Deadline> findByProject(Project project);

    /**
     * Counts the deadlines based on the given project
     * @param project Project containing deadline(s)
     * @return A count of deadline(s) within the project
     */
    public int countByProject(Project project);

}
