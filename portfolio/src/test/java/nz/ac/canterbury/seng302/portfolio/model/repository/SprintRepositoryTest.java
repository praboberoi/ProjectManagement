package nz.ac.canterbury.seng302.portfolio.model.repository;

import nz.ac.canterbury.seng302.portfolio.model.Event;
import nz.ac.canterbury.seng302.portfolio.model.EvidenceRepository;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.ProjectRepository;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.model.SprintRepository;
import nz.ac.canterbury.seng302.portfolio.utils.SprintColor;
import nz.ac.canterbury.seng302.portfolio.model.Event;
import nz.ac.canterbury.seng302.portfolio.model.EvidenceRepository;
import nz.ac.canterbury.seng302.portfolio.model.SprintRepository;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.model.ProjectRepository;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Date;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class SprintRepositoryTest {
    @Autowired 
    private SprintRepository sprintRepository;

    @MockBean
    private EvidenceRepository evidenceRepository;
    @Autowired
    private ProjectRepository projectRepository;
    private Sprint sprint1;
    private Sprint sprint2;
    private Project project;

    @BeforeEach
    public void setUp() {
        project = new Project.Builder()
                .projectName("Project 2020")
                .description("First Attempt")
                .startDate(new Date(2020, 3, 12))
                .endDate(new Date(2021, 1, 10))
                .build();

        sprint1 = new Sprint.Builder()
                .sprintLabel("Sprint 1")
                .sprintName("Sprint 1")
                .description("Attempt 1")
                .project(project)
                .startDate(new Date(2021,1,1))
                .endDate(new Date(2021, 3, 1))
                .color(SprintColor.BLUE)
                .build();

        sprint2 = new Sprint.Builder()
                .sprintLabel("Sprint 2")
                .sprintName("Sprint 2")
                .description("Attempt 2")
                .project(project)
                .startDate(new Date(2021,3,1))
                .endDate(new Date(2021, 6, 1))
                .color(SprintColor.SYYBLUE)
                .build();
        projectRepository.save(project);
        sprintRepository.save(sprint1);
        sprintRepository.save(sprint2);
    }

    @AfterEach
    public void tearDown() {
        sprintRepository.delete(sprint1);
        sprintRepository.delete(sprint2);
        projectRepository.delete(project);
    }

    @Test
    void givenSprintExists_FindBySprintName() {
        List<Sprint> sprintList = Arrays.asList(sprint1);
        assertArrayEquals(sprintList.toArray(), sprintRepository.findBySprintName("Sprint 1").toArray());

        List<Sprint> emptyList = Collections.<Sprint>emptyList();
        assertArrayEquals(emptyList.toArray(), sprintRepository.findBySprintName("No").toArray());

    }

    /**
     * Given a date and project this test asserts that the repository can find the corresponding sprint.
     */
    @Test
    void givenSprintExists_FindByProjectAndDate() {
        Date date = new Date(2021, 1, 2);
        assertEquals(sprint1, sprintRepository.findByDateAndProject(project, date));
    }

    /**
     * Given a date not relating to a sprint, this test asserts that the repository returns null.
     */
    @Test
    void givenSprintDoesntExist_FindByProjectAndDateReturnsNull() {
        Date date = new Date(1999, 1, 2);
        assertEquals(null, sprintRepository.findByDateAndProject(project, date));
    }

    @Test
    void givenSprintExists_FindBySprintNameContaining() {
        List<Sprint> sprintList = Arrays.asList(sprint1, sprint2);
        assertArrayEquals(sprintList.toArray(),sprintRepository.findBySprintNameContaining("Sprint").toArray());

        List<Sprint> emptyList = Collections.<Sprint>emptyList();
        assertArrayEquals(emptyList.toArray(), sprintRepository.findBySprintNameContaining("No").toArray());
    }

    @Test
    void givenSprintExists_FindByDescription() {
        List<Sprint> sprintList = Arrays.asList(sprint1);
        assertArrayEquals(sprintList.toArray(), sprintRepository.findByDescription("Attempt 1").toArray());

        List<Sprint> emptyList = Collections.<Sprint>emptyList();
        assertArrayEquals(emptyList.toArray(), sprintRepository.findByDescription("No").toArray());
    }

    @Test
    void givenSprintExists_FindByDescriptionContaining() {
        List<Sprint> sprintList = Arrays.asList(sprint1, sprint2);
        assertArrayEquals(sprintList.toArray(), sprintRepository.findByDescriptionContaining("Attempt").toArray());

        List<Sprint> emptyList = Collections.<Sprint>emptyList();
        assertArrayEquals(emptyList.toArray(), sprintRepository.findByDescriptionContaining("No").toArray());
    }

    @Test
    void givenSprintExists_FindByStartDate() {
        List<Sprint> sprintList = Arrays.asList(sprint1);
        assertArrayEquals(sprintList.toArray(), sprintRepository.findByStartDate(new Date(2021,1,1)).toArray());
    }

    @Test
    void givenSprintExists_FindByEndDate() {
        List<Sprint> sprintList = Arrays.asList(sprint1);
        assertArrayEquals(sprintList.toArray(), sprintRepository.findByEndDate(new Date(2021, 3, 1)).toArray());

        List<Sprint> emptyList = Collections.<Sprint>emptyList();
        assertArrayEquals(emptyList.toArray(), sprintRepository.findByEndDate(new Date(2025, 3,1)).toArray());
    }

    @Test
    void givenSprintExists_FindByProject() {
        List<Sprint> sprintList = Arrays.asList(sprint1, sprint2);
        assertArrayEquals(sprintList.toArray(), sprintRepository.findByProject(project).toArray());

        Project project2 = new Project.Builder()
                .projectName("Project 2021")
                .description("Second Attempt")
                .startDate(new Date(2021, 1, 9))
                .endDate(new Date(2021, 11, 20))
                .build();
        projectRepository.save(project2);

        List<Sprint> emptyList = Collections.<Sprint>emptyList();
        assertArrayEquals(emptyList.toArray(), sprintRepository.findByProject(project2).toArray());

        projectRepository.delete(project2);
    }

    @Test
    void givenSprintExists_CountByProject() {
        Project project2 = new Project.Builder()
                .projectName("Project 2021")
                .description("Second Attempt")
                .startDate(new Date(2021, 1, 9))
                .endDate(new Date(2021, 11, 20))
                .build();
        projectRepository.save(project2);

        assertEquals(2, sprintRepository.countByProject(project));

        assertNotEquals(2, sprintRepository.countByProject(project2));

        projectRepository.delete(project2);
    }

    @Test
    void givenEventExists_whenFindByEventRequested_thenAnAppropriateListOfEventsIsReturned() {
       Sprint sprint3 = new Sprint.Builder()
                .sprintLabel("Sprint 3")
                .sprintName("Sprint 3")
                .description("Attempt 3")
                .project(project)
                .startDate(new Date(2022,3,1))
                .endDate(new Date(2022, 6, 1))
                .color(SprintColor.GREEN)
                .build();
        sprintRepository.save(sprint3);

        Event event = new Event.Builder().eventName("This is a test")
                .eventId(1)
                .startDate(new Date(2021, 1,1))
                .endDate(new Date(2021, 3, 6))
                .project(project)
                .build();

        List<Sprint> sprintList = sprintRepository.findSprintsByEvent(event);
        assertArrayEquals(List.of(sprint1, sprint2).toArray(), sprintList.toArray());

    }
}