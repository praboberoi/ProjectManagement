package nz.ac.canterbury.seng302.portfolio.model;

import nz.ac.canterbury.seng302.portfolio.service.EventService;
import org.apache.tomcat.jni.Local;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Date;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Test class for the event service class.
 */
public class EventServiceTest {

    @Autowired
    private EventRepository eventRepository;
    private EventService eventService = new EventService();
    private  Event.Builder eventBuilder;

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
    public void givenEventIsNull_ThenShowErrorMessage() throws Exception {
        Event emptyEvent = null;
        assertNull(emptyEvent);
        String resultString = eventService.verifyEvent(emptyEvent);
        assertEquals(resultString, "No Event");
    }

    /**
     * Checks if event form has null values
     */
    @Test
    public void givenEventWithNullValues_ThenShowNullMsg() throws Exception {
        Event emptyEvent = new Event();
        assertNull(emptyEvent.getEventName());
        String resultString = eventService.verifyEvent(emptyEvent);
        assertEquals(resultString, "Event values are null");
    }

    /**
     * Adds an event and checks if event dates are valid
     */
    @Test
    public void givenEventWithIncorrectDates_ThenShowErrorMsg() throws Exception {
        //Event with end date before start date
        Event newEvent = eventBuilder.eventId(1)
                .eventName("newEvent")
                .startDate(new Date(Calendar.getInstance().getTimeInMillis()))
                .endDate(Date.valueOf(LocalDate.now().minusDays(7)))
                .startTime("20:00")
                .endTime("21:00")
                .build();
        String resultString = eventService.verifyEvent(newEvent);
        assertEquals(resultString, "The event end date cannot be before the event start date");
    }

    /**
     * Adds an event and checks for same event date and same event time
     */
    @Test
    public void givenEventWithSameDateAndSameTime_ThenShowErrorMessage() throws Exception {
        Date currentDate = new Date(Calendar.getInstance().getTimeInMillis());
        Event newEvent = eventBuilder.eventId(1)
                .eventName("newEvent")
                .startDate(currentDate)
                .endDate(currentDate)
                .startTime("20:00")
                .endTime("20:00")
                .build();
        String resultString = eventService.verifyEvent(newEvent);
        assertEquals(resultString, "The end time cannot be the same as the start time when event is on the same day");
    }

    /**
     * Adds an event and checks if end time is before start time
     * for events that start and end on the same date
     */
    @Test
    public void givenEventWithSameDateAndEndTimeBeforeStartTime_ThenShowErrorMessage() throws Exception {
        Date currentDate = new Date(Calendar.getInstance().getTimeInMillis());
        Event newEvent = eventBuilder.eventId(1)
                .eventName("newEvent")
                .startDate(currentDate)
                .endDate(currentDate)
                .startTime("20:00")
                .endTime("19:00")
                .build();
        String resultString = eventService.verifyEvent(newEvent);
        assertEquals(resultString, "The end time cannot be before the start time when event is on the same day");
    }

    /**
     * Adds an event and checks if all event from values are valid
     */
    @Test
    public void givenEventWithCorrectValues_ThenShowSuccessMsg() throws Exception {
        Event newEvent = eventBuilder.eventId(1)
                .eventName("newEvent")
                .startDate(new Date(Calendar.getInstance().getTimeInMillis()))
                .endDate(new Date(Calendar.getInstance().getTimeInMillis()))
                .startTime("20:00")
                .endTime("21:00")
                .build();
        String resultString = eventService.verifyEvent(newEvent);
        assertEquals(resultString, "Event has been verified");
    }
}
