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

public class EventServiceTest {

    @Autowired
    private EventRepository eventRepository;
    private EventService eventService = new EventService();
    private  Event.Builder eventBuilder;

    @BeforeEach
    public void init(){
         eventBuilder = new Event.Builder();
    }

    @Test
    public  void givenNoEventIsNull_ThenShowErrorMessage() throws Exception {
        Event emptyEvent = null;
        assertNull(emptyEvent);
        String resultString = eventService.verifyEvent(emptyEvent);
        assertEquals(resultString, "No Event");
    }

    @Test
    public void givenEventWithNullValues_ThenShowNullMsg() throws Exception {
        Event emptyEvent = new Event();
        assertNull(emptyEvent.getEventName());
        String resultString = eventService.verifyEvent(emptyEvent);
        assertEquals(resultString, "Event values are null");
    }

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
