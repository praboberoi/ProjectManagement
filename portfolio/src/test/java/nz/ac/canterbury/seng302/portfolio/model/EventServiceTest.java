package nz.ac.canterbury.seng302.portfolio.model;

import nz.ac.canterbury.seng302.portfolio.service.EventService;
import org.apache.tomcat.jni.Local;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class EventServiceTest {

    private EventRepository eventRepository;
    private EventService eventService;
    private  Event.Builder eventBuilder;

    @BeforeEach
    public void init(){
         eventBuilder = new Event.Builder();
    }

    @Test
    public void givenEventWithNullValues_ThenShowNullMsg() throws Exception {
        Event emptyEvent = new Event();
        assertNull(emptyEvent.getEventName());
        String resultString = eventService.verifyEvent(emptyEvent);
        assertEquals(resultString, "Event values are null");
    }

    @Test
    public void givenEventWithCorrectValues_ThenShowSuccessMsg() throws Exception {
        Event newEvent = eventBuilder.eventId(1)
                .eventName("newEvent")
                .startDate(new Date(Calendar.getInstance().getTimeInMillis()))
                .endDate(new Date(Calendar.getInstance().getTimeInMillis())).build();
        String resultString = eventService.verifyEvent(newEvent);
        assertEquals(resultString, "Event has been verified");
    }

    @Test
    public void givenEventWithIncorrectDates_ThenShowErrorMsg() throws Exception {
        Event newEvent = eventBuilder.eventId(1)
                .eventName("newEvent")
                .startDate(new Date(Calendar.getInstance().getTimeInMillis()))
                .endDate(Date.valueOf(LocalDate.now().minusDays(7))).build();
        String resultString = eventService.verifyEvent(newEvent);
        assertEquals(resultString, "The event end date cannot be before the event start date");
    }
}
