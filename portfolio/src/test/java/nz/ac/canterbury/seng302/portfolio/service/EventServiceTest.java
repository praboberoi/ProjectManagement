package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Event;
import nz.ac.canterbury.seng302.portfolio.model.EventRepository;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the event service class.
 */

@SpringBootTest
public class EventServiceTest {
    private EventService eventService = new EventService();
    private Event.Builder eventBuilder;

    /**
     * Creates an event builder
     */
    @BeforeEach
    public void init(){
         eventBuilder = new Event.Builder();
    }

    /**
     * Checks if event is null
     */
    @Test
    public void givenEventIsNull_WhenVerifyEvent_ThenExceptionIsThrown() {
        IncorrectDetailsException exception = assertThrows(IncorrectDetailsException.class,() ->
                eventService.verifyEvent(null) );

        assertEquals("No Event", exception.getMessage());
    }

    /**
     * Checks if event form has null values
     */
    @Test
    public void givenEventWithNullValues_WhenVerifyEvent_ThenExceptionIsThrown() {
        IncorrectDetailsException exception = assertThrows(IncorrectDetailsException.class,() ->
                eventService.verifyEvent(new Event()) );

        assertEquals("Event values are null", exception.getMessage());
    }

    /**
     * Adds an event and checks if event dates are valid
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
     * Adds an event and checks for same event date and same event time
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
     * Adds an event and checks if end time is before start time
     * for events that start and end on the same date
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
     * Adds an event and checks if all event form values are valid
     */
    @Test
    public void givenEventWithCorrectValues_WhenVerifyEvent_ThenNoExceptionIsThrown() {
        Event newEvent = eventBuilder.eventId(1)
                .eventName("newEvent")
                .project(new Project())
                .startDate(new Date(Calendar.getInstance().getTimeInMillis()))
                .endDate(Date.valueOf(LocalDate.now().plusDays(7)))
                .build();
        
        assertDoesNotThrow(() -> eventService.verifyEvent(newEvent));
    }

    /**
     * Adds an event and checks if event name is empty.
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
     * Adds an event and checks if event name exceeds 50 characters.
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
     * Adds an event and checks if event name matches the regex format.
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
}
