package nz.ac.canterbury.seng302.portfolio.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * A class for retrieving milestones from the database.
 */
@Repository
public interface MilestoneRepository extends CrudRepository<Milestone, Integer> {

    /**
     * Get a list of milestones that are stored with the given name
     * @param milestoneName Name of the milestone(s)
     * @return List of milestone(s) with the given name
     */
    public List<Milestone> findByName(String milestoneName);

    /**
     * Obtains a list of milestones in the given project.
     * @param project Project containing milestones
     * @return List of milestone(s) with the given project
     */
    public List<Milestone> findByProject(Project project);

    /**
     * Counts the milestones based on the given project
     * @param project Project containing milestone(s)
     * @return A count of milestone(s) within the project
     */
    public int countByProject(Project project);

}
