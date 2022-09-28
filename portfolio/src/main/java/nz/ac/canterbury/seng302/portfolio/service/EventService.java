package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.*;
import nz.ac.canterbury.seng302.portfolio.utils.IncorrectDetailsException;
import nz.ac.canterbury.seng302.portfolio.utils.SprintColor;
import nz.ac.canterbury.seng302.portfolio.utils.ValidationUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.PersistenceException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.*;

/**
 * Client service used to communicate to the database
 */
@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private SprintRepository sprintRepository;
    private Logger logger = LoggerFactory.getLogger(EventService.class);

    public EventService(ProjectRepository projectRepository, EventRepository eventRepository,
            SprintRepository sprintRepository) {
        this.eventRepository = eventRepository;
        this.projectRepository = projectRepository;
        this.sprintRepository = sprintRepository;
    }

    /**
     * Returns an event object from the database. If the event is not present then
     * it throws an exception.
     *
     * @param eventId The unique id (integer) of the requested event.
     * @return Event The event requested with the associated id.
     * @throws IncorrectDetailsException If null value is returned by
     *                                   {@link EventRepository#findById(Object)
     *                                   findById}
     */
    public Event getEvent(int eventId) throws IncorrectDetailsException {
        Event result = eventRepository.findById(eventId);
        if (result != null)
            return result;
        else
            throw new IncorrectDetailsException("Failed to locate the event in the database");

    }

    /**
     * Creates a new event with a name
     *
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
     * This method maps the id's of events to the names of sprints that occur at the
     * start and end of the event
     *
     * @param eventList The list of events to have sprints mapped to them
     * @return Hashtable containing a mapping between the id of an event to a List
     *         containing the sprint occurring at
     *         the start of the event and then the sprint that occurs at the end of
     *         an event.
     */
    public Map<Integer, List<String>> getSprintLabelsForStartAndEndDates(List<Event> eventList) {
        HashMap<Integer, List<String>> eventDateMappingDictionary = new HashMap<>();
        for (Event event : eventList) {
            List<String> sprintNames = new ArrayList<String>();
            Date startDate = new Date(event.getStartDate().getTime());
            Date endDate = new Date(event.getEndDate().getTime());
            Sprint start = sprintRepository.findByDateAndProject(event.getProject(), startDate);
            Sprint end = sprintRepository.findByDateAndProject(event.getProject(), endDate);
            if (start == null) {
                sprintNames.add("(No Sprint)");
            } else {
                sprintNames.add("(" + start.getSprintName() + ")");
            }
            if (end == null) {
                sprintNames.add("(No Sprint)");
            } else {
                sprintNames.add("(" + end.getSprintName() + ")");
            }
            eventDateMappingDictionary.put(event.getEventId(), sprintNames);
        }
        return eventDateMappingDictionary;
    }

    /**
     * Returns a list of events that are related to the given project ID
     *
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
     *
     * @param event The event object to verify
     * @throws IncorrectDetailsException Message explaining the error
     */
    public void verifyEvent(Event event) throws IncorrectDetailsException {

        if (event == null)
            throw new IncorrectDetailsException("No Event");

        else if (event.getEventName() == null || event.getProject() == null || event.getEndDate() == null || event.getStartDate() == null)
            throw new IncorrectDetailsException("Event values are null");


        // Removes leading and trailing white spaces from the name
        event.setEventName(event.getEventName().strip());

        if (ValidationUtilities.hasEmoji(event.getEventName()))
            throw new IncorrectDetailsException("Event name must not contain an emoji");

        else if (event.getEventName().length() < 1)
            throw new IncorrectDetailsException("Event name must not be empty");

        else if (event.getEventName().length() > 50)
            throw new IncorrectDetailsException("Event name cannot be more than 50 characters");

        else if (event.getEndDate().before(event.getStartDate()))
            throw new IncorrectDetailsException("The event end date and time cannot be before the event start date and time");

        else if (event.getEndDate().equals(event.getStartDate()))
            throw new IncorrectDetailsException("The event end date and time cannot be the same as event start date and time");

        else if (event.getStartDate().before(event.getProject().getStartDate()))
            throw new IncorrectDetailsException("The event cannot start before the project");

        else if (event.getStartDate().after(event.getProject().getEndDate()) || event.getEndDate().after(event.getProject().getEndDate()))
            throw new IncorrectDetailsException("The event cannot start or end after the project");
    }

    /**
     * Updates the colours for the given event
     *
     * @param event of type Event
     */
    public void updateEventColors(Event event) {
        List<Sprint> sprintList = sprintRepository.findSprintsByEvent(event)
                .stream().sorted((sprint1, sprint2) -> sprint1.getEndDate().before(sprint2.getStartDate()) ? 1 : 0)
                .toList();
        ArrayList<SprintColor> eventColours = new ArrayList<>();
        sprintList.forEach(sprint -> {
            if (!eventColours.contains(sprint.getColor())) {
                eventColours.add(sprint.getColor());
            }
        });

        if (!sprintList.isEmpty()) {
            if (sprintList.get(0).getStartDate().after(event.getStartDate()))
                eventColours.add(0, SprintColor.WHITE);

            if (sprintList.get(sprintList.size() - 1).getEndDate().before(event.getEndDate()))
                eventColours.add(SprintColor.WHITE);
        }
        event.setColors(eventColours);
    }

    /**
     * Saves event into the database
     *
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
     * Returns a list of deadlines that occur within the given sprint related to the
     * sprint ID.
     *
     * @param sprintId The id of the sprint (int).
     * @return A list of deadlines from a sprint specified by its id.
     */
    public List<Event> getEventsBySprintId(int sprintId) {
        Optional<Sprint> current = sprintRepository.findById(sprintId);
        return current.map(sprint -> eventRepository.findEventsBySprint(sprint).stream()
                .sorted(Comparator.comparing(Event::getStartDate)).toList()).orElse(List.of());
    }

    /**
     * Deletes event object from the database
     *
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
