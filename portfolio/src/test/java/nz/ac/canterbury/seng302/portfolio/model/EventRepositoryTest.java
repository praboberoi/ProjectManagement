package nz.ac.canterbury.seng302.portfolio.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

/**
 * Test class for the event class and event repository. This handles CRUD operations.
 */
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext
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
                .projectId(1)
                .projectName("Project 2020")
                .description("First Attempt")
                .startDate(new Date(2020, 3, 12))
                .endDate(new Date(2021, 1, 10))
                .build();

        project = projectRepository.save(project);

        basicEventBuilder = new Event.Builder()
        .eventId(1)
        .project(project)
        .eventName("testEvent")
        .startDate(new Date(2020, 4, 12))
        .endDate(new Date(2020, 5, 12))
                .startTime("20:00")
                .endTime("21:00");
    }

    /**
     * Adds an event and checks if it exists by id
     */
    @Test
    @DirtiesContext
    public void givenEventExists_FindByEventId() {
        Event testEvent = basicEventBuilder.build();
        testEvent = eventRepo.save(testEvent);
        assertEquals(testEvent, eventRepo.findById(1));
    }

    /**
     * Adds an event and checks if it exists by name
     */
    @Test
    @DirtiesContext
    public void givenEventExists_FindByEventName() {
        Event testEvent = basicEventBuilder.build();
        eventRepo.save(testEvent);
        assertEquals(testEvent, eventRepo.findByEventName(testEvent.getEventName()).get(0));
    }
}