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

    public String verifyEvent(Event event){
        if(event == null) return ("No Event");

        if(event.getEventName() == null || event.getEndDate() == null || event.getStartDate() == null)
            return ("Event values are null");

        if(event.getEndDate() != null && event.getStartDate() != null) {
            if(event.getEndDate().before(event.getStartDate())){
                return ("The event end date cannot be before the event start date");
            }
        }
        return("Event has been verified");
    }

}


