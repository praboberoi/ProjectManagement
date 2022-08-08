package nz.ac.canterbury.seng302.portfolio.service;

import io.grpc.Deadline;
import nz.ac.canterbury.seng302.portfolio.model.Event;
import nz.ac.canterbury.seng302.portfolio.model.EventRepository;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Test class for the event service class.
 */

@SpringBootTest
public class EventServiceTest {

    @MockBean
    private EventRepository eventRepository;

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
    public void givenEventWithStartDateAfterEndDate_WhenVerifyEvent_ThenExceptionIsThrown() throws Exception {
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



}
