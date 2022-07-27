package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Event;
import nz.ac.canterbury.seng302.portfolio.model.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
    Client service used to communicate to the database
 */
@Service
public class EventService {
    @Autowired EventRepository eventRepository;
    private boolean isNew = false;
    private Event currentEvent;

    /**
     * Creates a new event with a name
     * @return of type Event
     */
    public Event getNewEvent() {
        try {
            Event newEvent = new Event();
            isNew = true;
            return newEvent;
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

    /**
     * Verifies the event date and time
     * @param event The event object to verify
     * @return Message explaining the error
     * */
    public String verifyEvent(Event event) throws ParseException {
        if (event == null) return ("No Event");

        if (event.getEventName() == null || event.getEndDate() == null || event.getStartDate() == null || event.getStartTime() == null || event.getEndTime() == null) {
            return ("Event values are null");
        } else if (event.getEndDate().before(event.getStartDate())) {
            return ("The event end date cannot be before the event start date");
        } else {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                Date d1 = (Date) sdf.parse(event.getStartTime());
                Date d2 = (Date) sdf.parse(event.getEndTime());
                // Check start date is same as end date
                if (event.getStartDate().equals(event.getEndDate()) && (d2.before(d1) || d2.equals(d1))) {
                    // End time before start time or Start and end time is the same
                    return ("The event must start before the event ends");
                }
                return("Event has been verified");
            } catch (ParseException e) {
                return ("Error with start and end time");
            } catch (Exception e) {
                return ("Error with start and end time validation");
            }
        }
    }

    /**
     * Saves event into the database
     * @param event The event object to be saved
     * @return Message based on saving edit or creating event
     */
    public String saveEvent(Event event) throws Exception {
        String message;
        if (isNew) {
            currentEvent = event;
            message = "Successfully Created " + event.getEventName();
            isNew = false;
        } else {
            currentEvent.setEventName(event.getEventName());
            currentEvent.setEndDate(event.getEndDate());
            currentEvent.setStartDate(event.getStartDate());
            currentEvent.setStartTime(event.getStartTime());
            currentEvent.setEndTime(event.getEndTime());
            message = "Successfully Saved " + event.getEventName();
        }
        try {
            eventRepository.save(currentEvent);
            return message;
        } catch (Exception e) {
            throw new Exception("Failure Saving Project");
        }
    }
}


