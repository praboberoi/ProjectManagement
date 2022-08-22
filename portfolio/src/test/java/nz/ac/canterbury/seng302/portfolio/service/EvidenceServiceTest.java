package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Evidence;
import nz.ac.canterbury.seng302.portfolio.model.EvidenceRepository;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Calendar;
import java.util.Date;

/**
 * Test class for the functionality in the evidence service class
 */
@SpringBootTest
public class EvidenceServiceTest {
    @MockBean
    private EvidenceRepository evidenceRepository;
    private EvidenceService evidenceService;
    private Evidence.Builder evidenceBuilder;
    private Project project;
    private Evidence evidence1;

    /**
     * Initialises evidence and project objects
     */
    @BeforeEach
    public void setup() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 1);
        project = new Project.Builder()
                .projectName("Project 2020")
                .description("First Attempt")
                .startDate(new java.sql.Date(Calendar.getInstance().getTimeInMillis()))
                .endDate(new java.sql.Date(c.getTimeInMillis()))
                .build();

        evidence1 = evidenceBuilder.evidenceId(999)
                .title("New evidence")
                .description("This is a new piece of evidence")
                .dateOccurred(new Date())
                .project(project)
                .ownerId(999)
                .build();
    }

}
