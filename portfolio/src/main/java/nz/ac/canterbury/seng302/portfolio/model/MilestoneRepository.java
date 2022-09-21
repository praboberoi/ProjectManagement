package nz.ac.canterbury.seng302.portfolio.model;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface MilestoneRepository extends CrudRepository<Milestone, Integer> {

    /**
     * Get a list of milestones that are stored with the given name
     * @param milestoneName Name of the milestone(s)
     * @return List of milestone(s) with the given name
     */
    public List<Milestone> findByName(String milestoneName);

    /**
     * Gets a list of milestones that occur during a given sprint
     * @param sprint the sprint to search for milestones within
     * @return A list of milestones occurring within the given sprint
     */

    @Query(value = "SELECT DISTINCT milestone from Milestone milestone where milestone.date between :#{#sprint.startDate} " +
            "and :#{#sprint.endDate}")
    List<Milestone> findMilestonesBySprint(@Param("sprint") Sprint sprint);


    /**
     * Obtains a list of milestones in the given project.
     * @param project Project containing milestones
     * @return List of milestone(s) with the given project
     */
    public List<Milestone> findByProject(Project project);

    /**
     * Obtains a list of milestones by the given ID
     * @param milestoneID ID of the milestone
     * @return List of a milestone with the given ID
     */
    List<Milestone> findByMilestoneId(int milestoneID);

    /**
     * Obtains a list of milestones by the given Date
     * @param date Date of a milestone
     * @return A list of deadliens with the given Date
     */
    List<Milestone> findByDate(Date date);

    /**
     * Counts the milestones based on the given project
     * @param project Project containing milestone(s)
     * @return A count of milestone(s) within the project
     */
    public int countByProject(Project project);

}
