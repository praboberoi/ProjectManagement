package nz.ac.canterbury.seng302.portfolio.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Date;
import java.util.Calendar;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Test class for the event class and event repository. This handles CRUD operations.
 */
@DataJpaTest 
@RunWith(SpringRunner.class) 
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class EventRepositoryTest {

    @Autowired 
    private EventRepository eventRepo;
    @Autowired
    private ProjectRepository projectRepository;


    private Event.Builder basicEventBuilder;
    private Project project;

    /**
     * Creates an event builder
     */
    @BeforeEach
    public void init() {
        Project project = new Project.Builder()
                .projectName("Project 2020")
                .description("First Attempt")
                .startDate(new Date(2020, 3, 12))
                .endDate(new Date(2021, 1, 10))
                .build();

        projectRepository.save(project);

        basicEventBuilder = new Event.Builder()
        .eventId(1)
        .project(project)
        .eventName("testEvent")
        .startDate(new Date(Calendar.getInstance().getTimeInMillis()))
        .endDate(new Date(Calendar.getInstance().getTimeInMillis()))
                .startTime("20:00")
                .endTime("21:00");
    }

    @AfterEach
    public void tearDown() {
        projectRepository.delete(project);
    }
    /**
     * Adds an event and checks if it exists by id
     */
    @Test
    public void givenEventExists_FindByEventId() {
        Event testEvent = basicEventBuilder.build();
        eventRepo.save(testEvent);
        assertEquals(testEvent, eventRepo.findById(1).get());
    }

    /**
     * Adds an event and checks if it exists by name
     */
    @Test
    public void givenEventExists_FindByEventName() {
        Event testEvent = basicEventBuilder.build();
        eventRepo.save(testEvent);
        assertEquals(testEvent, eventRepo.findByEventName(testEvent.getEventName()).get(0));
    }
}