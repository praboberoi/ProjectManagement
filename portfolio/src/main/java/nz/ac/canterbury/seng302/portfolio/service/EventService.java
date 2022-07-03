package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Event;
import nz.ac.canterbury.seng302.portfolio.model.EventRepository;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;

/**
    Client service used to communicate to the database
 */
@Service
public class EventService {
    @Autowired EventRepository eventRepository;

    /**
     * Creates a new event with a name
     * @return of type Event
     */
    public Event getNewEvent() {
        try {
            Event newEvent = new Event();
            return newEvent;
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

}


