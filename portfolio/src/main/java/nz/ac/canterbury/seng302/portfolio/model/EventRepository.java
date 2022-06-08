package nz.ac.canterbury.seng302.portfolio.model;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends CrudRepository<Event, Integer>{

    public List<Event> findByEventName(String eventName);
    
}
