package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Event;
import nz.ac.canterbury.seng302.portfolio.model.EventRepository;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.ProjectRepository;
import nz.ac.canterbury.seng302.portfolio.utils.IncorrectDetailsException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;

import javax.persistence.PersistenceException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;


/**
    Client service used to communicate to the database
 */
@Service
public class EventService {
    @Autowired private EventRepository eventRepository;
    @Autowired private ProjectRepository projectRepository;
    private Logger logger = LoggerFactory.getLogger(EventService.class);


    public EventService(ProjectRepository projectRepository, EventRepository eventRepository) {
        this.eventRepository = eventRepository;
        this.projectRepository = projectRepository;
    }

    /**
     * Returns an event object from the database. If the event is not present then it throws an exception.
     * @param eventId The unique id (integer) of the requested event.
     * @return Event The event requested with the associated id.
     * @throws IncorrectDetailsException If null value is returned by {@link EventRepository#findById(Object) findById}
     */
    public Event getEvent(int eventId) throws IncorrectDetailsException {
        Event result = eventRepository.findById(eventId);
        if(result != null)
            return result;
        else
            throw new IncorrectDetailsException("Failed to locate the event in the database");

    }

    /**
     * Creates a new event with a name
     * @return of type Event
     */
    public Event getNewEvent(Project project) {
        int currentNumber = eventRepository.findByProject(project).size() + 1;

        LocalDate now = LocalDate.now();
        return new Event.Builder()
                .project(project)
                .eventName("New Event " + currentNumber)
                .startDate(java.sql.Date.valueOf(now))
                .endDate(java.sql.Date.valueOf(now.plusDays(1)))
                .build();
    }

    /**
     * Returns a list of events that are related to the given project ID
     * @param projectId of type int
     * @return a list of events from a project specified by its Id.
     */
    public List<Event> getEventByProjectId(int projectId) {
        Optional<Project> current = projectRepository.findById(projectId);
        return current.map(project -> eventRepository
                    .findByProject(project)
                    .stream()
                    .sorted(Comparator.comparing(Event::getStartDate))
                    .toList())
                .orElse(List.of());
    }

    /**
     * Verifies the event date and time
     * @param event The event object to verify
     * @return Message explaining the error
     * */
    public void verifyEvent(Event event) throws IncorrectDetailsException {

        if (event == null)
            throw new IncorrectDetailsException ("No Event");

        else if (event.getEventName() == null || event.getProject() == null || event.getEndDate() == null || event.getStartDate() == null)
            throw new IncorrectDetailsException ("Event values are null");

        // Removes leading and trailing white spaces from the name
        event.setEventName(event.getEventName().strip());

        if (event.getEventName().length() < 1)
            throw new IncorrectDetailsException ("Event name must not be empty");

        else if (event.getEventName().length() > 50)
             throw new IncorrectDetailsException ("Event name cannot be more than 50 characters");

        else if (event.getEndDate().before(event.getStartDate()))
            throw new IncorrectDetailsException ("The event end date and time cannot be before the event start date and time");

        else if (event.getEndDate().equals(event.getStartDate()))
            throw new IncorrectDetailsException("The event end date and time cannot be the same as event start date and time");

        else if (event.getStartDate().before(event.getProject().getStartDate()))
            throw new IncorrectDetailsException("The event cannot start before the project");

        else if(event.getStartDate().after(event.getProject().getEndDate()) || event.getEndDate().after(event.getProject().getEndDate()))
            throw new IncorrectDetailsException("The event cannot start or end after the project");

        event.setEventName(event.getEventName().strip());
    }

    /**
     * Saves event into the database
     * @param event The event object to be saved
     * @return Message based on saving edit or creating event
     */
    public String saveEvent(Event event) throws IncorrectDetailsException {
        String message;
        if (event.getEventId() == 0) {
            message = "Successfully Created " + event.getEventName();
        } else {
            message = "Successfully Saved " + event.getEventName();
        }
        try {
            eventRepository.save(event);
            return message;
        } catch (PersistenceException e) {
            logger.error("Failure Saving Event {}", event.getEventId(), e);
            throw new IncorrectDetailsException("Failure Saving Event");
        }
    }

    /**
     * Deletes event object from the database
     * @param eventId of type int
     * @return Message of type String
     * @throws IncorrectDetailsException if unable to delete the event
     */
    public String deleteEvent(int eventId) throws IncorrectDetailsException {
        try {
            Event event = eventRepository.findById(eventId);
            eventRepository.deleteById(eventId);
            return "Successfully deleted " + event.getEventName();
        } catch (IllegalArgumentException ex) {
            logger.error("Failure deleting event, id was null", ex);
            throw new IncorrectDetailsException("Could not find an existing event");
        }
    }
}


