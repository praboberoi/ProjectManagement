package nz.ac.canterbury.seng302.portfolio.model;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends CrudRepository<Event, Integer>{

    /**
     * Get a list of events that are stored with the given name
     * @param eventName Event name to search
     * @return A list of events found
     */
    List<Event> findByEventName(String eventName);

    /**
     * Get the event with the given ID
     * @param eventId Event's ID
     * @return The corrosponding event
     */
    Event findById(int eventId);

    /**
     * Obtains a list of events within the given project.
     * @param project of type Project.
     * @return list of type Event.
     */
    List<Event> findByProject(Project project);

    /**
     * Gets a list of deadlines that occur during a givent sprint
     * @param sprint the sprint to search for deadlines within
     * @return A list of deadlines occuring within the given sprint
     */

    @Query(value = "SELECT DISTINCT event FROM Event event WHERE event.startDate BETWEEN :#{#sprint.startDate} " +
            "AND :#{#sprint.endDate} AND event.project = :#{#sprint.project}")
    List<Event> findEventsBySprint(@Param("sprint") Sprint sprint);


}
