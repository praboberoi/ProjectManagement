package nz.ac.canterbury.seng302.portfolio.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Date;
import java.util.Calendar;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;

@DataJpaTest 
@RunWith(SpringRunner.class) 
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class EventRepositoryTest {

    @Autowired 
    private EventRepository eventRepo;

    private static Event.Builder basicEventBuilder;

    @BeforeClass
    public static void init() {
        basicEventBuilder = new Event.Builder()
        .eventId(1)
        .eventName("testEvent")
        .startDate(new Date(Calendar.getInstance().getTimeInMillis()))
        .endDate(new Date(Calendar.getInstance().getTimeInMillis()));
    }

    @Test
    public void givenEventExists_FindByEventId() {
        Event testEvent = basicEventBuilder.build();
        eventRepo.save(testEvent);
        assertEquals(testEvent, eventRepo.findById(1).get());
    }

    @Test
    public void givenEventExists_FindByEventName() {
        Event testEvent = basicEventBuilder.build();
        eventRepo.save(testEvent);
        assertEquals(testEvent, eventRepo.findByEventName(testEvent.getEventName()).get(0));
    }
}