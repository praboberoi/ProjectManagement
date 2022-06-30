package nz.ac.canterbury.seng302.portfolio.model;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends CrudRepository<Event, Integer>{

    /**
     * Get a list of events that are stored with the given name
     * @param eventName Event name to search
     * @return A list of events found
     */
    List<Event> findByEventName(String eventName);
    
}
