package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Event;
import nz.ac.canterbury.seng302.portfolio.model.EventRepository;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.text.SimpleDateFormat;

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
    public Event getNewEvent(Project project) {
        try {
            LocalDate now = LocalDate.now();
            Event newEvent = new Event.Builder()
                    .project(project)
                    .eventName("New Event")
                    .startDate(java.sql.Date.valueOf(now))
                    .endDate(java.sql.Date.valueOf(now.plusDays(1)))
                    .startTime("00:00")
                    .endTime("00:00")
                    .build();
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
    public String verifyEvent(Event event) {
        if (event == null)  {
            return ("No Event");
        } else if (event.getEventName() == null || event.getEndDate() == null || event.getStartDate() == null || event.getStartTime() == null || event.getEndTime() == null) {
            return ("Event values are null");
        } else if (!event.getEventName().matches("^[A-Za-z0-9]+(?: +[A-Za-z0-9]+)*$")) {
            // checks if event name starts or ends with space.
            return ("Event name must not start or end with space characters");
        } else if (event.getEndDate().before(event.getStartDate())){
            return ("The event end date cannot be before the event start date");
        } else {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                Date d1 = (Date) sdf.parse(event.getStartTime());
                Date d2 = (Date) sdf.parse(event.getEndTime());
                // Check if start date is same as end date
                if (event.getStartDate().equals(event.getEndDate())) {
                    // End time before start time or start and end time is the same
                    if (d2.before(d1) || d2.equals(d1)) {
                        return ("The start of the event must occur before the end of the event");
                    }
                }
                return("Event has been verified");
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
        if (event.getEventId() == 0) {
            message = "Successfully Created " + event.getEventName();
        } else {
            message = "Successfully Saved " + event.getEventName();
        }
        try {
            event = eventRepository.save(event);
            return message;
        } catch (Exception e) {
            throw new Exception("Failure Saving Event");
        }
    }
}


