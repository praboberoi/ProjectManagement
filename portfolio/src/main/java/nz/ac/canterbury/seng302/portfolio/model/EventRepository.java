package nz.ac.canterbury.seng302.portfolio.model;

import org.springframework.data.repository.CrudRepository;
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


}
