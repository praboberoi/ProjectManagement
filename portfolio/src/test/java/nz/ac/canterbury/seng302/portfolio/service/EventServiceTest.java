package nz.ac.canterbury.seng302.portfolio.service;

import io.grpc.Deadline;
import nz.ac.canterbury.seng302.portfolio.model.*;
import nz.ac.canterbury.seng302.portfolio.utils.IncorrectDetailsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.persistence.PersistenceException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

/**
 * Test class for the event service class.
 */

@SpringBootTest
public class EventServiceTest {

    @MockBean
    private EventRepository eventRepository;

    @MockBean
    private EvidenceRepository evidenceRepository;


    @MockBean
    private ProjectRepository projectRepository;

    private EventService eventService;
    private Event.Builder eventBuilder;
    private Project project;

    /**
     * Creates an event builder
     */
    @BeforeEach
    public void init(){

        eventBuilder = new Event.Builder();
        eventService = new EventService(projectRepository, eventRepository);
    }

    /**
     * Tests to make sure an error is thrown and an appropriate error message is received when a null object is
     * sent for verifying an event
     */
    @Test
    public void givenEventIsNull_WhenVerifyEvent_ThenExceptionIsThrown() {
        IncorrectDetailsException exception = assertThrows(IncorrectDetailsException.class,() ->
                eventService.verifyEvent(null) );

        assertEquals("No Event", exception.getMessage());
    }

    /**
     * Tests to make sure an error is thrown and an appropriate error message is received when event values are null
     * for verifying an event
     */
    @Test
    public void givenEventWithNullValues_WhenVerifyEvent_ThenExceptionIsThrown() {
        IncorrectDetailsException exception = assertThrows(IncorrectDetailsException.class,() ->
                eventService.verifyEvent(new Event()) );

        assertEquals("Event values are null", exception.getMessage());
    }

    /**
     * Tests to make sure an error is thrown and an appropriate error message is received when the end date
     * is before the start date
     */
    @Test
    public void givenEventWithIncorrectDates_WhenVerifyEvent_ThenExceptionIsThrown() {
        //Event with end date before start date
        Event newEvent = eventBuilder.eventId(1)
                .eventName("newEvent")
                .project(new Project())
                .startDate(new Date(Calendar.getInstance().getTimeInMillis()))
                .endDate(Date.valueOf(LocalDate.now().minusDays(7)))
                .build();
        IncorrectDetailsException exception = assertThrows(IncorrectDetailsException.class,() ->
                eventService.verifyEvent(newEvent) );

        assertEquals("The event end date and time cannot be before the event start date and time", exception.getMessage());
    }

    /**
     * Tests to make sure an error is thrown and an appropriate error message is received when the event start date
     * is the same as the end date
     */
    @Test
    public void givenEventWithSameDateAndSameTime_WhenVerifyEvent_ThenShowErrorMessage() {
        Date currentDate = new Date(Calendar.getInstance().getTimeInMillis());
        Event newEvent = eventBuilder.eventId(1)
                .eventName("newEvent")
                .startDate(currentDate)
                .project(new Project())

                .endDate(currentDate)
                .build();
        IncorrectDetailsException exception = assertThrows(IncorrectDetailsException.class,() ->
                eventService.verifyEvent(newEvent) );

        assertEquals("The event end date and time cannot be the same as event start date and time", exception.getMessage());
    }

    /**
     * Tests to make sure an error is thrown and an appropriate error message is received when the event start
     * date and time is the same as the end date and time
     */
    @Test
    public void givenEventWithSameDateAndEndTimeBeforeStartTime_WhenVerifyEvent_ThenShowErrorMessage() {
        Date currentDate = new Date(Calendar.getInstance().getTimeInMillis());
        Event newEvent = eventBuilder.eventId(1)
                .eventName("newEvent")
                .startDate(currentDate)
                .project(new Project())
                .endDate(currentDate)
                .build();
        IncorrectDetailsException exception = assertThrows(IncorrectDetailsException.class,() ->
                eventService.verifyEvent(newEvent) );

        assertEquals("The event end date and time cannot be the same as event start date and time", exception.getMessage());
    }

    /**
     * Tests to make sure no error is thrown when the event is verified with correct values
     */
    @Test
    public void givenEventWithCorrectValues_WhenVerifyEvent_ThenNoExceptionIsThrown() {
        project = new Project.Builder()
                .description("This is a test project")
                .startDate(new java.sql.Date(2020 - 1900, 11, 12))
                .endDate(new java.sql.Date(2023 - 1900, 1, 10))
                .projectName("Project 2020")
                .projectId(1)
                .build();

        Event newEvent = eventBuilder.eventId(1)
                .eventName("newEvent")
                .project(project)
                .startDate(new Date(Calendar.getInstance().getTimeInMillis()))
                .endDate(Date.valueOf(LocalDate.now().plusDays(7)))
                .build();
        
        assertDoesNotThrow(() -> eventService.verifyEvent(newEvent));
    }

    /**
     * Tests to make sure an error is thrown and an appropriate error message is received when an event
     * has no name
     */
    @Test
    public void givenEventWithEmptyName_WhenVerifyEvent_ThenExceptionIsThrown() {
        Event newEvent = eventBuilder.eventId(1)
                .eventName("")
                .project(new Project())
                .startDate(new Date(Calendar.getInstance().getTimeInMillis()))
                .endDate(new Date(Calendar.getInstance().getTimeInMillis()))
                .build();
        IncorrectDetailsException exception = assertThrows(IncorrectDetailsException.class,() ->
                eventService.verifyEvent(newEvent) );
        
        assertEquals("Event name must not be empty", exception.getMessage());
    }

    /**
     * Tests to make sure an error is thrown and an appropriate error message is received when the event name
     * is over the character limit
     */
    @Test
    public void givenEventWithNameOverLimit_WhenVerifyEvent_ThenExceptionIsThrown() {
        Event newEvent = eventBuilder.eventId(1)
                .eventName("This is a event name thats more than 50 characters long")
                .project(new Project())
                .startDate(new Date(Calendar.getInstance().getTimeInMillis()))
                .endDate(new Date(Calendar.getInstance().getTimeInMillis()))
                .build();

        IncorrectDetailsException exception = assertThrows(IncorrectDetailsException.class,() ->
                eventService.verifyEvent(newEvent) );

        assertEquals("Event name cannot be more than 50 characters", exception.getMessage());
    }

    /**
     * Tests to make sure an error is thrown and an appropriate error message is received the event start date
     * is after the end date
     */
    @Test
    public void givenEventWithStartDateAfterEndDate_WhenVerifyEvent_ThenExceptionIsThrown(){
        Event newEvent = eventBuilder.eventId(1)
                .eventName(" Event name with whitespace start")
                .project(new Project())
                .startDate(Date.valueOf(LocalDate.now().plusDays(7)))
                .endDate(new Date(Calendar.getInstance().getTimeInMillis()))
                .build();
        IncorrectDetailsException exception = assertThrows(IncorrectDetailsException.class,() ->
                eventService.verifyEvent(newEvent) );

        assertEquals("The event end date and time cannot be before the event start date and time", exception.getMessage());

    }

    /**
     * Tests to make an event is successfully created
     */
    @Test
    public void givenNewEventCreated_whenSaveEvent_thenSuccessfullyCreatedMsgDisplayed() {
        Event newEvent = eventBuilder.eventId(0)
                .eventName("New Event")
                .project(new Project())
                .startDate(new Date(Calendar.getInstance().getTimeInMillis()))
                .endDate(Date.valueOf(LocalDate.now().plusDays(7)))
                .build();
        try{
            assertEquals("Successfully Created " + newEvent.getEventName(), eventService.saveEvent(newEvent));
        } catch (IncorrectDetailsException e){
            e.printStackTrace();
        }
    }

    /**
     * Tests to make an event is successfully updated
     */
    @Test
    public void givenExistingEvent_whenSaveEvent_thenSuccessfullyUpdatedMsgDisplayed() {
        Event newEvent = eventBuilder.eventId(1)
                .eventName("New Event")
                .project(new Project())
                .startDate(new Date(Calendar.getInstance().getTimeInMillis()))
                .endDate(Date.valueOf(LocalDate.now().plusDays(7)))
                .build();
        newEvent.setEventName("Update with new name");
        try{
            assertEquals("Successfully Saved " + newEvent.getEventName(), eventService.saveEvent(newEvent));
        } catch (IncorrectDetailsException e){
            e.printStackTrace();
        }
    }

    /**
     * Tests to make sure a new event is returned with appropriate attributes when a new event
     * is requested
     */
    @Test
    public void givenEventServiceExist_whenGetNewEventRequested_thenNewEventFormDisplayed(){
        when(eventRepository.findByProject(project)).thenReturn(List.of());
        LocalDate now = LocalDate.now();

        project = new Project.Builder()
                .description("This is a test project")
                .startDate(new java.sql.Date(2020 - 1900, 3, 12))
                .endDate(new java.sql.Date(2023 - 1900, 1, 10))
                .projectName("Project 2020")
                .projectId(1)
                .build();


        Event newEvent = eventService.getNewEvent(project);
        assertInstanceOf(Event.class, newEvent);
        assertEquals(0, newEvent.getEventId());
        assertEquals(java.sql.Date.valueOf(now), newEvent.getStartDate());
        assertEquals(java.sql.Date.valueOf(now.plusDays(1)), newEvent.getEndDate());
        assertEquals("New Event 1", newEvent.getEventName());
        assertEquals(project, newEvent.getProject());

    }


    /**
     * Tests to make sure an error is thrown and an appropriate error message is received the event starts
     * or ends before the project start date
     */
    @Test
    public void givenProject_whenEventStartsBeforeProject_ThenExceptionIsThrown() {
        project = new Project.Builder()
                .description("This is a test project")
                .startDate(new java.sql.Date(2022, 11, 12))
                .endDate(new java.sql.Date(2023, 1, 10))
                .projectName("Project 2020")
                .projectId(1)
                .build();

        Event newEvent = eventBuilder.eventId(1)
                .eventName("Event name")
                .project(project)
                .startDate(Date.valueOf(LocalDate.now().plusDays(2)))
                .endDate(Date.valueOf(LocalDate.now().plusDays(5)))
                .build();
        IncorrectDetailsException exception = assertThrows(IncorrectDetailsException.class,() ->
                eventService.verifyEvent(newEvent) );

        assertEquals("The event cannot start before the project", exception.getMessage());

    }

    /**
     * Tests to make sure an error is thrown and an appropriate error message is received the event starts
     * or ends after the project end date
     */
    @Test
    public void givenProject_whenEventStartsOrEndsAfterProject_ThenExceptionIsThrown() {
        project = new Project.Builder()
                .description("This is a test project")
                .startDate(new java.sql.Date(2022, 11, 12))
                .endDate(new java.sql.Date(2023, 1, 10))
                .projectName("Project 2020")
                .projectId(1)
                .build();

        Event newEvent = eventBuilder.eventId(1)
                .eventName("Event name")
                .project(project)
                .startDate(new java.sql.Date(2022, 12, 12))
                .endDate(new java.sql.Date(2023, 3, 12))
                .build();
        Event newEvent2 = eventBuilder.eventId(1)
                .eventName("Event name")
                .project(project)
                .startDate(new java.sql.Date(2023, 4, 12))
                .endDate(new java.sql.Date(2023, 5, 12))
                .build();
        IncorrectDetailsException exception = assertThrows(IncorrectDetailsException.class,() ->
                eventService.verifyEvent(newEvent) );
        assertEquals("The event cannot start or end after the project", exception.getMessage());
        IncorrectDetailsException exception2 = assertThrows(IncorrectDetailsException.class,() ->
                eventService.verifyEvent(newEvent2) );
        assertEquals("The event cannot start or end after the project", exception2.getMessage());

    }

    /**
     * Tests to make sure an error is thrown and an appropriate error message is received when event
     * values are null
     */
    @Test
    public void givenEventWithEmptyValues_WhenVerifyEvent_ThenExceptionIsThrown() {
        Event newEvent = eventBuilder.eventId(1)
                .eventName("Event name")
                .project(project)
                .build();
        Event newEvent2 = eventBuilder.eventId(1)
                .eventName("Event name")
                .startDate(new java.sql.Date(2023, 4, 12))
                .endDate(new java.sql.Date(2023, 5, 12))
                .build();
        IncorrectDetailsException exception = assertThrows(IncorrectDetailsException.class,() ->
                eventService.verifyEvent(newEvent) );
        assertEquals("Event values are null", exception.getMessage());
        IncorrectDetailsException exception2 = assertThrows(IncorrectDetailsException.class,() ->
                eventService.verifyEvent(newEvent2) );
        assertEquals("Event values are null", exception2.getMessage());
    }

    /**
     * Tests to make sure an error is thrown and an appropriate error message is received failure to
     * save event
     */
    @Test
    public void givenExistingEvent_whenFailureToSaveEvent_thenExceptionThrown() {
        Event newEvent = eventBuilder.eventId(1)
                .eventName("New Event")
                .project(new Project())
                .startDate(new Date(Calendar.getInstance().getTimeInMillis()))
                .endDate(Date.valueOf(LocalDate.now().plusDays(7)))
                .build();

        when(eventRepository.save(newEvent)).thenThrow(PersistenceException.class);
        IncorrectDetailsException exception = assertThrows(IncorrectDetailsException.class, () ->
                eventService.saveEvent(newEvent));
        assertEquals("Failure Saving Event", exception.getMessage());
    }

    /**
     * Tests to make sure event is returned when get valid event.
     */
    @Test
    public void givenExistingEvent_whenGetEventWithId_thenReturnEvent() throws IncorrectDetailsException {
        Event event = eventBuilder.eventId(1)
                .eventName("New Event")
                .project(new Project())
                .startDate(new Date(Calendar.getInstance().getTimeInMillis()))
                .endDate(Date.valueOf(LocalDate.now().plusDays(7)))
                .build();

        when(eventRepository.findById(1)).thenReturn(event);
        Event returnedEvent = eventService.getEvent(1);
        assertTrue(event.equals(returnedEvent));
    }

    /**
     * Tests to make sure an error is thrown when calling getEvent on a non-existent event.
     */
    @Test
    public void givenNotExistingEvent_whenGetEvent_thenExceptionThrown() {
        when(eventRepository.findById(0)).thenReturn(null);
        IncorrectDetailsException exception = assertThrows(IncorrectDetailsException.class, () ->
                eventService.getEvent(0));
        assertEquals("Failed to locate the event in the database", exception.getMessage());
    }

    /**
     * Tests to make sure a list of sorted events is returned when calling getEventByProjectId.
     */
    @Test
    public void givenProjectWithEvents_whenGetEventByProjectId_thenSortedEventsReturned() {

        List<Event> listEvents = new ArrayList<Event>();
        Event event1 = eventBuilder.eventId(1)
                .eventName("New Event 1")
                .project(new Project())
                .startDate(Date.valueOf(LocalDate.now().plusDays(1)))
                .endDate(Date.valueOf(LocalDate.now().plusDays(7)))
                .build();
        Event event2 = eventBuilder.eventId(1)
                .eventName("New Event 2")
                .project(new Project())
                .startDate(Date.valueOf(LocalDate.now().plusDays(2)))
                .endDate(Date.valueOf(LocalDate.now().plusDays(7)))
                .build();
        Event event3 = eventBuilder.eventId(1)
                .eventName("New Event 3")
                .project(new Project())
                .startDate(Date.valueOf(LocalDate.now().plusDays(3)))
                .endDate(Date.valueOf(LocalDate.now().plusDays(7)))
                .build();

        listEvents.add(event2);
        listEvents.add(event3);
        listEvents.add(event1);

        project = new Project.Builder()
                .description("This is a test project")
                .startDate(new java.sql.Date(2020 - 1900, 11, 12))
                .endDate(new java.sql.Date(2023 - 1900, 1, 10))
                .projectName("Project 2020")
                .projectId(1)
                .build();

        Optional<Project> projectOptional = Optional.of(project);

        when(projectRepository.findById(anyInt())).thenReturn(projectOptional);
        when(eventRepository.findByProject(project)).thenReturn(listEvents);
        List<Event> returnedList = eventService.getEventByProjectId(1);
        assertTrue(returnedList.get(0).equals(event1));
        assertTrue(returnedList.get(1).equals(event2));
        assertTrue(returnedList.get(2).equals(event3));
    }
    /**
     * Asserts that an event is properly deleted when deleteEvent is called
     * @throws IncorrectDetailsException
     */
    @Test
    public void givenEventExists_whenDeleteEventCalled_thenEventSuccessfullyDeleted() throws IncorrectDetailsException {
        Event newEvent = eventBuilder.eventId(99)
                .eventName("New Event")
                .project(new Project())
                .startDate(new Date(Calendar.getInstance().getTimeInMillis()))
                .endDate(Date.valueOf(LocalDate.now().plusDays(7)))
                .build();

        when(eventRepository.findById(99)).thenReturn(newEvent);
        assertEquals("Successfully deleted " + newEvent.getEventName(), eventService.deleteEvent(99));

    }

    /**
     * Asserts that the correct error message is thrown when attempting to delete a non-existent event
     */
    @Test
    public void givenEventDoesntExists_whenDeleteEventCalled_thenEventErrorIsThrown(){
        Event newEvent = eventBuilder.eventId(99)
                .eventName("New Event")
                .project(new Project())
                .startDate(new Date(Calendar.getInstance().getTimeInMillis()))
                .endDate(Date.valueOf(LocalDate.now().plusDays(7)))
                .build();

        when(eventRepository.findById(99)).thenThrow(IllegalArgumentException.class);
        IncorrectDetailsException exception = assertThrows(IncorrectDetailsException.class, () ->
                eventService.deleteEvent(99));
        assertEquals("Could not find an existing event", exception.getMessage() );

    }


}