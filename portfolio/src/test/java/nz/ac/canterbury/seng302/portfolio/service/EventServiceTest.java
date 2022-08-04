package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Event;
import nz.ac.canterbury.seng302.portfolio.model.EventRepository;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Calendar;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Test class for the event service class.
 */
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
    public void givenEventIsNull_WhenVerifyEvent_ThenShowErrorMessage() throws Exception {
        Event emptyEvent = null;
        String resultString = eventService.verifyEvent(emptyEvent);
        assertEquals("No Event", resultString);
    }

    /**
     * Checks if event form has null values
     */
    @Test
    public void givenEventWithNullValues_WhenVerifyEvent_ThenShowNullMsg() throws Exception {
        Event emptyEvent = new Event();
        assertNull(emptyEvent.getEventName());
        String resultString = eventService.verifyEvent(emptyEvent);
        assertEquals("Event values are null", resultString);
    }

    /**
     * Adds an event and checks if event dates are valid
     */
    @Test
    public void givenEventWithIncorrectDates_WhenVerifyEvent_ThenShowErrorMsg() throws Exception {
        //Event with end date before start date
        Event newEvent = eventBuilder.eventId(1)
                .eventName("newEvent")
                .project(new Project())
                .startDate(new Date(Calendar.getInstance().getTimeInMillis()))
                .endDate(Date.valueOf(LocalDate.now().minusDays(7)))
                .startTime("20:00")
                .endTime("21:00")
                .build();
        String resultString = eventService.verifyEvent(newEvent);
        assertEquals("The event end date cannot be before the event start date", resultString);
    }

    /**
     * Adds an event and checks for same event date and same event time
     */
    @Test
    public void givenEventWithSameDateAndSameTime_WhenVerifyEvent_ThenShowErrorMessage() throws Exception {
        Date currentDate = new Date(Calendar.getInstance().getTimeInMillis());
        Event newEvent = eventBuilder.eventId(1)
                .eventName("newEvent")
                .startDate(currentDate)
                .project(new Project())

                .endDate(currentDate)
                .startTime("20:00")
                .endTime("20:00")
                .build();
        String resultString = eventService.verifyEvent(newEvent);
        assertEquals("The start of the event must occur before the end of the event", resultString);
    }

    /**
     * Adds an event and checks if end time is before start time
     * for events that start and end on the same date
     */
    @Test
    public void givenEventWithSameDateAndEndTimeBeforeStartTime_WhenVerifyEvent_ThenShowErrorMessage() throws Exception {
        Date currentDate = new Date(Calendar.getInstance().getTimeInMillis());
        Event newEvent = eventBuilder.eventId(1)
                .eventName("newEvent")
                .startDate(currentDate)
                .project(new Project())

                .endDate(currentDate)
                .startTime("20:00")
                .endTime("19:00")
                .build();
        String resultString = eventService.verifyEvent(newEvent);
        assertEquals("The start of the event must occur before the end of the event", resultString);
    }

    /**
     * Adds an event and checks if all event form values are valid
     */
    @Test
    public void givenEventWithCorrectValues_WhenVerifyEvent_ThenShowSuccessMsg() throws Exception {
        Event newEvent = eventBuilder.eventId(1)
                .eventName("newEvent")
                .project(new Project())
                .startDate(new Date(Calendar.getInstance().getTimeInMillis()))
                .endDate(new Date(Calendar.getInstance().getTimeInMillis()))
                .startTime("20:00")
                .endTime("21:00")
                .build();
        String resultString = eventService.verifyEvent(newEvent);
        assertEquals("Event has been verified", resultString);
    }

    /**
     * Adds an event and checks if event name is empty.
     */
    @Test
    public void givenEventWithEmptyName_WhenVerifyEvent_ThenShowErrorMsg() throws Exception {
        Event newEvent = eventBuilder.eventId(1)
                .eventName("")
                .project(new Project())
                .startDate(new Date(Calendar.getInstance().getTimeInMillis()))
                .endDate(new Date(Calendar.getInstance().getTimeInMillis()))
                .startTime("20:00")
                .endTime("21:00")
                .build();
        String resultString = eventService.verifyEvent(newEvent);
        assertEquals("Event name must not be empty", resultString);
    }

    /**
     * Adds an event and checks if event name exceeds 50 characters.
     */
    @Test
    public void givenEventWithNameOverLimit_WhenVerifyEvent_ThenShowErrorMsg() throws Exception {
        Event newEvent = eventBuilder.eventId(1)
                .eventName("This is a event name thats more than 50 characters long")
                .project(new Project())
                .startDate(new Date(Calendar.getInstance().getTimeInMillis()))
                .endDate(new Date(Calendar.getInstance().getTimeInMillis()))
                .startTime("20:00")
                .endTime("21:00")
                .build();
        String resultString = eventService.verifyEvent(newEvent);
        assertEquals("Event name cannot be more than 50 characters", resultString);
    }

    /**
     * Adds an event and checks if event name matches the regex format.
     */
    @Test
    public void givenEventWithIncorrectNameFormat_WhenVerifyEvent_ThenShowErrorMsg() throws Exception {
        Event newEvent = eventBuilder.eventId(1)
                .eventName(" Event name with whitespace start")
                .project(new Project())
                .startDate(new Date(Calendar.getInstance().getTimeInMillis()))
                .endDate(new Date(Calendar.getInstance().getTimeInMillis()))
                .startTime("20:00")
                .endTime("21:00")
                .build();
        String resultString = eventService.verifyEvent(newEvent);
        assertEquals("Event name must not start or end with space characters", resultString);
    }
}
